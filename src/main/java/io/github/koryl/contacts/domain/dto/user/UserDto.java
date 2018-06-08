package io.github.koryl.contacts.domain.dto.user;

import io.github.koryl.contacts.domain.dto.contact.ContactDto;
import io.github.koryl.contacts.utilities.validation.Gender;
import io.github.koryl.contacts.utilities.validation.InCorrectDateRange;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.pl.PESEL;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    private long id;

    @NotEmpty
    @Size(max = 50)
    private String firstName;

    @NotEmpty
    @Size(max = 50)
    private String lastName;

    @NotNull
    @Gender
    private char gender;

    @NotNull
    @InCorrectDateRange
    private LocalDate birthDate;

    @NotNull
    @PESEL
    private String pesel;

    private List<ContactDto> contacts = new ArrayList<>();
}
