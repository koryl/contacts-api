package io.github.koryl.contacts.utilities.parser.xml;

import io.github.koryl.contacts.domain.ContactType;
import io.github.koryl.contacts.domain.entity.contact.Contact;
import io.github.koryl.contacts.domain.entity.contact.ContactFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.util.ArrayList;
import java.util.List;

import static io.github.koryl.contacts.utilities.parser.xml.XmlDataParserHelper.getData;
import static io.github.koryl.contacts.utilities.parser.xml.XmlDataParserHelper.isElement;
import static java.util.Objects.requireNonNull;

@Component("contactXmlDataParser")
@Slf4j
public class ContactXmlDataParser {

    private final ContactFactory contactFactory;

    @Autowired
    public ContactXmlDataParser(ContactFactory contactFactory) {
        this.contactFactory = contactFactory;
    }

    public List<Contact> parseContacts(XMLEventReader xmlEventReader) {

        List<Contact> contacts = new ArrayList<>();
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

                        switch (startElement.getAttributeByName(QName.valueOf("contactType")).getName().getLocalPart()) {
                            case "EMAIL_ADDRESS":
                                contact = contactFactory.getContact(ContactType.EMAIL_ADDRESS);
                                break;
                            case "PHONE_NUMBER":
                                contact = contactFactory.getContact(ContactType.PHONE_NUMBER);
                                break;
                            default:
                                throw new RuntimeException("Contact type was not recognized.");
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
