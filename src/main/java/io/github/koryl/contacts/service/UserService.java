package io.github.koryl.contacts.service;

import com.google.common.collect.Lists;
import io.github.koryl.contacts.dao.UserRepository;
import io.github.koryl.contacts.domain.dto.UserDto;
import io.github.koryl.contacts.domain.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import static java.util.Objects.*;
import static java.util.stream.Collectors.*;

@Service
@Slf4j
public class UserService {

    private static final String MIN_DATE = "1918-01-01";

    private UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {

        this.userRepository = userRepository;
    }

    public List<UserDto> getAllUsers() {

        List<User> rawUsers = Lists.newArrayList(userRepository.findAll());

        return rawUsers
                .stream()
                .map(this::fromDbTransferToUserDto)
                .collect(toList());
    }

    public UserDto getUserById(Long id) {

        User rawUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id: " + id + " not found."));

        return fromDbTransferToUserDto(rawUser);
    }

    public UserDto createNewUser(UserDto userDto) {

        User savedUser = userRepository.save(fromUserDtoTransferToDb(userDto));

        log.info("New user with id: " + savedUser.getId() + " was created.");

        return fromDbTransferToUserDto(savedUser);
    }

    public UserDto updateUserWithId(Long id, UserDto user) {

        User rawUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id: " + id + " not found."));

        User updatedUser = userRepository.save(updateUserDbWithUserDtoData(rawUser, user));
        log.info("User with id: " + updatedUser.getId() + " was updated.");

        return fromDbTransferToUserDto(updatedUser);
    }

    public void deleteUserWithId(Long id) {

        User rawUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id: " + id + " not found."));
        userRepository.delete(rawUser);
        log.info("User with id: " + rawUser.getId() + "was deleted.");
    }



    public List<UserDto> findPeopleByBirthDateBetween(String from, String to) {

        LocalDate fromDate;
        LocalDate toDate;

        if (isNull(from) || Objects.equals(from, "")) {
            fromDate = LocalDate.parse(MIN_DATE);
        } else {
            fromDate = LocalDate.parse(from);
        }

        if (isNull(to) || Objects.equals(from, "")) {
            toDate = LocalDate.now();
        } else {
            toDate = LocalDate.parse(to);
        }

        List<User> rawUsers = userRepository.findUsersByBirthDateAfterAndBirthDateBefore(fromDate, toDate);

        log.info("It was found " + rawUsers.size() + " users having a birth day in this range.");

        return rawUsers
                .stream()
                .map(this::fromDbTransferToUserDto)
                .collect(toList());
    }


    private UserDto fromDbTransferToUserDto(User rawUser) {

        return new UserDto(
                rawUser.getId(),
                rawUser.getFirstName(),
                rawUser.getLastName(),
                rawUser.getGender(),
                rawUser.getBirthDate(),
                rawUser.getPesel());
    }

    private User fromUserDtoTransferToDb(UserDto userDto) {

        User user = new User();
        user.setFirstName(userDto.getFirstName());
        user.setLastName((userDto.getLastName()));
        user.setGender(userDto.getGender());
        user.setBirthDate(userDto.getBirthDate());
        user.setPesel(userDto.getPesel());

        return user;
    }

    private User updateUserDbWithUserDtoData(User userToUpdate, UserDto updatingUserData) {

        userToUpdate.setFirstName(updatingUserData.getFirstName());
        userToUpdate.setLastName((updatingUserData.getLastName()));
        userToUpdate.setGender(updatingUserData.getGender());
        userToUpdate.setBirthDate(updatingUserData.getBirthDate());
        userToUpdate.setPesel(updatingUserData.getPesel());

        return userToUpdate;
    }
}
