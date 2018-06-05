package io.github.koryl.contacts.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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


    public static ContactDtoBuilder builder() {
        return new ContactDtoBuilder();
    }

    public static class ContactDtoBuilder {
        private long id;
        private @NotNull ContactType contactType;
        private @NotEmpty String contactValue;
        private Long userId;

        ContactDtoBuilder() {
        }

        public ContactDtoBuilder id(long id) {
            this.id = id;
            return this;
        }

        public ContactDtoBuilder contactType(@NotNull ContactType contactType) {
            this.contactType = contactType;
            return this;
        }

        public ContactDtoBuilder contactValue(@NotEmpty String contactValue) {
            this.contactValue = contactValue;
            return this;
        }

        public ContactDtoBuilder userId(Long userId) {
            this.userId = userId;
            return this;
        }

        public ContactDto build() {
            return new ContactDto(id, contactType, contactValue, userId);
        }
    }
}
