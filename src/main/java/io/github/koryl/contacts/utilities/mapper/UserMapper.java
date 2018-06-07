package io.github.koryl.contacts.utilities.mapper;

import io.github.koryl.contacts.domain.dto.UserDto;
import io.github.koryl.contacts.domain.dto.contact.ContactDto;
import io.github.koryl.contacts.domain.entity.User;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserMapper {

    private final ModelMapper modelMapper;

    @Autowired
    public UserMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public UserDto mapUserToUserDto(User rawUser, List<ContactDto> contacts) {

        UserDto userDto = modelMapper.map(rawUser, UserDto.class);
        userDto.setContacts(contacts);
        return userDto;
    }

    public User mapUserDtoToUser(UserDto userDto) {

        return modelMapper.map(userDto, User.class);
    }
}
