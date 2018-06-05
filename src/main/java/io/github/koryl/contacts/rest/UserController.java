package io.github.koryl.contacts.rest;

import io.github.koryl.contacts.domain.dto.UserDto;
import io.github.koryl.contacts.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<UserDto> allUsers() {

        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public UserDto getUser(@PathVariable("id") Long id) {

        return userService.getUserById(id);
    }

    @PostMapping
    public UserDto createUser(@Valid @RequestBody UserDto user) {

        return userService.createNewUser(user);
    }

    @PutMapping("/{id}")
    public UserDto updateUser(@PathVariable("id") Long id, @Valid @RequestBody UserDto user) {

        return userService.updateUserWithId(id, user);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable("id") Long id) {

        userService.deleteUserWithId(id);

        return ResponseEntity.ok().build();
    }

}
