package io.github.koryl.contacts.utilities.parser.json;

import io.github.koryl.contacts.domain.entity.user.User;
import io.github.koryl.contacts.domain.entity.contact.Contact;
import io.github.koryl.contacts.utilities.parser.DataParser;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component("jsonDataParser")
public class JsonDataParser implements DataParser {

    public Map<User, List<? extends Contact>> parseUsersWithContacts(String filePath) {

        //TODO
        throw new RuntimeException("Not implemented!");
    }
}
