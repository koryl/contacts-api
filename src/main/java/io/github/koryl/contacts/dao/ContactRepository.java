package io.github.koryl.contacts.dao;

import io.github.koryl.contacts.domain.entity.Contact;
import io.github.koryl.contacts.domain.entity.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContactRepository extends CrudRepository<Contact, Long> {

    List<Contact> findByUser(User user);
}
