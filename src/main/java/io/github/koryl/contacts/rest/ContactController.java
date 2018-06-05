package io.github.koryl.contacts.rest;

import io.github.koryl.contacts.domain.dto.ContactDto;
import io.github.koryl.contacts.service.ContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/users/{id}/contacts")
public class ContactController {

    private final ContactService contactService;

    @Autowired
    public ContactController(ContactService contactService) {
        this.contactService = contactService;
    }

    @PostMapping
    public List<ContactDto> addContactToUser(@PathVariable("id") Long id) {

        return contactService.getContactsOfUser(id);
    }
}
