package io.github.koryl.contacts.dao;

import io.github.koryl.contacts.domain.entity.contact.EmailAddress;
import io.github.koryl.contacts.domain.entity.user.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmailAddressRepository extends CrudRepository<EmailAddress, Long> {

    List<EmailAddress> findByUser(User user);
    Optional<EmailAddress> findByValue(String value);
}
