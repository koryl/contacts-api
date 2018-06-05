package io.github.koryl.contacts.service;

import io.github.koryl.contacts.dao.ContactRepository;
import io.github.koryl.contacts.dao.EmailAddressRepository;
import io.github.koryl.contacts.dao.UserRepository;
import io.github.koryl.contacts.domain.dto.ContactDto;
import io.github.koryl.contacts.domain.entity.Contact;
import io.github.koryl.contacts.domain.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ContactService {

    private final ContactRepository contactRepository;
    private final EmailAddressRepository emailAddressRepository;
    private final UserRepository userRepository;

    @Autowired
    public ContactService(ContactRepository contactRepository, UserRepository userRepository, EmailAddressRepository emailAddressRepository) {

        this.contactRepository = contactRepository;
        this.userRepository = userRepository;
        this.emailAddressRepository = emailAddressRepository;
    }

    public List<ContactDto> getContactsOfUser(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id: " + id + " not found."));
        List<Contact> rawContacts = contactRepository.findByUser(user);

        return rawContacts
                .stream()
                .map(e ->
                        ContactDto.builder()
                                .id(e.getId())
                                .contactType(e.getContactType())
                                .contactValue(e.getContactValue())
                                .userId(e.getUser().getId())
                                .build())
                .collect(Collectors.toList());
    }

    public ContactDto createNewContact(Long id, ContactDto contactDto) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id: " + id + " not found."));

        Contact contact = new Contact();
        contact.setContactType(contactDto.getContactType());
        contact.setContactValue(contactDto.getContactValue());
        contact.setUser(user);

        Contact savedContact = contactRepository.save(contact);

        return ContactDto.builder()
                .id(savedContact.getId())
                .contactType(savedContact.getContactType())
                .contactValue(savedContact.getContactValue())
                .userId(savedContact.getUser().getId())
                .build();
    }
}
