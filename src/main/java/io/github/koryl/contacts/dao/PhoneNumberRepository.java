package io.github.koryl.contacts.dao;

import io.github.koryl.contacts.domain.entity.PhoneNumber;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PhoneNumberRepository extends CrudRepository<PhoneNumber, Long> {
}
