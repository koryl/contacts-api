package io.github.koryl.contacts.domain.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.List;

@Entity
@Data
public class User {

    @Id
    private long userId;

    private String firstName;
    private String lastName;
    private String gender;
    private String birthDate;
    private String pesel;
    private List<Contact> contacts;
}
