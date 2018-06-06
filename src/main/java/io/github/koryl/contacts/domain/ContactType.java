package io.github.koryl.contacts.domain;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize(using = ContactTypeSerializer.class)
public enum ContactType {

    EMAIL_ADDRESS("EmailAddress"),
    PHONE_NUMBER("PhoneNumber");

    private String contactType;

    ContactType(String type) {

        contactType = type;
    }

    public String toString() {

        return contactType;
    }
}
