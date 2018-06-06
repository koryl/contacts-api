package io.github.koryl.contacts.dao;

import io.github.koryl.contacts.domain.entity.contact.EmailAddress;
import io.github.koryl.contacts.domain.entity.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmailAddressRepository extends CrudRepository<EmailAddress, Long> {

    List<EmailAddress> findByUser(User user);
}
