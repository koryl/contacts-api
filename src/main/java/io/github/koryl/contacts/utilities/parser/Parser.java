package io.github.koryl.contacts.utilities.parser;

import io.github.koryl.contacts.domain.entity.User;

import java.util.List;

public interface Parser {

    List<User> parseUsers(String filePath);
}
