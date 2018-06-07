package io.github.koryl.contacts.utilities.parser;

import io.github.koryl.contacts.domain.entity.User;
import io.github.koryl.contacts.domain.entity.contact.Contact;

import java.util.List;
import java.util.Map;

public interface DataParser {

    Map<User, List<? extends Contact>> parseUsers (String filePath);
}
