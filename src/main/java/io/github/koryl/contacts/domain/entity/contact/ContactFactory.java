package io.github.koryl.contacts.domain.entity.contact;

import io.github.koryl.contacts.domain.ContactType;
import io.github.koryl.contacts.domain.entity.user.User;
import org.springframework.stereotype.Component;

@Component
public class ContactFactory {

    public Contact getContact(ContactType contactType, String value, User user) {

        switch (contactType) {
            case EMAIL_ADDRESS:
                return new EmailAddress(0, value, user);
            case PHONE_NUMBER:
                return new PhoneNumber(0, value, user);
            default:
                throw new RuntimeException("Cannot create Contact of the provided type.");
        }
    }
}
