package io.github.koryl.contacts.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.koryl.contacts.domain.dto.contact.ContactDto;
import io.github.koryl.contacts.domain.dto.contact.EmailAddressDto;
import io.github.koryl.contacts.domain.dto.contact.PhoneNumberDto;
import io.github.koryl.contacts.domain.dto.user.UserDto;
import io.github.koryl.contacts.service.UserServiceImpl;
import org.assertj.core.util.Lists;
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
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserServiceImpl userService;

    private List<UserDto> users;
    private UserDto testUser1;
    private UserDto testUser2;

    @Before
    public void initUsers() {

        List<ContactDto> contacts = Arrays.asList(
                new EmailAddressDto("test@test.com"),
                new PhoneNumberDto("123456789")
        );

        this.testUser1 = new UserDto(1, "Jan", "Kowalski", 'M', LocalDate.parse("1950-01-01"), "50010191216", contacts);
        this.testUser2 = new UserDto(2, "Anna", "Nowak", 'F', LocalDate.parse("1975-05-30"), "75063092802", Lists.emptyList());

        users = Arrays.asList(testUser1, testUser2);
    }

    @Test
    public void shouldReturnAllUsers() throws Exception {

        given(userService.getAllUsers()).willReturn(users);

        mockMvc.perform(get("/users")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].firstName", is(testUser1.getFirstName())))
                .andExpect(jsonPath("$[1].firstName", is(testUser2.getFirstName())))
                .andExpect(jsonPath("$[0].lastName", is(testUser1.getLastName())))
                .andExpect(jsonPath("$[0].gender", is(String.valueOf(testUser1.getGender()))))
                .andExpect(jsonPath("$[0].birthDate", is(testUser1.getBirthDate().toString())))
                .andExpect(jsonPath("$[0].pesel", is(testUser1.getPesel())))
                .andExpect(jsonPath("$[0].contacts", hasSize(2)))
                .andExpect(jsonPath("$[0].contacts[0].value", is("test@test.com")))
                .andExpect(jsonPath("$[0].contacts[1].value", is("123456789")));
    }

    @Test
    public void shouldReturnValidUser() throws Exception {

        given(userService.getUserById(testUser1.getId())).willReturn(testUser1);

        mockMvc.perform(get("/users/{id}", testUser1.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.firstName", is(testUser1.getFirstName())))
                .andExpect(jsonPath("$.lastName", is(testUser1.getLastName())))
                .andExpect(jsonPath("$.gender", is(String.valueOf(testUser1.getGender()))))
                .andExpect(jsonPath("$.birthDate", is(testUser1.getBirthDate().toString())))
                .andExpect(jsonPath("$.pesel", is(testUser1.getPesel())))
                .andExpect(jsonPath("$.contacts", hasSize(2)))
                .andExpect(jsonPath("$.contacts[0].value", is("test@test.com")))
                .andExpect(jsonPath("$.contacts[1].value", is("123456789")));
    }

    @Test
    public void shouldAddValidNewUser() throws Exception {

        String jsonUser = objectMapper.writeValueAsString(testUser1);
        when(userService.createNewUser(testUser1)).thenReturn(testUser1);

        mockMvc.perform(post("/users")
                .content(jsonUser)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.firstName", is(testUser1.getFirstName())))
                .andExpect(jsonPath("$.gender", is(String.valueOf(testUser1.getGender()))));
    }

    @Test
    public void shouldUpdateUser() throws Exception {

        String updatedFirstName = "Stanislaw";
        String updatedLastName = "Nowak";
        UserDto updatedTestUser = new UserDto(1, updatedFirstName, updatedLastName, 'M', LocalDate.parse("1950-01-01"), "50010191216", Lists.emptyList());

        updatedTestUser.setFirstName(updatedFirstName);
        updatedTestUser.setLastName(updatedLastName);
        String jsonUser = objectMapper.writeValueAsString(updatedTestUser);

        when(userService.updateUserWithId(testUser1.getId(), updatedTestUser)).thenReturn(updatedTestUser);

        mockMvc.perform(put("/users/{id}", testUser1.getId())
                .content(jsonUser)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.firstName", is(updatedFirstName)))
                .andExpect(jsonPath("$.lastName", is(updatedLastName)));
    }

    @Test
    public void shouldReturnStatusOkWhenDeleteUser() throws Exception {

        mockMvc.perform(delete("/users/{id}", 1))
                .andExpect(status().isOk());
    }
}
