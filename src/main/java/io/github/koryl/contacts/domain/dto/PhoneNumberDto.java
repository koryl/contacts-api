package io.github.koryl.contacts.domain.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PhoneNumberDto implements Contact {

    private long id;

    @NotEmpty
    @Pattern(regexp = "[0-9]+")
    private String phone_number;

    private long userId;
}
