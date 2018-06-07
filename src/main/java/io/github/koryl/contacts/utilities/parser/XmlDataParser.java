package io.github.koryl.contacts.utilities.parser;

import io.github.koryl.contacts.domain.ContactType;
import io.github.koryl.contacts.domain.entity.User;
import io.github.koryl.contacts.domain.entity.contact.Contact;
import io.github.koryl.contacts.domain.entity.contact.ContactFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.util.*;

import static java.util.Objects.*;

@Component("xmlDataParser")
@Slf4j
public class XmlDataParser implements DataParser {

    private final ContactFactory contactFactory;

    @Autowired
    public XmlDataParser(ContactFactory contactFactory) {
        this.contactFactory = contactFactory;
    }

    public Map<User, List<? extends Contact>> parseUsers(String filePath) {

        File file = new File(filePath);
        Map<User, List<? extends Contact>> userListMap = new HashMap<>();
        User user = null;
        List<Contact> contacts = new ArrayList<>();
        XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();

        try {
            XMLEventReader xmlEventReader = xmlInputFactory.createXMLEventReader(new FileInputStream(file));

            while (xmlEventReader.hasNext()) {

                XMLEvent xmlEvent = xmlEventReader.nextEvent();
                if (xmlEvent.isStartElement()) {
                    StartElement startElement = xmlEvent.asStartElement();

                    if (isElement(startElement, "user")) {

                        user = new User();
                        contacts = new ArrayList<>();

                    } else if (isElement(startElement, "firstName")) {

                        xmlEvent = xmlEventReader.nextEvent();
                        requireNonNull(user).setFirstName(getData(xmlEvent));

                    } else if (isElement(startElement, "lastName")) {

                        xmlEvent = xmlEventReader.nextEvent();
                        requireNonNull(user).setLastName(getData(xmlEvent));

                    } else if (isElement(startElement, "gender")) {

                        xmlEvent = xmlEventReader.nextEvent();
                        requireNonNull(user).setGender(getData(xmlEvent).charAt(0));

                    } else if (isElement(startElement, "birthDate")) {

                        xmlEvent = xmlEventReader.nextEvent();
                        requireNonNull(user).setBirthDate(LocalDate.parse(getData(xmlEvent)));

                    } else if (isElement(startElement, "pesel")) {

                        xmlEvent = xmlEventReader.nextEvent();
                        requireNonNull(user).setPesel(getData(xmlEvent));

                    } else if (isElement(startElement, "contacts")) {

                        contacts = addContacts(xmlEventReader, contacts);
                    }
                }

                if (xmlEvent.isEndElement()) {

                    EndElement endElement = xmlEvent.asEndElement();

                    if (isElement(endElement, "user")) {
                        userListMap.put(user, contacts);
                    }
                }
            }
        } catch (FileNotFoundException | XMLStreamException e) {

            log.error("Cannot parse xml document");
            e.printStackTrace();
        }
        return userListMap;
    }

    private String getData(XMLEvent event) {

        return event.asCharacters().getData();
    }

    private boolean isElement(XMLEvent element, String name) {

        if (element instanceof StartElement) {

            StartElement startElement = (StartElement) element;
            return startElement.getName().getLocalPart().equals(name);

        } else if (element instanceof EndElement) {

            EndElement endElement = (EndElement) element;
            return endElement.getName().getLocalPart().equals(name);

        } else {

            throw new RuntimeException("Bad element type provided.");
        }
    }

    private List<Contact> addContacts(XMLEventReader xmlEventReader, List<Contact> contacts) {

        Contact contact = null;

        try {
            XMLEvent xmlEvent = xmlEventReader.nextEvent();

            while (xmlEventReader.hasNext()) {

                if (xmlEvent.isEndElement()) {

                    EndElement element = xmlEvent.asEndElement();
                    if (isElement(element, "contacts")) break;
                }
                xmlEvent = xmlEventReader.nextEvent();

                if (xmlEvent.isStartElement()) {

                    StartElement startElement = xmlEvent.asStartElement();

                    if (isElement(startElement, "contact")) {

                    } else if (isElement(startElement, "contactType")) {

                        xmlEvent = xmlEventReader.nextEvent();

                        switch (getData(xmlEvent)) {
                            case "EMAIL_ADDRESS":
                                contact = contactFactory.getContact(ContactType.EMAIL_ADDRESS);
                                break;
                            case "PHONE_NUMBER":
                                contact = contactFactory.getContact(ContactType.PHONE_NUMBER);
                                break;
                        }

                    } else if (isElement(startElement, "value")) {

                        xmlEvent = xmlEventReader.nextEvent();
                        requireNonNull(contact).setValue(getData(xmlEvent));
                    }
                }

                if (xmlEvent.isEndElement()) {
                    EndElement endElement = xmlEvent.asEndElement();

                    if (isElement(endElement, "contact")) {
                        contacts.add(contact);
                    }
                }
            }
        } catch (XMLStreamException e) {
            log.error("Error when parsing xml file occurred.");
            e.printStackTrace();
        }
        return contacts;
    }
}