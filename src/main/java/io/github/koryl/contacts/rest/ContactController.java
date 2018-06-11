package io.github.koryl.contacts.rest;

import io.github.koryl.contacts.domain.dto.contact.ContactDto;
import io.github.koryl.contacts.service.contact.ContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/users")
public class ContactController {

    private final ContactService contactService;

    @Autowired
    public ContactController(ContactService contactService) {
        this.contactService = contactService;
    }

    @GetMapping("/{id}/contacts")
    public List<ContactDto> getContactsOfUser(@PathVariable("id") Long id) {

        return contactService.getContactsOfUser(id);
    }

    @PostMapping("/{id}/contacts")
    @ResponseStatus(HttpStatus.CREATED)
    public ContactDto addNewContactForUser(@PathVariable("id") Long id, @Valid @RequestBody ContactDto contactDto) {

        return contactService.createNewContact(id, contactDto);
    }

    @PutMapping("/{id}/contacts/updateContact")
    public ContactDto updateContactOfUser(@PathVariable("id") Long id,
                                           @RequestParam String value,
                                           @Valid @RequestBody ContactDto contactDto) {

        return contactService.updateContact(id, value, contactDto);
    }

    @DeleteMapping("/{id}/contacts/deleteContact")
    @ResponseStatus(HttpStatus.OK)
    public void deleteContactOfUser(@PathVariable("id") Long id, @RequestParam String value) {

        contactService.deleteContact(id, value);
    }
}
