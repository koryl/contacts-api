package io.github.koryl.contacts.dao;

import io.github.koryl.contacts.domain.entity.contact.EmailAddress;
import io.github.koryl.contacts.domain.entity.user.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Optional;

import static io.github.koryl.contacts.TestData.*;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
public class EmailAddressRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private EmailAddressRepository emailAddressRepository;

    private EmailAddress testEmail;
    private User testUser;

    @Before
    public void setUpTestUser() {

        testUser = new User(0, FIRST_NAME, LAST_NAME, GENDER, BIRTH_DATE, PESEL);
        testEmail = new EmailAddress(0, EMAIL_ADDRESS_VALUE, testUser);

        entityManager.persist(testUser);
        entityManager.flush();
    }

    @Test
    public void shouldFindByUser() {

        entityManager.persist(testEmail);
        entityManager.flush();

        List<EmailAddress> foundEmails = emailAddressRepository.findByUser(testUser);

        assertThat(foundEmails)
                .hasSize(1)
                .contains(testEmail);
    }

    @Test
    public void findByUserShouldReturnEmptyListWhenUserDoNotHaveEmails() {

        List<EmailAddress> foundEmails = emailAddressRepository.findByUser(testUser);

        assertThat(foundEmails).hasSize(0);
    }

    @Test
    public void shouldFindByValue() {

        entityManager.persist(testEmail);
        entityManager.flush();

        Optional<EmailAddress> foundEmail = emailAddressRepository.findByValue(EMAIL_ADDRESS_VALUE);

        assertThat(foundEmail)
                .isNotNull()
                .contains(testEmail);
    }
}
