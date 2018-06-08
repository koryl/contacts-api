package io.github.koryl.contacts.rest;

import io.github.koryl.contacts.domain.dto.user.UserDto;
import io.github.koryl.contacts.service.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(UserController.class)
public class UserControllerTest {

    private List<UserDto> users;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Before
    public void initUserList() {

        UserDto user1 = new UserDto(1, "Jan", "Koewalski", 'M', LocalDate.parse("1950-01-01"), "50010191216", new ArrayList<>());
        UserDto user2 = new UserDto(2, "Anna", "Nowak", 'K', LocalDate.parse("1975-05-30"), "75063092802", new ArrayList<>());
        users = Arrays.asList(user1, user2);
    }

    @Test
    public void shouldReturnAllUsers() throws Exception {

        given(userService.getAllUsers()).willReturn(users);

        mockMvc.perform(get("/users")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].firstName", is(users.get(0).getFirstName())));
    }
}
