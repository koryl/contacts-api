package io.github.koryl.contacts.utilities.parser.xml;

import io.github.koryl.contacts.domain.entity.user.User;
import io.github.koryl.contacts.domain.entity.contact.Contact;
import io.github.koryl.contacts.utilities.parser.DataParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

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

        Map<User, List<? extends Contact>> userListMap = new HashMap<>();

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new File(filePath));

            document.getDocumentElement().normalize();

            Element root = document.getDocumentElement();
            List<User> users = userParser.parseUsers(root);
            List<Contact> contacts = contactParser.parseContacts(root);

            users.forEach(user -> {
                List<Contact> userContacts = contacts
                        .stream()
                        .filter(contact -> Objects.equals(contact.getUser(), user))
                        .collect(Collectors.toList());
                userListMap.put(user, userContacts);
            });

        } catch (ParserConfigurationException | SAXException | IOException e) {
            log.error("It was occurred error during parsing xml file.", e);
        }
        return userListMap;
    }
}