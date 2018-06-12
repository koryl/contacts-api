package io.github.koryl.contacts.rest;

import io.github.koryl.contacts.domain.dto.user.UserDto;
import io.github.koryl.contacts.service.user.UserService;
import io.github.koryl.contacts.service.user.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserServiceImpl userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<UserDto> getAllUsers() {

        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public UserDto getUser(@PathVariable("id") Long id) {

        return userService.getUserById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto createUser(@Valid @RequestBody UserDto user) {

        return userService.createNewUser(user);
    }

    @PutMapping("/{id}")
    public UserDto updateUser(@PathVariable("id") Long id, @Valid @RequestBody UserDto user) {

        return userService.updateUserWithId(id, user);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteUser(@PathVariable("id") Long id) {

        userService.deleteUserWithId(id);
    }

    @GetMapping("/findByBirthDayBetween")
    public List<UserDto> findPeopleByBirthDateBetween(@Valid @RequestParam(required = false) String fromDate,
                                                      @Valid @RequestParam(required = false) String toDate) {

        return userService.findPeopleByBirthDateBetween(fromDate, toDate);
    }

    @GetMapping("/findByEmail")
    public List<UserDto> findPeopleByEmail(@Valid @RequestParam String email) {

        return userService.findPeopleByEmail(email);
    }
}
