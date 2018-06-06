package io.github.koryl.contacts.domain.dto;

import io.github.koryl.contacts.domain.dto.contact.ContactDto;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.*;

@Component
public class UserDtoBuilder {

    private UserDto userDto;

    public UserDtoBuilder() {

        userDto = new UserDto();
    }

    public UserDtoBuilder userId(long id) {

        userDto.setUserId(id);
        return this;
    }

    public UserDtoBuilder firstName(String firstName) {

        userDto.setFirstName(firstName);
        return this;
    }

    public UserDtoBuilder lastName(String lastName) {

        userDto.setLastName(lastName);
        return this;
    }

    public UserDtoBuilder gender(char gender) {

        userDto.setGender(gender);
        return this;
    }

    public UserDtoBuilder birthDate(LocalDate birthDate) {

        userDto.setBirthDate(birthDate);
        return this;
    }

    public UserDtoBuilder pesel(String pesel) {

        userDto.setPesel(pesel);
        return this;
    }

    public UserDtoBuilder contacts(List<ContactDto> contacts) {

        if (isNull(contacts)) {
            contacts = new ArrayList<>();
        }
        userDto.setContacts(contacts);
        return this;
    }

    public UserDto build() {

        return this.userDto;
    }
}
