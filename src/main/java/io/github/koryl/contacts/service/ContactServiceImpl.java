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
import io.github.koryl.contacts.utilities.mapper.ContactMapper;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
public class ContactServiceImpl implements ContactService {

    private final EmailAddressRepository emailAddressRepository;
    private final PhoneNumberRepository phoneNumberRepository;
    private final UserRepository userRepository;
    private final ContactFactory contactFactory;
    private final ContactDtoFactory contactDtoFactory;
    private final ContactMapper contactMapper;

    @Autowired
    public ContactServiceImpl(UserRepository userRepository, EmailAddressRepository emailAddressRepository, PhoneNumberRepository phoneNumberRepository, ContactFactory contactFactory, ContactDtoFactory contactDtoFactory, ContactMapper contactMapper) {

        this.userRepository = userRepository;
        this.emailAddressRepository = emailAddressRepository;
        this.phoneNumberRepository = phoneNumberRepository;
        this.contactFactory = contactFactory;
        this.contactDtoFactory = contactDtoFactory;
        this.contactMapper = contactMapper;
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
        Contact contact = contactFactory.getContact(contactDto.getContactType());
        contact.setValue(contactDto.getValue());
        contact.setUser(user);
        contact = saveContact(contact);

        ContactDto createdContact = contactDtoFactory.getContactDto(contactDto.getContactType());
        createdContact.setValue(contact.getValue());
        createdContact.setUserId(contact.getUser().getId());

        return createdContact;
    }

    public ContactDto updateContact(Long id, String value, ContactDto contactDto) {

        if (!userRepository.findById(id).isPresent()) {
            throw new ResourceNotFoundException("User with id: " + id + " not found.");
        }
        Contact contact;

        switch (contactDto.getContactType()) {
            case EMAIL_ADDRESS:
                contact = emailAddressRepository.findByValue(value)
                        .orElseThrow(() -> new ResourceNotFoundException("Provided email was not found - nothing to update."));
                break;
            case PHONE_NUMBER:
                contact = phoneNumberRepository.findByValue(value)
                        .orElseThrow(() -> new ResourceNotFoundException("Provided phone number was not found - nothing to update."));
                break;
            default:
                throw new RuntimeException("Unknown error occurred - check if contact type is correct.");
        }
        contact.setValue(contactDto.getValue());
        contact = saveContact(contact);

        ContactDto updatedContact = contactDtoFactory.getContactDto(contactDto.getContactType());
        updatedContact.setValue(contact.getValue());
        updatedContact.setUserId(contact.getUser().getId());

        return updatedContact;
    }

    public void deleteContact(Long id, String value) {

        if (!userRepository.findById(id).isPresent()) {
            throw new ResourceNotFoundException("User with id: " + id + " not found.");
        }

        Optional<? extends Contact> opContact =
                Stream.of(emailAddressRepository.findByValue(value), phoneNumberRepository.findByValue(value))
                        .filter(Optional::isPresent)
                        .findFirst()
                        .orElseThrow(() -> new ResourceNotFoundException("Provided contact was not found - nothing to delete."));

        if (opContact.isPresent() && opContact.get() instanceof EmailAddress) {
            EmailAddress email = (EmailAddress) opContact.get();
            emailAddressRepository.delete(email);
        } else if (opContact.isPresent() && opContact.get() instanceof PhoneNumber) {
            PhoneNumber number = (PhoneNumber) opContact.get();
            phoneNumberRepository.delete(number);
        }
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

    private Contact saveContact(Contact contact) {

        try {
            if (contact instanceof EmailAddress) {
                EmailAddress emailAddress = (EmailAddress) contact;
                return emailAddressRepository.save(emailAddress);

            } else if (contact instanceof PhoneNumber) {
                PhoneNumber phoneNumber = (PhoneNumber) contact;
                return phoneNumberRepository.save(phoneNumber);

            } else {
                throw new RuntimeException("It was not possible to save contact in the repository. Check if type of contact is correct.");
            }
        } catch (ConstraintViolationException | DataIntegrityViolationException e) {
            throw new RuntimeException("Provided contact already exist - cannot save contact.");
        }
    }
}
