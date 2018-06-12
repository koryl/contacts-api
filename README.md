# contacts-api

## Purpose
This is example project of REST and SOAP API.

RESTful interface enables of finding/adding/editing/deleting users and user's contacts.

For SOAP WebService it's possible to finding users by an email address.

## Running
To run app after cloning repository, you should first build and package project with command:

`mvn clean package`

It will generate classes for SOAP WebService (they generates automatically with usage of *jaxb2-maven-plugin*).

After that run:

`mvn spring-boot:run`

App will start and will be available at *http://localhost:8080*. 

## Rest methods and endpoints
- *getAllUsers* - **GET** - retrieves all users - ( [/users](http://localhost:8080/users) )
- *getUser* - **GET** - retrieves a specific user by id - ( [/users/{id}](http://localhost:8080/users/1) )
- *createUser* - **POST** - creates a new user - ( [/users](http://localhost:8080/users) )
- *updateUser* - **PUT** - updates user with id - ( [/users/{id}](http://localhost:8080/users/1) )
- *deleteUser* - **DELETE** - deletes user with id - ( [/users/{id}](http://localhost:8080/users/1) )
- *findPeopleByBirthDateBetween* - **GET** - finds people with a birthday in the provided scope - ( [/users/findByBirthDayBetween?fromDate=?&toDate=?](http://localhost:8080/users/findByBirthDayBetween) )
- *findPeopleByEmail* - **GET** - finds people with an exact mail (e.g. *test@test.com*) or part of mail when parametr starts and ends with astersik * - ( [/users/findByEmail?email=?](http://localhost:8080/users/findByEmail?email=*poczta*) )
- *getContactsOfUser* - **GET** - retrieves all user's contacts - ( [/users/{id}/contacts](http://localhost:8080/users/1/contacts) )
- *addNewContactForUser* - **POST** - add a new contact for user - ( [/users/{id}/contacts](http://localhost:8080/users/1/contacts) )
- *updateContactOfUser* - **PUT** - updates a contact of user - ( [/users/{id}/contacts/updateContact?value=?](http://localhost:8080/users/1/contacts/updateContact) )
- *deleteContactOfUser* - **DELETE** - deletes a contact of user - ( [/users/{id}/contacts/deleteContact?value=?](http://localhost:8080/users/1/contacts/deleteContact) )

## SOAP WebService
Import wsdl file when application is running from http://localhost:8080/soap-ws/users.wsdl
- *findPeopleByEmail* - finds people with an exact mail (e.g. *test@test.com*) or part of mail when parametr starts and ends with astersik *


## Used technologies
- Java 8
- Spring Boot
- Spring Data
- H2
- Lombok
- Guava
- AssertJ


###### koryl.github.io 2018