package io.github.koryl.contacts.service;

import com.google.common.collect.Lists;
import io.github.koryl.contacts.dao.EmailAddressRepository;
import io.github.koryl.contacts.dao.PhoneNumberRepository;
import io.github.koryl.contacts.dao.UserRepository;
import io.github.koryl.contacts.domain.dto.UserDto;
import io.github.koryl.contacts.domain.dto.UserDtoBuilder;
import io.github.koryl.contacts.domain.dto.contact.ContactDto;
import io.github.koryl.contacts.domain.entity.User;
import io.github.koryl.contacts.utilities.mapper.ContactMapper;
import io.github.koryl.contacts.utilities.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.*;
import static java.util.stream.Collectors.*;

@Service
@Slf4j
public class UserService {

    private static final String MIN_DATE = "1918-01-01";

    private final UserRepository userRepository;
    private final UserDtoBuilder userDtoBuilder;
    private final EmailAddressRepository emailAddressRepository;
    private final PhoneNumberRepository phoneNumberRepository;
    private final UserMapper userMapper;
    private final ContactMapper contactMapper;

    @Autowired
    public UserService(UserRepository userRepository, UserDtoBuilder userDtoBuilder, EmailAddressRepository emailAddressRepository, PhoneNumberRepository phoneNumberRepository, ModelMapper modelMapper, UserMapper userMapper, ContactMapper contactMapper) {

        this.userRepository = userRepository;
        this.userDtoBuilder = userDtoBuilder;
        this.emailAddressRepository = emailAddressRepository;
        this.phoneNumberRepository = phoneNumberRepository;
        this.userMapper = userMapper;
        this.contactMapper = contactMapper;
    }

    public List<UserDto> getAllUsers() {

        List<User> rawUsers = Lists.newArrayList(userRepository.findAll());

        return rawUsers
                .stream()
                .map(rawUser -> userMapper.mapUserToUserDto(rawUser, getContactsOf(rawUser)))
                .collect(toList());
    }

    public UserDto getUserById(Long id) {

        User rawUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id: " + id + " not found."));

        return userMapper.mapUserToUserDto(rawUser, getContactsOf(rawUser));
    }

    public UserDto createNewUser(UserDto userDto) {

        try {
            User savedUser = userRepository.save(userMapper.mapUserDtoToUser(userDto));
            log.info("New user with id: " + savedUser.getId() + " was created.");

            return userMapper.mapUserToUserDto(savedUser, getContactsOf(savedUser));

        } catch (ConstraintViolationException | DataIntegrityViolationException e) {
            log.error("Cannot create user with provided data.");
            throw new RuntimeException("Provided user already exist. Check if PESEL is unique.");
        }
    }

    public UserDto updateUserWithId(Long id, UserDto user) {

        User rawUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id: " + id + " not found."));

        User updatedUser = userRepository.save(updateUserDbWithUserDtoData(rawUser, user));
        log.info("User with id: " + updatedUser.getId() + " was updated.");

        return userMapper.mapUserToUserDto(updatedUser, getContactsOf(updatedUser));
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

        List<User> rawUsers = userRepository.findUsersByBirthDateIsGreaterThanEqualAndBirthDateLessThanEqual(fromDate, toDate);

        log.info("It was found " + rawUsers.size() + " users having a birth day in this range.");

        return rawUsers
                .stream()
                .map(rawUser -> userMapper.mapUserToUserDto(rawUser, getContactsOf(rawUser)))
                .collect(toList());
    }

    private User updateUserDbWithUserDtoData(User userToUpdate, UserDto updatingUserData) {

        userToUpdate.setFirstName(updatingUserData.getFirstName());
        userToUpdate.setLastName((updatingUserData.getLastName()));
        userToUpdate.setGender(updatingUserData.getGender());
        userToUpdate.setBirthDate(updatingUserData.getBirthDate());
        userToUpdate.setPesel(updatingUserData.getPesel());

        return userToUpdate;
    }

    private List<ContactDto> getContactsOf(User user) {

        List<ContactDto> contactEmails = contactMapper.mapContactListToContactDtoList(emailAddressRepository.findByUser(user));
        List<ContactDto> contactNumbers = contactMapper.mapContactListToContactDtoList(phoneNumberRepository.findByUser(user));

        return Stream
                .of(contactEmails, contactNumbers)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }
}
