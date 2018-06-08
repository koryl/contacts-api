package io.github.koryl.contacts.domain.entity.contact;

import io.github.koryl.contacts.domain.ContactType;
import org.springframework.stereotype.Component;

@Component
public class ContactFactory {

    public Contact getContact(ContactType contactType) {

        switch (contactType) {
            case EMAIL_ADDRESS:
                return new EmailAddress();
            case PHONE_NUMBER:
                return new PhoneNumber();
            default:
                throw new RuntimeException("Cannot create Contact of the provided type.");
        }
    }
}
