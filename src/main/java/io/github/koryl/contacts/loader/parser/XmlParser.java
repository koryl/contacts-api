package io.github.koryl.contacts.loader.parser;

import io.github.koryl.contacts.domain.entity.User;
import lombok.extern.slf4j.Slf4j;
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
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.*;

@Component("xmlParser")
@Slf4j
public class XmlParser implements Parser {

    public List<User> parseUsers(String filePath) {

        File file = new File(filePath);
        List<User> userList = new ArrayList<>();
        User user = null;
        XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();

        try {

            XMLEventReader xmlEventReader = xmlInputFactory.createXMLEventReader(new FileInputStream(file));

            while (xmlEventReader.hasNext()) {
                XMLEvent xmlEvent = xmlEventReader.nextEvent();

                if (xmlEvent.isStartElement()) {
                    StartElement startElement = xmlEvent.asStartElement();

                    if (isCurrentElement(startElement, "user")) {
                        user = new User();

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
                    }
                }

                if (xmlEvent.isEndElement()) {
                    EndElement endElement = xmlEvent.asEndElement();
                    if (endElement.getName().getLocalPart().equals("user")) {
                        userList.add(user);
                    }
                }
            }
        } catch (FileNotFoundException | XMLStreamException e) {
            log.error("Cannot parse xml document");
            e.printStackTrace();
        }
        return userList;
    }

    private String getData(XMLEvent event) {

        return event.asCharacters().getData();
    }

    private boolean isCurrentElement(StartElement startElement, String name) {

        return startElement.getName().getLocalPart().equals(name);
    }
}
