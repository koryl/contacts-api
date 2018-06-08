package io.github.koryl.contacts.utilities.parser.xml;

import io.github.koryl.contacts.domain.entity.user.User;
import io.github.koryl.contacts.domain.entity.user.UserBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.Element;

import java.time.LocalDate;

@Component("xmlDataParserHelper")
public class XmlDataParserHelper {

    private final UserBuilder userBuilder;

    @Autowired
    public XmlDataParserHelper(UserBuilder userBuilder) {
        this.userBuilder = userBuilder;
    }

    String getTextFromElement(Element element, String tagName) {

        return element.getElementsByTagName(tagName).item(0).getTextContent();
    }

    User buildUserFromElement(Element userElement) {

        return userBuilder
                .firstName(getTextFromElement(userElement, "firstName"))
                .lastName(getTextFromElement(userElement, "lastName"))
                .gender(getTextFromElement(userElement, "gender").charAt(0))
                .birthDate(LocalDate.parse(getTextFromElement(userElement, "birthDate")))
                .pesel(getTextFromElement(userElement, "pesel"))
                .createUser();
    }
}
