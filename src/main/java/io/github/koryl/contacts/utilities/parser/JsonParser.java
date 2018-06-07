package io.github.koryl.contacts.utilities.parser;

import io.github.koryl.contacts.domain.entity.User;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class JsonParser implements Parser {

    public List<User> parseUsers(String filePath) {

        //TODO
        throw new RuntimeException("Not implemented!");
    }
}
