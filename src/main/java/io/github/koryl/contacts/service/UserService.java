package io.github.koryl.contacts.service;

import com.google.common.collect.Lists;
import io.github.koryl.contacts.dao.UserRepository;
import io.github.koryl.contacts.domain.dto.UserDto;
import io.github.koryl.contacts.domain.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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

        users = rawUsers.stream().map(user ->
                new UserDto(user.getId(),
                        user.getFirstName(),
                        user.getLastName(),
                        user.getGender(),
                        user.getBirthDate(),
                        user.getPesel(),
                        user.getContacts()))
                .collect(toList());

        return users;
    }

}
