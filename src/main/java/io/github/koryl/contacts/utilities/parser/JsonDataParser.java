package io.github.koryl.contacts.utilities.parser;

import io.github.koryl.contacts.domain.entity.User;
import io.github.koryl.contacts.domain.entity.contact.Contact;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component("jsonDataParser")
public class JsonDataParser implements DataParser {

    public Map<User, List<? extends Contact>> parseUsers(String filePath) {

        //TODO
        throw new RuntimeException("Not implemented!");
    }
}
