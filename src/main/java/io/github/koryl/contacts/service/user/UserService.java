package io.github.koryl.contacts.service.user;

import io.github.koryl.contacts.domain.dto.user.UserDto;

import java.util.List;

public interface UserService {

    List<UserDto> getAllUsers();

    UserDto getUserById(Long id);

    UserDto createNewUser(UserDto userDto);

    UserDto updateUserWithId(Long id, UserDto user);

    void deleteUserWithId(Long id);

    List<UserDto> findPeopleByBirthDateBetween(String from, String to);

    List<UserDto> findPeopleByEmail(String email);
}
