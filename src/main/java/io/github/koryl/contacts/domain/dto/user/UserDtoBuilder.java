package io.github.koryl.contacts.domain.dto.user;

import io.github.koryl.contacts.domain.dto.contact.ContactDto;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

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

        if (id > 0) {
            this.id = id;
        }
        return this;
    }

    public UserDtoBuilder firstName(String firstName) {

        if (!Objects.equals(firstName, "") && Objects.nonNull(firstName)) {
            this.firstName = firstName.trim();
        }
        return this;
    }

    public UserDtoBuilder lastName(String lastName) {

        if (!Objects.equals(lastName, "") && Objects.nonNull(lastName)) {
            this.lastName = lastName.trim();
        }
        return this;
    }

    public UserDtoBuilder gender(char gender) {

        if (Character.isLetter(gender) && Character.isUpperCase(gender)) {
            this.gender = gender;
        }

        return this;
    }

    public UserDtoBuilder birthDate(LocalDate birthDate) {

        if (Objects.nonNull(birthDate)) {
            this.birthDate = birthDate;
        }

        return this;
    }

    public UserDtoBuilder pesel(String pesel) {

        if (!Objects.equals(pesel, "") && Objects.nonNull(pesel)) {
            this.pesel = pesel.trim();
        }
        return this;
    }

    public UserDtoBuilder contacts(List<ContactDto> contactList) {

        if (nonNull(contactList)) {
            this.contacts = contactList;
        }

        return this;
    }

    public UserDto createUser() {

        return new UserDto(id, firstName, lastName, gender, birthDate, pesel, contacts);
    }
}
