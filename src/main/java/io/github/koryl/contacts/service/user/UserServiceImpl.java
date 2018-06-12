package io.github.koryl.contacts.service.user;

import com.google.common.collect.Lists;
import io.github.koryl.contacts.dao.EmailAddressRepository;
import io.github.koryl.contacts.dao.PhoneNumberRepository;
import io.github.koryl.contacts.dao.UserRepository;
import io.github.koryl.contacts.domain.dto.user.UserDto;
import io.github.koryl.contacts.domain.entity.contact.Contact;
import io.github.koryl.contacts.domain.entity.contact.EmailAddress;
import io.github.koryl.contacts.domain.entity.user.User;
import io.github.koryl.contacts.service.contact.ContactOperations;
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
import java.util.List;
import java.util.Objects;

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
    private final ContactOperations contactOperations;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, EmailAddressRepository emailAddressRepository, PhoneNumberRepository phoneNumberRepository, UserMapper userMapper, ContactMapper contactMapper, ContactOperations contactOperations) {

        this.userRepository = userRepository;
        this.emailAddressRepository = emailAddressRepository;
        this.phoneNumberRepository = phoneNumberRepository;
        this.userMapper = userMapper;
        this.contactMapper = contactMapper;
        this.contactOperations = contactOperations;
    }

    @Transactional
    public List<UserDto> getAllUsers() {

        List<User> rawUsers = Lists.newArrayList(userRepository.findAll());

        log.info("It was found " + rawUsers.size() + " user(s).");

        return rawUsers
                .stream()
                .map(rawUser -> userMapper.mapUserToUserDto(rawUser, contactOperations.getContactsOf(rawUser)))
                .collect(toList());
    }

    @Transactional
    public UserDto getUserById(Long id) {

        User rawUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id: " + id + " not found."));

        log.info("It was found user with id " + rawUser.getId() + ".");

        return userMapper.mapUserToUserDto(rawUser, contactOperations.getContactsOf(rawUser));
    }

    @Transactional
    public UserDto createNewUser(UserDto userDto) {

        if (userRepository.findByPesel(userDto.getPesel()).isPresent()) {
            throw new RuntimeException("User with provided PESEL already exists.");
        }

        try {
            User mappedUser = userMapper.mapUserDtoToUser(userDto);
            User savedUser = userRepository.save(mappedUser);
            List<Contact> contacts = contactMapper.mapContactDtoListToContactList(userDto.getContacts(), savedUser);
            contacts.forEach(contactOperations::saveContact);
            log.info("New user with id: " + savedUser.getId() + " was created.");

            return userMapper.mapUserToUserDto(savedUser, contactOperations.getContactsOf(savedUser));

        } catch (ConstraintViolationException | DataIntegrityViolationException e) {

            throw new RuntimeException("Cannot create user with provided data.");
        }
    }

    @Transactional
    public UserDto updateUserWithId(Long id, UserDto userDto) {

        User rawUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id: " + id + " not found."));
        User userToUpdate = updateUserDbWithUserDtoData(rawUser, userDto);
        User updatedUser = userRepository.save(userToUpdate);
        log.info("User with id: " + updatedUser.getId() + " was updated.");

        return userMapper.mapUserToUserDto(updatedUser, contactOperations.getContactsOf(updatedUser));
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
                .map(rawUser -> userMapper.mapUserToUserDto(rawUser, contactOperations.getContactsOf(rawUser)))
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
                    .distinct()
                    .collect(toList());
            log.info("It was found " + rawUsers.size() + " user(s) having such email pattern.");

        } else {
            rawUsers = emails
                    .stream()
                    .filter(e -> Objects.equals(e.getValue(), email))
                    .map(EmailAddress::getUser)
                    .distinct()
                    .collect(toList());
            log.info("It was found " + rawUsers.size() + " user(s) having such email.");
        }

        return rawUsers
                .stream()
                .map(rawUser -> userMapper.mapUserToUserDto(rawUser, contactOperations.getContactsOf(rawUser)))
                .collect(toList());
    }

    private User updateUserDbWithUserDtoData(User userToUpdate, UserDto userDto) {

        if (nonNull(userDto.getFirstName()) && !Objects.equals(userDto.getFirstName(), "")) {
            userToUpdate.setFirstName(userDto.getFirstName().trim());
        }
        if (nonNull(userDto.getLastName()) && !Objects.equals(userDto.getLastName(), "")) {
            userToUpdate.setLastName((userDto.getLastName().trim()));
        }
        if (Character.isLetter(userDto.getGender())) {
            userToUpdate.setGender(userDto.getGender());
        }
        if (nonNull(userDto.getBirthDate())) {
            userToUpdate.setBirthDate(userDto.getBirthDate());
        }
        if (nonNull(userDto.getPesel()) && !Objects.equals(userDto.getPesel(), "") && !Objects.equals(userDto.getPesel(), userToUpdate.getPesel())) {
            userToUpdate.setPesel(userDto.getPesel());
        }
        return userToUpdate;
    }
}
