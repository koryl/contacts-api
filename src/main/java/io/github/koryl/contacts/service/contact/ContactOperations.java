package io.github.koryl.contacts.service.contact;

import io.github.koryl.contacts.dao.EmailAddressRepository;
import io.github.koryl.contacts.dao.PhoneNumberRepository;
import io.github.koryl.contacts.domain.dto.contact.ContactDto;
import io.github.koryl.contacts.domain.dto.contact.EmailAddressDto;
import io.github.koryl.contacts.domain.dto.contact.PhoneNumberDto;
import io.github.koryl.contacts.domain.entity.contact.Contact;
import io.github.koryl.contacts.domain.entity.contact.EmailAddress;
import io.github.koryl.contacts.domain.entity.contact.PhoneNumber;
import io.github.koryl.contacts.domain.entity.user.User;
import io.github.koryl.contacts.utilities.mapper.ContactMapper;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class ContactOperations {

    private final EmailAddressRepository emailAddressRepository;
    private final PhoneNumberRepository phoneNumberRepository;
    private final ContactMapper contactMapper;

    @Autowired
    public ContactOperations(EmailAddressRepository emailAddressRepository, PhoneNumberRepository phoneNumberRepository) {
        this.emailAddressRepository = emailAddressRepository;
        this.phoneNumberRepository = phoneNumberRepository;
        contactMapper = new ContactMapper();
    }

    @Transactional
    public Contact saveContact(Contact contact) {

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

    public List<ContactDto> buildContactsFromEmailsAndNumbers(List<EmailAddress> emails, List<PhoneNumber> numbers) {

        List<ContactDto> contactEmails = emails.stream()
                .map(email -> new EmailAddressDto(email.getValue()))
                .collect(Collectors.toList());

        List<ContactDto> contactNumbers = numbers.stream()
                .map(number -> new PhoneNumberDto(number.getValue()))
                .collect(Collectors.toList());

        return Stream.of(contactEmails, contactNumbers)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    @Transactional
    public List<ContactDto> getContactsOf(User user) {

        List<ContactDto> contactEmails = contactMapper.mapContactListToContactDtoList(emailAddressRepository.findByUser(user));
        List<ContactDto> contactNumbers = contactMapper.mapContactListToContactDtoList(phoneNumberRepository.findByUser(user));

        return Stream
                .of(contactEmails, contactNumbers)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }
}
