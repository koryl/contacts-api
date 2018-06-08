package io.github.koryl.contacts.utilities.parser.xml;

import io.github.koryl.contacts.domain.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static io.github.koryl.contacts.utilities.parser.xml.XmlDataParserHelper.getData;
import static io.github.koryl.contacts.utilities.parser.xml.XmlDataParserHelper.isElement;
import static java.util.Objects.requireNonNull;

@Component("userXmlDataParser")
@Slf4j
public class UserXmlDataParser {

    public List<User> parseUsers(XMLEventReader xmlEventReader) {

        User user = null;
        List<User> users = new ArrayList<>();

        try {
            while (xmlEventReader.hasNext()) {

                XMLEvent xmlEvent = xmlEventReader.nextEvent();
                if (xmlEvent.isStartElement()) {
                    StartElement startElement = xmlEvent.asStartElement();

                    if (isElement(startElement, "user")) {
                        user = new User();

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

                    }
                }
                if (xmlEvent.isEndElement()) {

                    EndElement endElement = xmlEvent.asEndElement();

                    if (isElement(endElement, "user")) {
                        users.add(user);
                    }
                }
            }
        } catch (XMLStreamException e) {

            log.error("Cannot parse xml document");
            e.printStackTrace();
        }
        return users;
    }
}
