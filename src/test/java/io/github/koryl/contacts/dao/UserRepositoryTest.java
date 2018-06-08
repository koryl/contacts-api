package io.github.koryl.contacts.dao;

import io.github.koryl.contacts.domain.entity.user.User;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    User testUser;

    @Before
    public void setUpTestUser() {

        testUser = new User(0, "Jan", "Kowalski", 'M', LocalDate.parse("1950-01-01"), "50010191216");
    }

    @Test
    public void shouldFindUsersWithInBirthDayBetweenProvidedDates() {

        entityManager.persist(testUser);
        entityManager.flush();
        LocalDate from = LocalDate.parse("1949-12-31");
        LocalDate to = LocalDate.parse("1950-01-02");

        List<User> foundUsers = userRepository.findUsersByBirthDateIsGreaterThanEqualAndBirthDateLessThanEqual(from, to);

        assertThat(foundUsers.size()).isGreaterThan(0);
        assertThat(foundUsers.get(0))
                .isNotNull()
                .isEqualTo(testUser);
    }

    @Test
    public void shouldNotFindUsersWithBirthDayAfterProvidedFromDate() {

        entityManager.persist(testUser);
        entityManager.flush();
        LocalDate from = LocalDate.parse("1950-01-02");
        LocalDate to = LocalDate.now();

        List<User> foundUsers = userRepository.findUsersByBirthDateIsGreaterThanEqualAndBirthDateLessThanEqual(from, to);

        assertThat(foundUsers.size()).isEqualTo(0);
    }

    @Test
    public void shouldNotFindUsersWithBirthDayBeforeProvidedToDate() {

        entityManager.persist(testUser);
        entityManager.flush();
        LocalDate from = LocalDate.parse("1918-01-01");
        LocalDate to = LocalDate.parse("1949-12-31");

        List<User> foundUsers = userRepository.findUsersByBirthDateIsGreaterThanEqualAndBirthDateLessThanEqual(from, to);

        assertThat(foundUsers.size()).isEqualTo(0);
    }
}
