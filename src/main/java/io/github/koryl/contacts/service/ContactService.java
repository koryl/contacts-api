package io.github.koryl.contacts.service;

import io.github.koryl.contacts.dao.ContactRepository;
import io.github.koryl.contacts.domain.dto.ContactDto;
import io.github.koryl.contacts.domain.entity.Contact;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ContactService {

    private final ContactRepository contactRepository;

    @Autowired
    public ContactService(ContactRepository contactRepository) {

        this.contactRepository = contactRepository;
    }

    public List<ContactDto> getContactsOfUser(Long id) {

        List<Contact> rawContacts = contactRepository.findByUserId(id);
        return new ArrayList<>();
    }
}
