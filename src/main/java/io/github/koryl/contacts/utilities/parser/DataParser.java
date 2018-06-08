package io.github.koryl.contacts.utilities.parser;

import io.github.koryl.contacts.domain.entity.user.User;
import io.github.koryl.contacts.domain.entity.contact.Contact;

import java.util.List;
import java.util.Map;

public interface DataParser {

    Map<User, List<? extends Contact>> parseUsersWithContacts(String filePath);
}
