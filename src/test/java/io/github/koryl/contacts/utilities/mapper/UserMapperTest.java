package io.github.koryl.contacts.utilities.mapper;


import io.github.koryl.contacts.domain.dto.contact.EmailAddressDto;
import io.github.koryl.contacts.domain.dto.user.UserDto;
import io.github.koryl.contacts.domain.entity.user.User;
import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.Test;
import org.modelmapper.ModelMapper;

import java.time.LocalDate;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserMapperTest {

    private final String firstName = "Jan";
    private final String lastName = "Kowalski";
    private final char gender = 'M';
    private final LocalDate birthDate = LocalDate.parse("1950-01-01");
    private final String pesel = "50010191216";
    private final String emailValue = "test@test.com";

    private ModelMapper modelMapper;
    private UserMapper userMapper;

    private User testUser;
    private UserDto testUserDto;

    @Before
    public void setUp() {

        modelMapper = mock(ModelMapper.class);
        userMapper = new UserMapper(modelMapper);

        testUser = new User(1, firstName, lastName, gender, birthDate, pesel);
        testUserDto = new UserDto(1, firstName, lastName, gender, birthDate, pesel, Lists.list(new EmailAddressDto(emailValue)));

    }

    @Test
    public void shouldMapUserToUserDto() {

        when(modelMapper.map(testUser, UserDto.class)).thenReturn(testUserDto);

        UserDto mappedUser = userMapper.mapUserToUserDto(testUser, Lists.list(new EmailAddressDto(emailValue)));

        assertThat(mappedUser)
                .isNotNull()
                .matches(u -> Objects.equals(u.getFirstName(), firstName) &&
                        Objects.equals(u.getLastName(), lastName) &&
                        Objects.equals(u.getGender(), gender) &&
                        Objects.equals(u.getBirthDate(), birthDate) &&
                        Objects.equals(u.getPesel(), pesel) &&
                        Objects.equals(u.getContacts().get(0).getValue(), emailValue)
                );
    }
}
