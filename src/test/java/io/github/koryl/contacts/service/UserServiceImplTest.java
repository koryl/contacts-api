package io.github.koryl.contacts.service;

import com.google.common.collect.ImmutableList;
import io.github.koryl.contacts.dao.EmailAddressRepository;
import io.github.koryl.contacts.dao.PhoneNumberRepository;
import io.github.koryl.contacts.dao.UserRepository;
import io.github.koryl.contacts.domain.dto.user.UserDto;
import io.github.koryl.contacts.domain.entity.user.User;
import io.github.koryl.contacts.utilities.mapper.ContactMapper;
import io.github.koryl.contacts.utilities.mapper.UserMapper;
import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserServiceImplTest {

    private final String firstName = "Jan";
    private final String lastName = "Kowalski";
    private final char gender = 'M';
    private final LocalDate birthDate = LocalDate.parse("1950-01-01");
    private final String pesel = "50010191216";

    private UserService userService;
    private UserRepository userRepository;
    private EmailAddressRepository emailAddressRepository;
    private PhoneNumberRepository phoneNumberRepository;
    private UserMapper userMapper;
    private ContactMapper contactMapper;

    private User testUser;
    private List<User> testUserList;

    @Before
    public void setUp() {

        userRepository = mock(UserRepository.class);
        emailAddressRepository = mock(EmailAddressRepository.class);
        phoneNumberRepository = mock(PhoneNumberRepository.class);
        userMapper = mock(UserMapper.class);
        contactMapper = mock(ContactMapper.class);
        userService = new UserServiceImpl(userRepository, emailAddressRepository, phoneNumberRepository, userMapper, contactMapper);

        testUser = new User(1, firstName, lastName, gender, birthDate, pesel);
        testUserList = ImmutableList.of(testUser);
    }

    @Test
    public void shouldGetAllUsers() {

        UserDto expectedUser = new UserDto(1, firstName, lastName, gender, birthDate, pesel, Lists.emptyList());


        when(userRepository.findAll()).thenReturn(testUserList);
        when(userMapper.mapUserToUserDto(testUser, Lists.emptyList())).thenReturn(expectedUser);

        List<UserDto> users = userService.getAllUsers();

        assertThat(users)
                .contains(expectedUser)
                .hasSize(1)
                .first()
                .hasNoNullFieldsOrProperties();
    }

    @Test
    public void shouldGetSpecificUserById() {

        UserDto expectedUser = new UserDto(1, firstName, lastName, gender, birthDate, pesel, Lists.emptyList());

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userMapper.mapUserToUserDto(testUser, Lists.emptyList())).thenReturn(expectedUser);

        UserDto user = userService.getUserById(1L);

        assertThat(user)
                .hasNoNullFieldsOrProperties()
                .isEqualToComparingFieldByField(expectedUser);
    }

    @Test
    public void shouldCreateNewUser() {

        UserDto newUser = new UserDto(1, firstName, lastName, gender, birthDate, pesel, Lists.emptyList());

        when(userRepository.save(testUser)).thenReturn(testUser);
        when(userMapper.mapUserToUserDto(testUser, Lists.emptyList())).thenReturn(newUser);
        when(userMapper.mapUserDtoToUser(newUser)).thenReturn(testUser);

        UserDto user = userService.createNewUser(newUser);

        assertThat(user)
                .hasNoNullFieldsOrProperties()
                .isEqualToComparingFieldByField(newUser);
    }

    @Test
    public void shouldNotCreateNewUserWithInvalidPesel() {

        UserDto newUser = new UserDto(1, firstName, lastName, gender, birthDate, "123456789000", Lists.emptyList());

        when(userRepository.save(testUser)).thenReturn(testUser);
        when(userMapper.mapUserToUserDto(testUser, Lists.emptyList())).thenReturn(newUser);
        when(userMapper.mapUserDtoToUser(newUser)).thenReturn(testUser);

        UserDto user = userService.createNewUser(newUser);

        assertThat(user)
                .hasNoNullFieldsOrProperties()
                .hasFieldOrPropertyWithValue("firstName", newUser.getFirstName())
                .hasFieldOrPropertyWithValue("lastName", newUser.getLastName())
                .hasFieldOrPropertyWithValue("gender", newUser.getGender())
                .hasFieldOrPropertyWithValue("birthDate", newUser.getBirthDate())
                .hasFieldOrPropertyWithValue("pesel", newUser.getPesel());
    }
}
