package io.github.koryl.contacts.utilities.parser.xml;

import io.github.koryl.contacts.domain.entity.user.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

@Component("userXmlDataParser")
@Slf4j
public class UserXmlDataParser {

    private final XmlDataParserHelper parserHelper;

    @Autowired
    public UserXmlDataParser(XmlDataParserHelper parserHelper) {

        this.parserHelper = parserHelper;
    }

    List<User> parseUsers(Element root) {

        List<User> users = new ArrayList<>();

        NodeList nList = root.getElementsByTagName("user");

        for (int i = 0; i < nList.getLength(); i++) {
            Node node = nList.item(i);

            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element userElement = (Element) node;

                User user = parserHelper.buildUserFromElement(userElement);
                users.add(user);
            }
        }
        return users;
    }
}
