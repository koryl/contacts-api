package io.github.koryl.contacts.dao;

import io.github.koryl.contacts.domain.entity.EmailAddress;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmailAddressRepository extends CrudRepository<EmailAddress, Long> {
}
