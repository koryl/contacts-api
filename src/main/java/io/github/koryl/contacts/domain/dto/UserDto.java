package io.github.koryl.contacts.domain.dto;

import io.github.koryl.contacts.domain.entity.Contact;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
@NoArgsConstructor
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
}
