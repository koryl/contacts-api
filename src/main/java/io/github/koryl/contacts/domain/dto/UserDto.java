package io.github.koryl.contacts.domain.dto;

import io.github.koryl.contacts.domain.entity.Contact;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
@AllArgsConstructor
public class UserDto {

    private long userId;

    @NotEmpty
    private String firstName;

    @NotEmpty
    private String lastName;

    @NotEmpty
    private String gender;

    @NotEmpty
    private String birthDate;

    @NotEmpty
    private String pesel;

    private List<Contact> contacts;

    public UserDto(@NotEmpty String firstName, @NotEmpty String lastName, @NotEmpty String gender, @NotEmpty String birthDate, @NotEmpty String pesel, List<Contact> contacts) {

        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.birthDate = birthDate;
        this.pesel = pesel;
        this.contacts = contacts;
    }
}
