package io.github.koryl.contacts.utilities.parser.xml;

import io.github.koryl.contacts.domain.ContactType;
import io.github.koryl.contacts.domain.entity.contact.Contact;
import io.github.koryl.contacts.domain.entity.contact.ContactFactory;
import io.github.koryl.contacts.domain.entity.user.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;


@Component("contactXmlDataParser")
@Slf4j
public class ContactXmlDataParser {

    private final XmlDataParserHelper parserHelper;
    private final ContactFactory contactFactory;

    @Autowired
    public ContactXmlDataParser(ContactFactory contactFactory, XmlDataParserHelper parserHelper) {
        this.contactFactory = contactFactory;
        this.parserHelper = parserHelper;
    }

    List<Contact> parseContacts(Element root) {

        List<Contact> contacts = new ArrayList<>();

        NodeList contactNodeList = root.getElementsByTagName("contact");

        for (int i = 0; i < contactNodeList.getLength(); i++) {
            Node node = contactNodeList.item(i);
            Node userNode = node.getParentNode().getParentNode();
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element contactElement = (Element) node;
                Element userElement = (Element) userNode;
                Contact contact;
                User user = parserHelper.buildUserFromElement(userElement);
                String value = parserHelper.getTextFromElement(contactElement, "value");

                switch (contactElement.getAttribute("contactType")) {
                    case "EMAIL_ADDRESS":
                        contact = contactFactory.getContact(ContactType.EMAIL_ADDRESS, value, user);
                        break;
                    case "PHONE_NUMBER":
                        contact = contactFactory.getContact(ContactType.PHONE_NUMBER, value, user);
                        break;
                    default:
                        throw new RuntimeException("Contact type was not recognized.");
                }
                contacts.add(contact);
            }
        }
        return contacts;
    }
}
