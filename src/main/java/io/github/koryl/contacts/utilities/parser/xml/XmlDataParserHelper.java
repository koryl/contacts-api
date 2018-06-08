package io.github.koryl.contacts.utilities.parser.xml;

import org.springframework.stereotype.Component;

import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

@Component("xmlDataParserHelper")
public class XmlDataParserHelper {

    static boolean isElement(XMLEvent element, String name) {

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

    static String getData(XMLEvent event) {

        return event.asCharacters().getData();
    }
}
