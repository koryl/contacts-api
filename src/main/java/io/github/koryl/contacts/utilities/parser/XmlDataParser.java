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

                    if (isCurrentElement(startElement, "user")) {

                        user = new User();
                        contacts = new ArrayList<>();

                    } else if (isCurrentElement(startElement, "firstName")) {

                        xmlEvent = xmlEventReader.nextEvent();
                        requireNonNull(user).setFirstName(getData(xmlEvent));

                    } else if (isCurrentElement(startElement, "lastName")) {

                        xmlEvent = xmlEventReader.nextEvent();
                        requireNonNull(user).setLastName(getData(xmlEvent));

                    } else if (isCurrentElement(startElement, "gender")) {

                        xmlEvent = xmlEventReader.nextEvent();
                        requireNonNull(user).setGender(getData(xmlEvent).charAt(0));

                    } else if (isCurrentElement(startElement, "birthDate")) {

                        xmlEvent = xmlEventReader.nextEvent();
                        requireNonNull(user).setBirthDate(LocalDate.parse(getData(xmlEvent)));

                    } else if (isCurrentElement(startElement, "pesel")) {

                        xmlEvent = xmlEventReader.nextEvent();
                        requireNonNull(user).setPesel(getData(xmlEvent));

                    } else if (isCurrentElement(startElement, "contacts")) {

                        xmlEvent = xmlEventReader.nextEvent();
                        addContacts(xmlEventReader, contacts);
                    }
                }

                if (xmlEvent.isEndElement()) {

                    EndElement endElement = xmlEvent.asEndElement();

                    if (endElement.getName().getLocalPart().equals("user")) {
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

    private boolean isCurrentElement(StartElement startElement, String name) {

        return startElement.getName().getLocalPart().equals(name);
    }

    private List<Contact> addContacts(XMLEventReader xmlEventReader, List<Contact> contacts) {

        Contact contact = null;


        try {
            XMLEvent xmlEvent = xmlEventReader.peek();

            while (xmlEvent.isEndElement() ? !Objects.equals(xmlEvent.asEndElement().getName().getLocalPart(), "contacts") : true) {

                xmlEvent = xmlEventReader.nextEvent();

                if (xmlEvent.isStartElement()) {

                    StartElement startElement = xmlEvent.asStartElement();

                    if (isCurrentElement(startElement, "contact")) {

                        contacts = new ArrayList<>();

                    } else if (isCurrentElement(startElement, "contactType")) {

                        xmlEvent = xmlEventReader.nextEvent();

                        switch (getData(xmlEvent)) {

                            case "EMAIL_ADDRESS":
                                contact = contactFactory.getContact(ContactType.EMAIL_ADDRESS);
                                break;
                            case "PHONE_NUMBER":
                                contact = contactFactory.getContact(ContactType.PHONE_NUMBER);
                                break;
                        }

                    } else if (isCurrentElement(startElement, "value")) {

                        xmlEvent = xmlEventReader.nextEvent();
                        requireNonNull(contact).setValue(getData(xmlEvent));
                    }

                    if (xmlEvent.isEndElement()) {

                        EndElement endElement = xmlEvent.asEndElement();

                        if (endElement.getName().getLocalPart().equals("contact")) {
                            contacts.add(contact);
                        }
                    }
                }
            }
        } catch (XMLStreamException e) {
            e.printStackTrace();
        }
        return contacts;
    }
}