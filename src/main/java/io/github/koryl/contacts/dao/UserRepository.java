package io.github.koryl.contacts.dao;

import io.github.koryl.contacts.domain.entity.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {

    List<User> findUsersByBirthDateAfterAndBirthDateBefore(LocalDate from, LocalDate to);
}
