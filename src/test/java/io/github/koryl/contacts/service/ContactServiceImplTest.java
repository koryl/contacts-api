package io.github.koryl.contacts.service;

import io.github.koryl.contacts.dao.EmailAddressRepository;
import io.github.koryl.contacts.dao.PhoneNumberRepository;
import io.github.koryl.contacts.dao.UserRepository;
import io.github.koryl.contacts.domain.ContactType;
import io.github.koryl.contacts.domain.dto.contact.ContactDto;
import io.github.koryl.contacts.domain.dto.contact.ContactDtoFactory;
import io.github.koryl.contacts.domain.dto.contact.EmailAddressDto;
import io.github.koryl.contacts.domain.dto.contact.PhoneNumberDto;
import io.github.koryl.contacts.domain.entity.contact.ContactFactory;
import io.github.koryl.contacts.domain.entity.contact.EmailAddress;
import io.github.koryl.contacts.domain.entity.contact.PhoneNumber;
import io.github.koryl.contacts.domain.entity.user.User;

import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Optional;

import static io.github.koryl.contacts.TestData.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

public class ContactServiceImplTest {

    private ContactService contactService;
    private EmailAddressRepository emailAddressRepository;
    private PhoneNumberRepository phoneNumberRepository;

    private User testUser;
    private EmailAddress emailAddress;
    private PhoneNumber phoneNumber;


    @Before
    public void setUp() {

        UserRepository userRepository = mock(UserRepository.class);
        emailAddressRepository = mock(EmailAddressRepository.class);
        phoneNumberRepository = mock(PhoneNumberRepository.class);
        ContactFactory contactFactory = new ContactFactory();
        ContactDtoFactory contactDtoFactory = new ContactDtoFactory();
        contactService = new ContactServiceImpl(userRepository, emailAddressRepository, phoneNumberRepository, contactFactory, contactDtoFactory);

        testUser = new User(1, FIRST_NAME, LAST_NAME, GENDER, BIRTH_DATE, PESEL);
        emailAddress = new EmailAddress(0, EMAIL_ADDRESS_VALUE, testUser);
        phoneNumber = new PhoneNumber(0, PHONE_NUMBER_VALUE, testUser);


        given(userRepository.findById(1L)).willReturn(Optional.of(testUser));
    }

    @Test
    public void shouldGetAllContactsOfSpecificUserFoundById() {

        when(emailAddressRepository.findByUser(testUser)).thenReturn(Lists.list(emailAddress));
        when(phoneNumberRepository.findByUser(testUser)).thenReturn(Lists.list(phoneNumber));

        List<ContactDto> contacts = contactService.getContactsOfUser(1L);

        assertThat(contacts)
                .hasSize(2)
                .hasAtLeastOneElementOfType(EmailAddressDto.class)
                .hasAtLeastOneElementOfType(PhoneNumberDto.class)
                .contains(new EmailAddressDto(EMAIL_ADDRESS_VALUE))
                .contains(new PhoneNumberDto(PHONE_NUMBER_VALUE));
    }

    @Test
    public void shouldCorrectlyReturnEmailAddressDtoAsContactDtoAfterAddingContact() {

        when(emailAddressRepository.save(emailAddress)).thenReturn(emailAddress);

        ContactDto emailDto = new EmailAddressDto(EMAIL_ADDRESS_VALUE);

        ContactDto contactDto = contactService.createNewContact(1L, emailDto);

        assertThat(contactDto)
                .isInstanceOf(EmailAddressDto.class)
                .hasFieldOrPropertyWithValue("value", EMAIL_ADDRESS_VALUE)
                .hasFieldOrPropertyWithValue("contactType", ContactType.EMAIL_ADDRESS);
    }

    @Test
    public void shouldCorrectlyReturnPhoneNumberDtoAsContactDtoAfterAddingContact() {

        when(phoneNumberRepository.save(phoneNumber)).thenReturn(phoneNumber);

        ContactDto numberDto = new PhoneNumberDto(PHONE_NUMBER_VALUE);

        ContactDto contactDto = contactService.createNewContact(1L, numberDto);

        assertThat(contactDto)
                .isInstanceOf(PhoneNumberDto.class)
                .hasFieldOrPropertyWithValue("value", PHONE_NUMBER_VALUE)
                .hasFieldOrPropertyWithValue("contactType", ContactType.PHONE_NUMBER);
    }

    @Test
    public void shouldCorrectlyUpdateEmailAddressAndReturnContactDto() {

        when(emailAddressRepository.findByValue(EMAIL_ADDRESS_VALUE)).thenReturn(Optional.of(emailAddress));
        when(emailAddressRepository.save(emailAddress)).thenReturn(emailAddress);

        String updatedValue = "updated@test.com";
        ContactDto emailDto = new EmailAddressDto(updatedValue);

        ContactDto updatedContactDto = contactService.updateContact(1L, EMAIL_ADDRESS_VALUE, emailDto);

        assertThat(updatedContactDto)
                .isInstanceOf(EmailAddressDto.class)
                .hasFieldOrPropertyWithValue("value",updatedValue)
                .hasFieldOrPropertyWithValue("contactType", ContactType.EMAIL_ADDRESS);
    }

    @Test
    public void shouldCorrectlyUpdatePhoneNumberAndReturnContactDto() {

        when(phoneNumberRepository.findByValue(PHONE_NUMBER_VALUE)).thenReturn(Optional.of(phoneNumber));
        when(phoneNumberRepository.save(phoneNumber)).thenReturn(phoneNumber);

        String updatedValue = "987654321";
        ContactDto phoneDto = new PhoneNumberDto(updatedValue);

        ContactDto updatedContactDto = contactService.updateContact(1L, PHONE_NUMBER_VALUE, phoneDto);

        assertThat(updatedContactDto)
                .isInstanceOf(PhoneNumberDto.class)
                .hasFieldOrPropertyWithValue("value",updatedValue)
                .hasFieldOrPropertyWithValue("contactType", ContactType.PHONE_NUMBER);
    }

    @Test
    public void shouldDeleteEmailAddress() {

        when(emailAddressRepository.findByValue(EMAIL_ADDRESS_VALUE)).thenReturn(Optional.of(emailAddress)).thenReturn(null);

        contactService.deleteContact(1L, EMAIL_ADDRESS_VALUE);
        Optional<EmailAddress> deletedEmailAddress = emailAddressRepository.findByValue(EMAIL_ADDRESS_VALUE);

        assertThat(deletedEmailAddress).isNull();
    }

    @Test
    public void shouldDeletePhoneNumber() {

        when(phoneNumberRepository.findByValue(PHONE_NUMBER_VALUE)).thenReturn(Optional.of(phoneNumber)).thenReturn(null);

        contactService.deleteContact(1L, PHONE_NUMBER_VALUE);
        Optional<PhoneNumber> deletedPhoneNumber = phoneNumberRepository.findByValue(PHONE_NUMBER_VALUE);

        assertThat(deletedPhoneNumber).isNull();
    }
}
