package io.github.koryl.contacts.domain.dto.contact;

import com.fasterxml.jackson.annotation.JsonTypeName;
import io.github.koryl.contacts.domain.ContactType;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
@JsonTypeName
@NoArgsConstructor
public class PhoneNumberDto implements ContactDto {

    @NotNull
    private ContactType contactType = ContactType.PHONE_NUMBER;

    @NotEmpty
    @Pattern(regexp = "[0-9]+")
    private String value;

    @NotNull
    private long userId;

    public PhoneNumberDto(@NotEmpty @Pattern(regexp = "[0-9]+") String value, long userId) {

        this.value = value;
        this.userId = userId;
    }
}
