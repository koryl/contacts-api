package io.github.koryl.contacts.utilities.mapper;

import io.github.koryl.contacts.domain.dto.contact.ContactDto;
import io.github.koryl.contacts.domain.dto.contact.ContactDtoFactory;
import io.github.koryl.contacts.domain.dto.contact.EmailAddressDto;
import io.github.koryl.contacts.domain.entity.contact.Contact;
import io.github.koryl.contacts.domain.entity.contact.ContactFactory;
import io.github.koryl.contacts.domain.entity.contact.EmailAddress;
import io.github.koryl.contacts.domain.entity.contact.PhoneNumber;
import io.github.koryl.contacts.domain.entity.user.User;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

import static io.github.koryl.contacts.domain.ContactType.EMAIL_ADDRESS;
import static io.github.koryl.contacts.domain.ContactType.PHONE_NUMBER;

@Component
public class ContactMapper {

    public List<ContactDto> mapContactListToContactDtoList(List<? extends Contact> contactList) {

        ContactDtoFactory contactDtoFactory = new ContactDtoFactory();

        return contactList
                .stream()
                .map(contact -> {
                    String value = contact.getValue();
                    if (contact instanceof EmailAddress) {
                        return contactDtoFactory.getContactDto(EMAIL_ADDRESS, value);
                    } else if (contact instanceof PhoneNumber) {
                        return contactDtoFactory.getContactDto(PHONE_NUMBER, value);
                    } else {
                        throw new RuntimeException("Cannot map provided contact list - unknown type of contacts.");
                    }
                })
                .collect(Collectors.toList());
    }

    public List<Contact> mapContactDtoListToContactList(List<? extends ContactDto> contactDtoList, User user) {

        ContactFactory contactFactory = new ContactFactory();

        return contactDtoList
                .stream()
                .map(contactDto -> {
                    String value = contactDto.getValue();
                    if (contactDto instanceof EmailAddressDto) {
                        return contactFactory.getContact(EMAIL_ADDRESS, value, user);
                    } else if (contactDto instanceof PhoneNumber) {
                        return contactFactory.getContact(PHONE_NUMBER, value, user);
                    } else {
                        throw new RuntimeException("Cannot map provided contact list - unknown type of contacts.");
                    }
                })
                .collect(Collectors.toList());
    }
}
