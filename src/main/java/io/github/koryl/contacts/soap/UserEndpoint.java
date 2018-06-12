package io.github.koryl.contacts.soap;

import io.github.koryl.contacts.domain.dto.contact.ContactDto;
import io.github.koryl.contacts.domain.dto.user.UserDto;
import io.github.koryl.contacts.service.user.UserService;
import io.github.koryl.contacts.soap.ws.Contact;
import io.github.koryl.contacts.soap.ws.FindPeopleByEmailRequest;
import io.github.koryl.contacts.soap.ws.FindPeopleByEmailResponse;
import io.github.koryl.contacts.soap.ws.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import java.util.List;
import java.util.stream.Collectors;

@Endpoint
public class UserEndpoint {

    private static final String NAMESPACE_URI = "http://localhost:8080/users-ws";

    private UserService userService;

    @Autowired
    public UserEndpoint(UserService userService) {
        this.userService = userService;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "findPeopleByEmailRequest")
    @ResponsePayload
    public FindPeopleByEmailResponse findPeopleByEmail(@RequestPayload FindPeopleByEmailRequest request) {

        FindPeopleByEmailResponse response = new FindPeopleByEmailResponse();
        List<UserDto> users = userService.findPeopleByEmail(request.getValue());
        System.out.println(users);
        List<User> wsUsers = users.stream().map(userDto -> {
            User mappedUser = new User();

            try {
                mappedUser.setId(userDto.getId());
                mappedUser.setFirstName(userDto.getFirstName());
                mappedUser.setLastName(userDto.getLastName());
                mappedUser.setGender(String.valueOf(userDto.getGender()));
                mappedUser.setBirthDate(DatatypeFactory.newInstance().newXMLGregorianCalendar(userDto.getBirthDate().toString()));
                mappedUser.setPesel(userDto.getPesel());
                userDto.getContacts().forEach(con -> {
                    Contact contact = new Contact();
                    contact.setContactType(con.getContactType().name());
                    contact.setValue(con.getValue());
                    mappedUser.getContacts().add(contact);
                });

            } catch (DatatypeConfigurationException e) {
                throw new RuntimeException("");
            }
            return mappedUser;
        }).collect(Collectors.toList());
        
        response.getUser().addAll(wsUsers);

        return response;
    }
}
