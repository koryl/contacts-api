package io.github.koryl.contacts.dao;

import io.github.koryl.contacts.domain.entity.user.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {

    List<User> findUsersByBirthDateIsGreaterThanEqualAndBirthDateLessThanEqual(LocalDate from, LocalDate to);
    Optional<User> findByPesel(String pesel);
}
