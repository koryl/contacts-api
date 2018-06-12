package io.github.koryl.contacts.dao;

import io.github.koryl.contacts.domain.entity.contact.PhoneNumber;
import io.github.koryl.contacts.domain.entity.user.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PhoneNumberRepository extends CrudRepository<PhoneNumber, Long> {

    List<PhoneNumber> findByUser(User user);
    Optional<PhoneNumber> findByValue(String value);
}
