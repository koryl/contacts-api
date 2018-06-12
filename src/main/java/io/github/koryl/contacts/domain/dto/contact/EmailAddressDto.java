package io.github.koryl.contacts.domain.dto.contact;

import io.github.koryl.contacts.domain.ContactType;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
public class EmailAddressDto implements ContactDto {

    @NotNull
    private ContactType contactType = ContactType.EMAIL_ADDRESS;

    @Email
    @NotEmpty
    private String value;

    public EmailAddressDto(@Email String value) {

        this.value = value;
    }
}
