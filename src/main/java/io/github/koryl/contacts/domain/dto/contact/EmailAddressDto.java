package io.github.koryl.contacts.domain.dto.contact;

import com.fasterxml.jackson.annotation.JsonTypeName;
import io.github.koryl.contacts.domain.ContactType;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Data
@JsonTypeName
@NoArgsConstructor
public class EmailAddressDto implements ContactDto {

    @NotNull
    private ContactType contactType = ContactType.EMAIL_ADDRESS;

    @Email
    private String value;

    @NotNull
    private long userId;

    public EmailAddressDto(@Email String value, long userId) {

        this.value = value;
        this.userId = userId;
    }
}
