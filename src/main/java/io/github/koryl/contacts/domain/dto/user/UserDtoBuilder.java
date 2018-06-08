package io.github.koryl.contacts.domain.dto.user;

import io.github.koryl.contacts.domain.dto.contact.ContactDto;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.*;

@Component
public class UserDtoBuilder {

    private long id;
    private String firstName;
    private String lastName;
    private char gender;
    private LocalDate birthDate;
    private String pesel;
    private List<ContactDto> contacts;

    public UserDtoBuilder() {
    }

    public UserDtoBuilder id(long id) {

        this.id = id;
        return this;
    }

    public UserDtoBuilder firstName(String firstName) {

        this.firstName = firstName;
        return this;
    }

    public UserDtoBuilder lastName(String lastName) {

        this.lastName = lastName;
        return this;
    }

    public UserDtoBuilder gender(char gender) {

        this.gender = gender;
        return this;
    }

    public UserDtoBuilder birthDate(LocalDate birthDate) {

        this.birthDate = birthDate;
        return this;
    }

    public UserDtoBuilder pesel(String pesel) {

        this.pesel = pesel;
        return this;
    }

    public UserDtoBuilder contacts(List<ContactDto> contactList) {

        if (isNull(contactList)) {
            this.contacts = new ArrayList<>();
        } else {
            this.contacts = contactList;
        }
        return this;
    }

    public UserDto createUser() {

        return new UserDto(id, firstName, lastName, gender, birthDate, pesel, contacts);
    }
}
