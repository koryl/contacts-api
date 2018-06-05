package io.github.koryl.contacts.domain.dto;

import io.github.koryl.contacts.domain.entity.ContactType;
import lombok.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContactDto {

    private long id;

    @NotNull
    private ContactType contactType;

    @NotEmpty
    private String contactValue;

    private Long userId;


    public ContactBuilder builder() {

        return new ContactBuilder();
    }

    public ContactDto(@NotNull ContactType contactType, @NotEmpty String contactValue) {
        this.contactType = contactType;
        this.contactValue = contactValue;
    }
}
