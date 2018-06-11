package io.github.koryl.contacts.dao;

import io.github.koryl.contacts.domain.entity.user.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.junit4.SpringRunner;

import javax.validation.ConstraintViolationException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static io.github.koryl.contacts.TestData.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.ThrowableAssert.catchThrowable;

@RunWith(SpringRunner.class)
@DataJpaTest
public class UserRepositoryTest {

    private final LocalDate minDate = LocalDate.parse("1918-01-01");
    private final LocalDate maxDate = LocalDate.now();


    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @Before
    public void setUpTestUser() {

        testUser = new User(0, FIRST_NAME, LAST_NAME, GENDER, BIRTH_DATE, PESEL);
    }

    @Test
    public void shouldFindUsersWithInBirthDayBetweenProvidedDates() {

        entityManager.persist(testUser);
        entityManager.flush();
        LocalDate from = BIRTH_DATE.minusDays(1);
        LocalDate to = BIRTH_DATE.plusDays(1);

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
        LocalDate from = BIRTH_DATE.plusDays(1);

        List<User> foundUsers = userRepository.findUsersByBirthDateIsGreaterThanEqualAndBirthDateLessThanEqual(from, maxDate);

        assertThat(foundUsers.size()).isEqualTo(0);
    }

    @Test
    public void shouldNotFindUsersWithBirthDayBeforeProvidedToDate() {

        entityManager.persist(testUser);
        entityManager.flush();
        LocalDate to = BIRTH_DATE.minusDays(1);

        List<User> foundUsers = userRepository.findUsersByBirthDateIsGreaterThanEqualAndBirthDateLessThanEqual(minDate, to);

        assertThat(foundUsers.size()).isEqualTo(0);
    }

    @Test
    public void shouldThrowExceptionWhenAlreadyExistingPesel() {

        User testUser1 = new User(0, "Test", "Test", 'M', LocalDate.parse("1950-01-01"), PESEL);
        User testUser2 = new User(0, "Test", "Test", 'M', LocalDate.parse("1950-01-01"), PESEL);

        Throwable thrown = catchThrowable(() -> {
            userRepository.save(testUser1);
            userRepository.save(testUser2);
        });

        assertThat(thrown)
                .isInstanceOfAny(DataIntegrityViolationException.class, ConstraintViolationException.class);
    }

    @Test
    public void shouldFindUserWithPesel() {

        User anotherUser = new User(0,"Anna", "Nowak", 'F', LocalDate.parse("1975-05-30"), "75063092802");
        entityManager.persist(testUser);
        entityManager.persist(anotherUser);
        entityManager.flush();

        Optional<User> foundUser = userRepository.findByPesel(PESEL);

        assertThat(foundUser)
                .contains(testUser);
    }
}
