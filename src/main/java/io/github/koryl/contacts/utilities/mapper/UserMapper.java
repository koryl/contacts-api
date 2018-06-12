package io.github.koryl.contacts.utilities.mapper;

import io.github.koryl.contacts.domain.dto.user.UserDto;
import io.github.koryl.contacts.domain.dto.contact.ContactDto;
import io.github.koryl.contacts.domain.dto.user.UserDtoBuilder;
import io.github.koryl.contacts.domain.entity.user.User;
import io.github.koryl.contacts.domain.entity.user.UserBuilder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserMapper {

    public UserDto mapUserToUserDto(User rawUser, List<ContactDto> contacts) {

        UserDtoBuilder userDtoBuilder = new UserDtoBuilder();

        return userDtoBuilder
                .id(rawUser.getId())
                .firstName(rawUser.getFirstName())
                .lastName(rawUser.getLastName())
                .gender(rawUser.getGender())
                .birthDate(rawUser.getBirthDate())
                .pesel(rawUser.getPesel())
                .contacts(contacts)
                .createUser();
    }

    public User mapUserDtoToUser(UserDto userDto) {

        UserBuilder userBuilder = new UserBuilder();

        return userBuilder.id(userDto.getId())
                .firstName(userDto.getFirstName())
                .lastName(userDto.getLastName())
                .gender(userDto.getGender())
                .birthDate(userDto.getBirthDate())
                .pesel(userDto.getPesel())
                .createUser();
    }
}
