package io.github.koryl.contacts.domain.dto;

import io.github.koryl.contacts.validation.Gender;
import io.github.koryl.contacts.validation.InCorrectDateRange;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.pl.PESEL;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    private long userId;

    @NotEmpty
    @Size(max = 50)
    private String firstName;

    @NotEmpty
    @Size(max = 50)
    private String lastName;

    @Gender
    private char gender;

    @InCorrectDateRange
    private LocalDate birthDate;

    @PESEL
    private String pesel;
}
