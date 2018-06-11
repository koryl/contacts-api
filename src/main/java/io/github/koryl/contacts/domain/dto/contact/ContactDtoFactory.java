package io.github.koryl.contacts.domain.dto.contact;

import io.github.koryl.contacts.domain.ContactType;
import org.springframework.stereotype.Component;

@Component
public class ContactDtoFactory {

    public ContactDto getContactDto(ContactType contactType, String value) {

        switch (contactType) {
            case EMAIL_ADDRESS:
                return new EmailAddressDto(value);
            case PHONE_NUMBER:
                return new PhoneNumberDto(value);
            default:
                throw new RuntimeException("Cannot create ContactDto of the provided type.");
        }
    }
}
