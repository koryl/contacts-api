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

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

public class ContactServiceImplTest {

    private final String firstName = "Jan";
    private final String lastName = "Kowalski";
    private final char gender = 'M';
    private final LocalDate birthDate = LocalDate.parse("1950-01-01");
    private final String pesel = "50010191216";

    private final String emailAddressValue = "test@test.com";
    private final String phoneNumberValue = "123456789";

    private ContactService contactService;
    private UserRepository userRepository;
    private EmailAddressRepository emailAddressRepository;
    private PhoneNumberRepository phoneNumberRepository;
    private ContactFactory contactFactory;
    private ContactDtoFactory contactDtoFactory;

    private User testUser;
    private EmailAddress emailAddress;
    private PhoneNumber phoneNumber;


    @Before
    public void setUp() {

        userRepository = mock(UserRepository.class);
        emailAddressRepository = mock(EmailAddressRepository.class);
        phoneNumberRepository = mock(PhoneNumberRepository.class);
        contactFactory = new ContactFactory();
        contactDtoFactory = new ContactDtoFactory();
        contactService = new ContactServiceImpl(userRepository, emailAddressRepository, phoneNumberRepository, contactFactory, contactDtoFactory);

        testUser = new User(1, firstName, lastName, gender, birthDate, pesel);
        emailAddress = new EmailAddress(0, emailAddressValue, testUser);
        phoneNumber = new PhoneNumber(0, phoneNumberValue, testUser);


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
                .contains(new EmailAddressDto(emailAddressValue))
                .contains(new PhoneNumberDto(phoneNumberValue));
    }

    @Test
    public void shouldCorrectlyReturnEmailAddressDtoAsContactDtoAfterAddingContact() {

        when(emailAddressRepository.save(emailAddress)).thenReturn(emailAddress);

        ContactDto emailDto = new EmailAddressDto(emailAddressValue);

        ContactDto contactDto = contactService.createNewContact(1L, emailDto);

        assertThat(contactDto)
                .isInstanceOf(EmailAddressDto.class)
                .hasFieldOrPropertyWithValue("value", emailAddressValue)
                .hasFieldOrPropertyWithValue("contactType", ContactType.EMAIL_ADDRESS);
    }

    @Test
    public void shouldCorrectlyReturnPhoneNumberDtoAsContactDtoAfterAddingContact() {

        when(phoneNumberRepository.save(phoneNumber)).thenReturn(phoneNumber);

        ContactDto numberDto = new PhoneNumberDto(phoneNumberValue);

        ContactDto contactDto = contactService.createNewContact(1L, numberDto);

        assertThat(contactDto)
                .isInstanceOf(PhoneNumberDto.class)
                .hasFieldOrPropertyWithValue("value", phoneNumberValue)
                .hasFieldOrPropertyWithValue("contactType", ContactType.PHONE_NUMBER);
    }

    @Test
    public void shouldCorrectlyUpdateEmailAddressAndReturnContactDto() {

        when(emailAddressRepository.findByValue(emailAddressValue)).thenReturn(Optional.of(emailAddress));
        when(emailAddressRepository.save(emailAddress)).thenReturn(emailAddress);

        String updatedValue = "updated@test.com";
        ContactDto emailDto = new EmailAddressDto(updatedValue);

        ContactDto updatedContactDto = contactService.updateContact(1L, emailAddressValue, emailDto);

        assertThat(updatedContactDto)
                .isInstanceOf(EmailAddressDto.class)
                .hasFieldOrPropertyWithValue("value",updatedValue)
                .hasFieldOrPropertyWithValue("contactType", ContactType.EMAIL_ADDRESS);
    }

    @Test
    public void shouldCorrectlyUpdatePhoneNumberAndReturnContactDto() {

        when(phoneNumberRepository.findByValue(phoneNumberValue)).thenReturn(Optional.of(phoneNumber));
        when(phoneNumberRepository.save(phoneNumber)).thenReturn(phoneNumber);

        String updatedValue = "987654321";
        ContactDto phoneDto = new PhoneNumberDto(updatedValue);

        ContactDto updatedContactDto = contactService.updateContact(1L, phoneNumberValue, phoneDto);

        assertThat(updatedContactDto)
                .isInstanceOf(PhoneNumberDto.class)
                .hasFieldOrPropertyWithValue("value",updatedValue)
                .hasFieldOrPropertyWithValue("contactType", ContactType.PHONE_NUMBER);
    }

    @Test
    public void shouldDeleteEmailAddress() {

        when(emailAddressRepository.findByValue(emailAddressValue)).thenReturn(Optional.of(emailAddress)).thenReturn(null);

        contactService.deleteContact(1L, emailAddressValue);
        Optional<EmailAddress> deletedEmailAddress = emailAddressRepository.findByValue(emailAddressValue);

        assertThat(deletedEmailAddress).isNull();
    }

    @Test
    public void shouldDeletePhoneNumber() {

        when(phoneNumberRepository.findByValue(phoneNumberValue)).thenReturn(Optional.of(phoneNumber)).thenReturn(null);

        contactService.deleteContact(1L, phoneNumberValue);
        Optional<PhoneNumber> deletedPhoneNumber = phoneNumberRepository.findByValue(phoneNumberValue);

        assertThat(deletedPhoneNumber).isNull();
    }
}
