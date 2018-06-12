package io.github.koryl.contacts.service.contact;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
@Slf4j
public class ContactServiceImpl implements ContactService {

    private final EmailAddressRepository emailAddressRepository;
    private final PhoneNumberRepository phoneNumberRepository;
    private final UserRepository userRepository;
    private final ContactOperations contactOperations;
    private final ContactFactory contactFactory;
    private final ContactDtoFactory contactDtoFactory;

    @Autowired
    public ContactServiceImpl(UserRepository userRepository, EmailAddressRepository emailAddressRepository, PhoneNumberRepository phoneNumberRepository, ContactFactory contactFactory, ContactDtoFactory contactDtoFactory, ContactOperations contactOperations) {

        this.userRepository = userRepository;
        this.emailAddressRepository = emailAddressRepository;
        this.phoneNumberRepository = phoneNumberRepository;
        this.contactFactory = contactFactory;
        this.contactDtoFactory = contactDtoFactory;
        this.contactOperations = contactOperations;
    }

    @Transactional
    public List<ContactDto> getContactsOfUser(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id: " + id + " not found."));
        List<EmailAddress> rawEmails = emailAddressRepository.findByUser(user);
        List<PhoneNumber> rawNumbers = phoneNumberRepository.findByUser(user);

        return contactOperations.buildContactsFromEmailsAndNumbers(rawEmails, rawNumbers);
    }

    @Transactional
    public ContactDto createNewContact(Long id, ContactDto contactDto) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id: " + id + " not found."));

        Contact contact = contactFactory.getContact(contactDto.getContactType(), contactDto.getValue(), user);
        contact = contactOperations.saveContact(contact);

        ContactDto createdContact = contactDtoFactory.getContactDto(contactDto.getContactType(), contactDto.getValue());
        createdContact.setValue(contact.getValue());

        return createdContact;
    }

    @Transactional
    public ContactDto updateContact(Long id, String value, ContactDto contactDto) {

        userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id: " + id + " not found."));

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
        contact = contactOperations.saveContact(contact);

        ContactDto updatedContact = contactDtoFactory.getContactDto(contactDto.getContactType(), contactDto.getValue());
        updatedContact.setValue(contact.getValue());

        return updatedContact;
    }

    @Transactional
    public void deleteContact(Long id, String value) {

        userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id: " + id + " not found."));

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
}
