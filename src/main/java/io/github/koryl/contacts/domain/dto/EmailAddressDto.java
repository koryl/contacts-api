package io.github.koryl.contacts.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailAddressDto implements Contact {

    private long id;

    @Email
    private String email;

    private long userId;
}
