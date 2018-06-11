package io.github.koryl.contacts.utilities.mapper;

import io.github.koryl.contacts.domain.dto.contact.EmailAddressDto;
import io.github.koryl.contacts.domain.dto.user.UserDto;
import io.github.koryl.contacts.domain.entity.user.User;
import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.Test;
import org.modelmapper.ModelMapper;

import java.util.Objects;

import static io.github.koryl.contacts.TestData.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserMapperTest {

    private ModelMapper modelMapper;
    private UserMapper userMapper;

    private User testUser;
    private UserDto testUserDto;

    @Before
    public void setUp() {

        modelMapper = mock(ModelMapper.class);
        userMapper = new UserMapper(modelMapper);

        testUser = new User(1, FIRST_NAME, LAST_NAME, GENDER, BIRTH_DATE, PESEL);
        testUserDto = new UserDto(1, FIRST_NAME, LAST_NAME, GENDER, BIRTH_DATE, PESEL, null);

    }

    @Test
    public void shouldMapUserToUserDto() {

        when(modelMapper.map(testUser, UserDto.class)).thenReturn(testUserDto);

        UserDto mappedUserDto = userMapper.mapUserToUserDto(testUser, Lists.list(new EmailAddressDto(EMAIL_ADDRESS_VALUE)));

        assertThat(mappedUserDto)
                .isNotNull()
                .matches(u -> Objects.equals(u.getFirstName(), FIRST_NAME) &&
                        Objects.equals(u.getLastName(), LAST_NAME) &&
                        Objects.equals(u.getGender(), GENDER) &&
                        Objects.equals(u.getBirthDate(), BIRTH_DATE) &&
                        Objects.equals(u.getPesel(), PESEL) &&
                        Objects.equals(u.getContacts().get(0).getValue(), EMAIL_ADDRESS_VALUE)
                );
    }

    @Test
    public void shouldMapUserDtoToUser() {

        when(modelMapper.map(testUserDto, User.class)).thenReturn(testUser);

        User mappedUser = userMapper.mapUserDtoToUser(testUserDto);

        assertThat(mappedUser)
                .isNotNull()
                .matches(u -> Objects.equals(u.getFirstName(), FIRST_NAME) &&
                        Objects.equals(u.getLastName(), LAST_NAME) &&
                        Objects.equals(u.getGender(), GENDER) &&
                        Objects.equals(u.getBirthDate(), BIRTH_DATE) &&
                        Objects.equals(u.getPesel(), PESEL)
                );
    }
}
