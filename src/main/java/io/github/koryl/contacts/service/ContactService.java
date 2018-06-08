package io.github.koryl.contacts.service;

import io.github.koryl.contacts.dao.EmailAddressRepository;
import io.github.koryl.contacts.dao.PhoneNumberRepository;
import io.github.koryl.contacts.dao.UserRepository;
import io.github.koryl.contacts.domain.dto.contact.*;
import io.github.koryl.contacts.domain.entity.contact.Contact;
import io.github.koryl.contacts.domain.entity.contact.ContactFactory;
import io.github.koryl.contacts.domain.entity.contact.EmailAddress;
import io.github.koryl.contacts.domain.entity.contact.PhoneNumber;
import io.github.koryl.contacts.domain.entity.user.User;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
public class ContactService {

    private final EmailAddressRepository emailAddressRepository;
    private final PhoneNumberRepository phoneNumberRepository;
    private final UserRepository userRepository;
    private final ContactFactory contactFactory;
    private final ContactDtoFactory contactDtoFactory;

    @Autowired
    public ContactService(UserRepository userRepository, EmailAddressRepository emailAddressRepository, PhoneNumberRepository phoneNumberRepository, ContactFactory contactFactory, ContactDtoFactory contactDtoFactory) {

        this.userRepository = userRepository;
        this.emailAddressRepository = emailAddressRepository;
        this.phoneNumberRepository = phoneNumberRepository;
        this.contactFactory = contactFactory;
        this.contactDtoFactory = contactDtoFactory;
    }

    public List<ContactDto> getContactsOfUser(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id: " + id + " not found."));
        List<EmailAddress> rawEmails = emailAddressRepository.findByUser(user);
        List<PhoneNumber> rawNumbers = phoneNumberRepository.findByUser(user);


        return buildContactsFromEmailsAndNumbers(rawEmails, rawNumbers);
    }

    public ContactDto createNewContact(Long id, ContactDto contactDto) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id: " + id + " not found."));

        contactDto.setUserId(user.getId());
        Contact contact = saveContact(contactDto, user);

        ContactDto createdContact = contactDtoFactory.getContactDto(contactDto.getContactType());
        createdContact.setValue(contact.getValue());
        createdContact.setUserId(contact.getUser().getId());

        return createdContact;
    }

    private List<ContactDto> buildContactsFromEmailsAndNumbers(List<EmailAddress> emails, List<PhoneNumber> numbers) {

        List<ContactDto> contactEmails = emails.stream()
                .map(email -> new EmailAddressDto(email.getValue(), email.getUser().getId()))
                .collect(Collectors.toList());

        List<ContactDto> contactNumbers = numbers.stream()
                .map(number -> new PhoneNumberDto(number.getValue(), number.getUser().getId()))
                .collect(Collectors.toList());

        return Stream.of(contactEmails, contactNumbers)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    private Contact saveContact(ContactDto contactDto, User user) {

        Contact contact = contactFactory.getContact(contactDto.getContactType());
        contact.setValue(contactDto.getValue());
        contact.setUser(user);

        try {
            switch (contactDto.getContactType()) {
                case EMAIL_ADDRESS:
                    EmailAddress emailAddress = (EmailAddress) contact;
                    return emailAddressRepository.save(emailAddress);
                case PHONE_NUMBER:
                    PhoneNumber phoneNumber = (PhoneNumber) contact;
                    return phoneNumberRepository.save(phoneNumber);
                default:
                    throw new RuntimeException("It was not possible to save contact in the repository.");
            }
        } catch (ConstraintViolationException | DataIntegrityViolationException e) {
            throw new RuntimeException("Provided contact already exist.");
        }
    }
}
