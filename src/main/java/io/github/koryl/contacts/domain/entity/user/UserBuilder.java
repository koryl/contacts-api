package io.github.koryl.contacts.domain.entity.user;

import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class UserBuilder {

    private long id;
    private String firstName;
    private String lastName;
    private char gender;
    private LocalDate birthDate;
    private String pesel;

    public UserBuilder() {
    }

    public UserBuilder id(long id) {

        this.id = id;
        return this;
    }

    public UserBuilder firstName(String firstName) {

        this.firstName = firstName;
        return this;
    }

    public UserBuilder lastName(String lastName) {

        this.lastName = lastName;
        return this;
    }

    public UserBuilder gender(char gender) {

        this.gender = gender;
        return this;
    }

    public UserBuilder birthDate(LocalDate birthDate) {

        this.birthDate = birthDate;
        return this;
    }

    public UserBuilder pesel(String pesel) {

        this.pesel = pesel;
        return this;
    }


    public User createUser() {

        return new User(id, firstName, lastName, gender, birthDate, pesel);
    }
}
