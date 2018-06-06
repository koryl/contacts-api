package io.github.koryl.contacts.domain.dto.contact;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.github.koryl.contacts.domain.ContactType;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "contactType")
@JsonSubTypes({
        @JsonSubTypes.Type(value = EmailAddressDto.class,  name = "EmailAddress"),
        @JsonSubTypes.Type(value = PhoneNumberDto.class, name = "PhoneNumber"),
})
public interface ContactDto {

    ContactType getContactType();

    String getValue();

    long getUserId();

    void setContactType(ContactType contactType);

    void setValue(String value);

    void setUserId(long userId);
}
