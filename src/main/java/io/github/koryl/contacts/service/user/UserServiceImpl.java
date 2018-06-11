package io.github.koryl.contacts.service.user;

import com.google.common.collect.Lists;
import io.github.koryl.contacts.dao.EmailAddressRepository;
import io.github.koryl.contacts.dao.PhoneNumberRepository;
import io.github.koryl.contacts.dao.UserRepository;
import io.github.koryl.contacts.domain.dto.user.UserDto;
import io.github.koryl.contacts.domain.dto.contact.ContactDto;
import io.github.koryl.contacts.domain.entity.contact.EmailAddress;
import io.github.koryl.contacts.domain.entity.user.User;
import io.github.koryl.contacts.utilities.mapper.ContactMapper;
import io.github.koryl.contacts.utilities.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
public class UserServiceImpl implements UserService {

    private static final String MIN_DATE = "1918-01-01";

    private final UserRepository userRepository;
    private final EmailAddressRepository emailAddressRepository;
    private final PhoneNumberRepository phoneNumberRepository;
    private final UserMapper userMapper;
    private final ContactMapper contactMapper;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, EmailAddressRepository emailAddressRepository, PhoneNumberRepository phoneNumberRepository, UserMapper userMapper, ContactMapper contactMapper) {

        this.userRepository = userRepository;
        this.emailAddressRepository = emailAddressRepository;
        this.phoneNumberRepository = phoneNumberRepository;
        this.userMapper = userMapper;
        this.contactMapper = contactMapper;
    }

    @Transactional
    public List<UserDto> getAllUsers() {

        List<User> rawUsers = Lists.newArrayList(userRepository.findAll());

        log.info("It was found " + rawUsers.size() + " user(s).");

        return rawUsers
                .stream()
                .map(rawUser -> userMapper.mapUserToUserDto(rawUser, getContactsOf(rawUser)))
                .collect(toList());
    }

    @Transactional
    public UserDto getUserById(Long id) {

        User rawUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id: " + id + " not found."));

        log.info("It was found user with id " + rawUser.getId() + ".");

        return userMapper.mapUserToUserDto(rawUser, getContactsOf(rawUser));
    }

    @Transactional
    public UserDto createNewUser(UserDto userDto) {

        if (userRepository.findByPesel(userDto.getPesel()).isPresent()) {
            throw new RuntimeException("User with provided PESEL already exists.");
        }

        try {

            User mappedUser = userMapper.mapUserDtoToUser(userDto);
            User savedUser = userRepository.save(mappedUser);
            log.info("New user with id: " + savedUser.getId() + " was created.");

            return userMapper.mapUserToUserDto(savedUser, getContactsOf(savedUser));

        } catch (ConstraintViolationException | DataIntegrityViolationException e) {

            throw new RuntimeException("Cannot create user with provided data.");
        }
    }

    @Transactional
    public UserDto updateUserWithId(Long id, UserDto user) {

        User rawUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id: " + id + " not found."));

        User updatedUser = userRepository.save(updateUserDbWithUserDtoData(rawUser, user));
        log.info("User with id: " + updatedUser.getId() + " was updated.");

        return userMapper.mapUserToUserDto(updatedUser, getContactsOf(updatedUser));
    }

    @Transactional
    public void deleteUserWithId(Long id) {

        User rawUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id: " + id + " not found."));
        userRepository.delete(rawUser);
        log.info("User with id: " + rawUser.getId() + " was deleted.");
    }

    @Transactional
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

    @Transactional
    public List<UserDto> findPeopleByEmail(String email) {

        List<EmailAddress> emails = Lists.newArrayList(emailAddressRepository.findAll());
        List<User> rawUsers;

        if (email.startsWith("*") && email.endsWith("*")) {
            rawUsers = emails
                    .stream()
                    .filter(e -> e.getValue().contains(email.substring(1, (email.length() - 1))))
                    .map(EmailAddress::getUser)
                    .collect(toList());
            log.info("It was found " + rawUsers.size() + " user(s) having such email pattern.");

        } else {
            rawUsers = emails
                    .stream()
                    .filter(e -> Objects.equals(e.getValue(), email))
                    .map(EmailAddress::getUser)
                    .collect(toList());
            log.info("It was found " + rawUsers.size() + " user(s) having such email.");
        }

        return rawUsers
                .stream()
                .map(rawUser -> userMapper.mapUserToUserDto(rawUser, getContactsOf(rawUser)))
                .collect(toList());
    }

    private User updateUserDbWithUserDtoData(User userToUpdate, UserDto userDto) {

        userToUpdate.setFirstName(userDto.getFirstName());
        userToUpdate.setLastName((userDto.getLastName()));
        userToUpdate.setGender(userDto.getGender());
        userToUpdate.setBirthDate(userDto.getBirthDate());
        userToUpdate.setPesel(userDto.getPesel());

        return userToUpdate;
    }

    @Transactional
    private List<ContactDto> getContactsOf(User user) {

        List<ContactDto> contactEmails = contactMapper.mapContactListToContactDtoList(emailAddressRepository.findByUser(user));
        List<ContactDto> contactNumbers = contactMapper.mapContactListToContactDtoList(phoneNumberRepository.findByUser(user));

        return Stream
                .of(contactEmails, contactNumbers)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }
}
