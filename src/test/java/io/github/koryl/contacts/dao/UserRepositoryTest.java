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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.ThrowableAssert.catchThrowable;

@RunWith(SpringRunner.class)
@DataJpaTest
public class UserRepositoryTest {

    private final String firstName = "Jan";
    private final String lastName = "Kowalski";
    private final char gender = 'M';
    private final LocalDate birthDate = LocalDate.parse("1950-01-01");
    private final String pesel = "50010191216";

    private final LocalDate minDate = LocalDate.parse("1918-01-01");
    private final LocalDate maxDate = LocalDate.now();


    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @Before
    public void setUpTestUser() {

        testUser = new User(0, firstName, lastName, gender, birthDate, pesel);
    }

    @Test
    public void shouldFindUsersWithInBirthDayBetweenProvidedDates() {

        entityManager.persist(testUser);
        entityManager.flush();
        LocalDate from = birthDate.minusDays(1);
        LocalDate to = birthDate.plusDays(1);

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
        LocalDate from = birthDate.plusDays(1);

        List<User> foundUsers = userRepository.findUsersByBirthDateIsGreaterThanEqualAndBirthDateLessThanEqual(from, maxDate);

        assertThat(foundUsers.size()).isEqualTo(0);
    }

    @Test
    public void shouldNotFindUsersWithBirthDayBeforeProvidedToDate() {

        entityManager.persist(testUser);
        entityManager.flush();
        LocalDate to = birthDate.minusDays(1);

        List<User> foundUsers = userRepository.findUsersByBirthDateIsGreaterThanEqualAndBirthDateLessThanEqual(minDate, to);

        assertThat(foundUsers.size()).isEqualTo(0);
    }

    @Test
    public void shouldThrowExceptionWhenAlreadyExistingPesel() {

        User testUser1 = new User(0, "", "", 'M', LocalDate.parse("1950-01-01"), pesel);
        User testUser2 = new User(0, "", "", 'M', LocalDate.parse("1950-01-01"), pesel);

        Throwable thrown = catchThrowable(() -> {
            userRepository.save(testUser1);
            userRepository.save(testUser2);
        });

        assertThat(thrown)
                .isInstanceOfAny(DataIntegrityViolationException.class, ConstraintViolationException.class);
    }
}
