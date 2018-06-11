package io.github.koryl.contacts.service;

import io.github.koryl.contacts.domain.dto.contact.ContactDto;

import java.util.List;

public interface ContactService {

    List<ContactDto> getContactsOfUser(Long id);

    ContactDto createNewContact(Long id, ContactDto contactDto);

    ContactDto updateContact(Long id, String value, ContactDto contactDto);

    void deleteContact(Long id, String value);
}
