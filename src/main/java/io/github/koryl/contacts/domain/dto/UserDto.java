package io.github.koryl.contacts.domain.dto;

import io.github.koryl.contacts.domain.entity.Contact;
import io.github.koryl.contacts.validation.InCorrectDateRange;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.pl.PESEL;

import javax.validation.constraints.*;
import java.time.LocalDate;
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

    @InCorrectDateRange
    private LocalDate birthDate;

    @PESEL
    private String pesel;

    private List<Contact> contacts;
}
