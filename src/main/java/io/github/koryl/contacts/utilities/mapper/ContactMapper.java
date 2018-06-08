package io.github.koryl.contacts.utilities.mapper;

import io.github.koryl.contacts.domain.dto.contact.ContactDto;
import io.github.koryl.contacts.domain.dto.contact.EmailAddressDto;
import io.github.koryl.contacts.domain.dto.contact.PhoneNumberDto;
import io.github.koryl.contacts.domain.entity.contact.Contact;
import io.github.koryl.contacts.domain.entity.contact.EmailAddress;
import io.github.koryl.contacts.domain.entity.contact.PhoneNumber;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ContactMapper {

    private final ModelMapper modelMapper;

    @Autowired
    public ContactMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public List<ContactDto> mapContactListToContactDtoList(List<? extends Contact> contactList) {

        return contactList
                .stream()
                .map(contact -> {
                    if (contact instanceof EmailAddress) {
                        return modelMapper.map(contact, EmailAddressDto.class);
                    } else if (contact instanceof PhoneNumber) {
                        return modelMapper.map(contact, PhoneNumberDto.class);
                    } else {
                        throw new RuntimeException("Cannot map provided contact list - unknown type of contacts");
                    }
                })
                .collect(Collectors.toList());
    }
}
