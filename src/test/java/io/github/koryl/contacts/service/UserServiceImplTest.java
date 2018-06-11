package io.github.koryl.contacts.service;

import com.google.common.collect.ImmutableList;
import io.github.koryl.contacts.dao.EmailAddressRepository;
import io.github.koryl.contacts.dao.PhoneNumberRepository;
import io.github.koryl.contacts.dao.UserRepository;
import io.github.koryl.contacts.domain.dto.contact.ContactDto;
import io.github.koryl.contacts.domain.dto.contact.EmailAddressDto;
import io.github.koryl.contacts.domain.dto.contact.PhoneNumberDto;
import io.github.koryl.contacts.domain.dto.user.UserDto;
import io.github.koryl.contacts.domain.entity.contact.EmailAddress;
import io.github.koryl.contacts.domain.entity.contact.PhoneNumber;
import io.github.koryl.contacts.domain.entity.user.User;
import io.github.koryl.contacts.service.user.UserService;
import io.github.koryl.contacts.service.user.UserServiceImpl;
import io.github.koryl.contacts.utilities.mapper.ContactMapper;
import io.github.koryl.contacts.utilities.mapper.UserMapper;
import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.Test;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static io.github.koryl.contacts.TestData.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.ThrowableAssert.catchThrowable;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserServiceImplTest {

    private UserService userService;
    private UserRepository userRepository;
    private EmailAddressRepository emailAddressRepository;
    private PhoneNumberRepository phoneNumberRepository;

    private User testUser;
    private List<User> testUserList;

    @Before
    public void setUp() {

        userRepository = mock(UserRepository.class);
        emailAddressRepository = mock(EmailAddressRepository.class);
        phoneNumberRepository = mock(PhoneNumberRepository.class);
        UserMapper userMapper = new UserMapper();
        ContactMapper contactMapper = new ContactMapper();
        userService = new UserServiceImpl(userRepository, emailAddressRepository, phoneNumberRepository, userMapper, contactMapper);

        testUser = new User(1, FIRST_NAME, LAST_NAME, GENDER, BIRTH_DATE, PESEL);
        testUserList = ImmutableList.of(testUser);
    }

    @Test
    public void shouldGetAllUsers() {

        UserDto expectedUser = new UserDto(1, FIRST_NAME, LAST_NAME, GENDER, BIRTH_DATE, PESEL, Lists.emptyList());

        when(userRepository.findAll()).thenReturn(testUserList);

        List<UserDto> users = userService.getAllUsers();

        assertThat(users)
                .contains(expectedUser)
                .hasSize(1)
                .first()
                .hasNoNullFieldsOrProperties();
    }

    @Test
    public void shouldGetSpecificUserById() {

        UserDto expectedUser = new UserDto(1, FIRST_NAME, LAST_NAME, GENDER, BIRTH_DATE, PESEL, Lists.emptyList());

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        UserDto user = userService.getUserById(1L);

        assertThat(user)
                .hasNoNullFieldsOrProperties()
                .isEqualToComparingFieldByField(expectedUser);
    }

    @Test
    public void shouldGetUserWithUserContacts() {

        EmailAddress emailAddress = new EmailAddress(1, EMAIL_ADDRESS_VALUE, testUser);
        PhoneNumber phoneNumber = new PhoneNumber(1, PHONE_NUMBER_VALUE, testUser);
        List<ContactDto> expectedContacts = Lists.newArrayList(new EmailAddressDto(EMAIL_ADDRESS_VALUE), new PhoneNumberDto(PHONE_NUMBER_VALUE));
        UserDto expectedUser = new UserDto(1, FIRST_NAME, LAST_NAME, GENDER, BIRTH_DATE, PESEL, expectedContacts);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(emailAddressRepository.findByUser(testUser)).thenReturn(Lists.list(emailAddress));
        when(phoneNumberRepository.findByUser(testUser)).thenReturn(Lists.list(phoneNumber));


        UserDto user = userService.getUserById(1L);

        assertThat(user)
                .hasNoNullFieldsOrProperties()
                .isEqualToComparingFieldByField(expectedUser);
    }

    @Test
    public void shouldCreateNewUser() {

        UserDto newUser = new UserDto(1, FIRST_NAME, LAST_NAME, GENDER, BIRTH_DATE, PESEL, Lists.emptyList());

        when(userRepository.save(testUser)).thenReturn(testUser);

        UserDto user = userService.createNewUser(newUser);

        assertThat(user)
                .hasNoNullFieldsOrProperties()
                .matches(u -> Objects.equals(u.getFirstName(), FIRST_NAME) &&
                        Objects.equals(u.getLastName(), LAST_NAME) &&
                        Objects.equals(u.getGender(), GENDER) &&
                        Objects.equals(u.getBirthDate(), BIRTH_DATE) &&
                        Objects.equals(u.getPesel(), PESEL));
    }

    @Test
    public void shouldNotCreateNewUserWhenPeselExists() {

        UserDto newUser = new UserDto(1, FIRST_NAME, LAST_NAME, GENDER, BIRTH_DATE, PESEL, Lists.emptyList());
        UserDto anotherUser = new UserDto(2, "Test", "Test", GENDER, BIRTH_DATE, PESEL, Lists.emptyList());

        when(userRepository.save(testUser)).thenReturn(testUser);
        when(userRepository.findByPesel(PESEL)).thenReturn(Optional.of(testUser));

        Throwable thrown = catchThrowable(() -> {
            userService.createNewUser(newUser);
            userService.createNewUser(anotherUser);
        });

        assertThat(thrown)
                .isNotNull()
                .hasMessage("User with provided PESEL already exists.");
    }

    @Test
    public void shouldUpdateUser() {

        UserDto newUser = new UserDto(1, "Test", "Test", GENDER, BIRTH_DATE, PESEL, Lists.emptyList());

        when(userRepository.save(testUser)).thenReturn(testUser);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        userRepository.save(testUser);

        UserDto updatedUser = userService.updateUserWithId(1L, newUser);


        assertThat(updatedUser)
                .hasNoNullFieldsOrProperties()
                .matches(u -> Objects.equals(u.getFirstName(), "Test") &&
                        Objects.equals(u.getLastName(), "Test"));
    }

    @Test
    public void shouldNotUpdateUserIfNotExist() {

        UserDto newUser = new UserDto(1, "Test", "Test", GENDER, BIRTH_DATE, PESEL, Lists.emptyList());

        when(userRepository.save(testUser)).thenReturn(testUser);
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        Throwable thrown = catchThrowable(() -> userService.updateUserWithId(1L, newUser));

        assertThat(thrown)
                .isNotNull()
                .hasMessage("User with id: 1 not found.")
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    public void shouldDeleteUser() {

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser)).thenReturn(null);

        userService.deleteUserWithId(1L);
        Optional<User> deletedUser = userRepository.findById(1L);

        assertThat(deletedUser).isNull();
    }

    @Test
    public void shouldFindPeopleWithBirthDayInProvidedScope() {

        when(userRepository.findUsersByBirthDateIsGreaterThanEqualAndBirthDateLessThanEqual(MIN_DATE, MAX_DATE)).thenReturn(Lists.list(testUser));

        List<UserDto> foundUsers = userService.findPeopleByBirthDateBetween(MIN_DATE.toString(), MAX_DATE.toString());

        assertThat(foundUsers)
                .hasSize(1)
                .doesNotContainNull()
                .first()
                .matches(u -> Objects.equals(u.getFirstName(), FIRST_NAME) &&
                        Objects.equals(u.getLastName(), LAST_NAME) &&
                        Objects.equals(u.getGender(), GENDER) &&
                        Objects.equals(u.getBirthDate(), BIRTH_DATE) &&
                        Objects.equals(u.getPesel(), PESEL));
    }

    @Test
    public void shouldFindUserByExactEmail() {

        EmailAddressDto emailAddressToFind = new EmailAddressDto(EMAIL_ADDRESS_VALUE);
        EmailAddress testEmail1 = new EmailAddress(1, EMAIL_ADDRESS_VALUE, testUser);
        EmailAddress testEmail2 = new EmailAddress(2, "another@test.com", new User());
        EmailAddress testEmail3 = new EmailAddress(3, "example@test.com", new User());

        List<EmailAddress> emails = Lists.list(testEmail1, testEmail2, testEmail3);

        when(emailAddressRepository.findAll()).thenReturn(emails);
        when(emailAddressRepository.findByUser(testUser)).thenReturn(Lists.list(testEmail1));

        List<UserDto> foundUsers = userService.findPeopleByEmail(EMAIL_ADDRESS_VALUE);

        assertThat(foundUsers)
                .hasSize(1)
                .doesNotContainNull()
                .first()
                .isEqualTo(new UserDto(1L, FIRST_NAME, LAST_NAME, GENDER, BIRTH_DATE, PESEL, Lists.list(emailAddressToFind)));
    }

    @Test
    public void shouldFindUserByPartOfEmail() {

        EmailAddressDto emailAddressToFind = new EmailAddressDto(EMAIL_ADDRESS_VALUE);
        EmailAddress testEmail1 = new EmailAddress(1, EMAIL_ADDRESS_VALUE, testUser);
        EmailAddress testEmail2 = new EmailAddress(2, "another@another.com", new User(2, "Another", "User",  'M', LocalDate.parse("2000-01-01"), "00210123911"));
        EmailAddress testEmail3 = new EmailAddress(3, "example@example.com", new User(3, "Example", "User", 'F', LocalDate.parse("1980-06-15"), "80061540202"));

        List<EmailAddress> emails = Lists.list(testEmail1, testEmail2, testEmail3);

        when(emailAddressRepository.findAll()).thenReturn(emails);
        when(emailAddressRepository.findByUser(testUser)).thenReturn(Lists.list(testEmail1));

        List<UserDto> foundUsers = userService.findPeopleByEmail("*test*");

        assertThat(foundUsers)
                .hasSize(1)
                .doesNotContainNull()
                .first()
                .isEqualTo(new UserDto(1L, FIRST_NAME, LAST_NAME, GENDER, BIRTH_DATE, PESEL, Lists.list(emailAddressToFind)));
    }

    @Test
    public void shouldNotFindUserByPartOfEmailWhenNoSuchPatternInEmailsExist() {

        EmailAddress testEmail1 = new EmailAddress(1, "another@another.com", new User(1, "Another", "User",  'M', LocalDate.parse("2000-01-01"), "00210123911"));
        EmailAddress testEmail2 = new EmailAddress(2, "example@example.com", new User(2, "Example", "User", 'F', LocalDate.parse("1980-06-15"), "80061540202"));

        List<EmailAddress> emails = Lists.list(testEmail1, testEmail2);

        when(emailAddressRepository.findAll()).thenReturn(emails);

        List<UserDto> foundUsers = userService.findPeopleByEmail("*test*");

        assertThat(foundUsers)
                .hasSize(0);
    }

    @Test
    public void shouldNotFindUserByPartOfEmailWithoutPatternWithStarsSymbols() {

        EmailAddress testEmail1 = new EmailAddress(1, "test@another.com", new User(1, "Another", "User",  'M', LocalDate.parse("2000-01-01"), "00210123911"));
        EmailAddress testEmail2 = new EmailAddress(2, "example@test.com", new User(2, "Example", "User", 'F', LocalDate.parse("1980-06-15"), "80061540202"));

        List<EmailAddress> emails = Lists.list(testEmail1, testEmail2);

        when(emailAddressRepository.findAll()).thenReturn(emails);

        List<UserDto> foundUsers = userService.findPeopleByEmail("test");

        assertThat(foundUsers)
                .hasSize(0);
    }
}
