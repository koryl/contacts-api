package io.github.koryl.contacts.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.koryl.contacts.domain.ContactType;
import io.github.koryl.contacts.domain.dto.contact.ContactDto;
import io.github.koryl.contacts.domain.dto.contact.EmailAddressDto;
import io.github.koryl.contacts.domain.dto.contact.PhoneNumberDto;
import io.github.koryl.contacts.service.contact.ContactServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static io.github.koryl.contacts.TestData.EMAIL_ADDRESS_VALUE;
import static io.github.koryl.contacts.TestData.PHONE_NUMBER_VALUE;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(ContactController.class)
public class ContactControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ContactServiceImpl contactService;

    private List<ContactDto> contacts;
    private EmailAddressDto emailAddress;
    private PhoneNumberDto phoneNumber;

    @Before
    public void setUp() {

        emailAddress = new EmailAddressDto(EMAIL_ADDRESS_VALUE);
        phoneNumber = new PhoneNumberDto(PHONE_NUMBER_VALUE);

        contacts = Arrays.asList(
                new EmailAddressDto(EMAIL_ADDRESS_VALUE),
                new PhoneNumberDto(PHONE_NUMBER_VALUE)
        );
    }

    @Test
    public void shouldReturnAllContactsOfUser() throws Exception {

        when(contactService.getContactsOfUser(1L)).thenReturn(contacts);

        mockMvc.perform(get("/users/{id}/contacts", 1)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].contactType", is(ContactType.EMAIL_ADDRESS.name())))
                .andExpect(jsonPath("$[0].value", is(EMAIL_ADDRESS_VALUE)))
                .andExpect(jsonPath("$[1].contactType", is(ContactType.PHONE_NUMBER.name())))
                .andExpect(jsonPath("$[1].value", is(PHONE_NUMBER_VALUE)));
    }

    @Test
    public void shouldReturnNewContactAfterAddingEmailAddress() throws Exception {

        String jsonEmail = objectMapper.writeValueAsString(emailAddress);
        when(contactService.createNewContact(1L, new EmailAddressDto(EMAIL_ADDRESS_VALUE))).thenReturn(emailAddress);

        mockMvc.perform(post("/users/{id}/contacts", 1)
                .content(jsonEmail)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.contactType", is(ContactType.EMAIL_ADDRESS.name())))
                .andExpect(jsonPath("$.value", is(EMAIL_ADDRESS_VALUE)));
    }

    @Test
    public void shouldReturnNewContactAfterAddingPhoneNumber() throws Exception {

        String jsonPhone = objectMapper.writeValueAsString(phoneNumber);
        when(contactService.createNewContact(1L, new PhoneNumberDto(PHONE_NUMBER_VALUE))).thenReturn(phoneNumber);

        mockMvc.perform(post("/users/{id}/contacts", 1)
                .content(jsonPhone)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.contactType", is(ContactType.PHONE_NUMBER.name())))
                .andExpect(jsonPath("$.value", is(PHONE_NUMBER_VALUE)));
    }

    @Test
    public void shouldReturnUpdatedContactAfterUpdatingEmailAddress() throws Exception {

        String updatedEmailValue = "updated@test.com";
        EmailAddressDto updatedEmailAddress = new EmailAddressDto(updatedEmailValue);
        String jsonEmail = objectMapper.writeValueAsString(updatedEmailAddress);

        when(contactService.updateContact(1L, EMAIL_ADDRESS_VALUE, updatedEmailAddress)).thenReturn(updatedEmailAddress);

        mockMvc.perform(put("/users/{id}/contacts/updateContact", 1)
                .param("value", EMAIL_ADDRESS_VALUE)
                .content(jsonEmail)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.contactType", is(ContactType.EMAIL_ADDRESS.name())))
                .andExpect(jsonPath("$.value", is(updatedEmailValue)));
    }

    @Test
    public void shouldReturnUpdatedContactAfterUpdatingPhoneNumber() throws Exception {

        String updatedPhoneNumberValue = "987654321";
        PhoneNumberDto updatedPhoneNumber = new PhoneNumberDto(updatedPhoneNumberValue);
        String jsonPhoneNumber = objectMapper.writeValueAsString(updatedPhoneNumber);

        when(contactService.updateContact(1L, PHONE_NUMBER_VALUE, updatedPhoneNumber)).thenReturn(updatedPhoneNumber);

        mockMvc.perform(put("/users/{id}/contacts/updateContact", 1)
                .param("value", PHONE_NUMBER_VALUE)
                .content(jsonPhoneNumber)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.contactType", is(ContactType.PHONE_NUMBER.name())))
                .andExpect(jsonPath("$.value", is(updatedPhoneNumberValue)));
    }

    @Test
    public void shouldReturnStatusOkAfterDeleteContact() throws Exception {

        mockMvc.perform(delete("/users/{id}/contacts/deleteContact", 1)
                .param("value", EMAIL_ADDRESS_VALUE))
                .andExpect(status().isOk());
    }
}
