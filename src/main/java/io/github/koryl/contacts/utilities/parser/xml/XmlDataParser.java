package io.github.koryl.contacts.utilities.parser.xml;

import io.github.koryl.contacts.domain.entity.User;
import io.github.koryl.contacts.domain.entity.contact.Contact;
import io.github.koryl.contacts.domain.entity.contact.ContactFactory;
import io.github.koryl.contacts.utilities.parser.DataParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;

@Component("xmlDataParser")
@Slf4j
public class XmlDataParser implements DataParser {

    private final UserXmlDataParser userParser;
    private final ContactXmlDataParser contactParser;

    @Autowired
    public XmlDataParser(UserXmlDataParser userParser, ContactXmlDataParser contactParser) {
        this.userParser = userParser;
        this.contactParser = contactParser;
    }

    public Map<User, List<? extends Contact>> parseUsersWithContacts(String filePath) {

        File file = new File(filePath);
        Map<User, List<? extends Contact>> userListMap = new HashMap<>();

        try {
            XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
            XMLEventReader xmlEventReader = xmlInputFactory.createXMLEventReader(new FileInputStream(file));

            userParser.parseUsers(xmlEventReader);
            contactParser.parseContacts(xmlEventReader);
        } catch (FileNotFoundException | XMLStreamException e) {
            e.printStackTrace();
        }
        return userListMap;
    }
}