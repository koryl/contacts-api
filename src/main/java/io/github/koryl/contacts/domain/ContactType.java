package io.github.koryl.contacts.domain;


import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum ContactType {

    EMAIL_ADDRESS,
    PHONE_NUMBER;
}
