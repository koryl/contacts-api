package io.github.koryl.contacts.service;

import com.google.common.collect.Lists;
import io.github.koryl.contacts.dao.UserRepository;
import io.github.koryl.contacts.domain.dto.UserDto;
import io.github.koryl.contacts.domain.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.*;

@Service
public class UserService {

    private UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {

        this.userRepository = userRepository;
    }

    public List<UserDto> getAllUsers() {

        List<User> rawUsers = Lists.newArrayList(userRepository.findAll());
        List<UserDto> users;

        users = rawUsers
                .stream()
                .map(this::fromDbTransferToUserDto)
                .collect(toList());

        return users;
    }

    public UserDto getUserById(Long id) {

        Optional<User> opUser = userRepository.findById(id);
        UserDto user = null;

        if (opUser.isPresent()) {
            User rawUser = opUser.get();
            user = fromDbTransferToUserDto(rawUser);
        }

        return user;
    }

    private UserDto fromDbTransferToUserDto(User rawUser) {

        return new UserDto(
                rawUser.getId(),
                rawUser.getFirstName(),
                rawUser.getLastName(),
                rawUser.getGender(),
                rawUser.getBirthDate(),
                rawUser.getPesel(),
                rawUser.getContacts());
    }
}
