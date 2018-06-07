package io.github.koryl.contacts.loader;

import io.github.koryl.contacts.dao.EmailAddressRepository;
import io.github.koryl.contacts.dao.PhoneNumberRepository;
import io.github.koryl.contacts.dao.UserRepository;
import io.github.koryl.contacts.domain.entity.User;
import io.github.koryl.contacts.domain.entity.contact.Contact;
import io.github.koryl.contacts.domain.entity.contact.EmailAddress;
import io.github.koryl.contacts.domain.entity.contact.PhoneNumber;
import io.github.koryl.contacts.utilities.parser.DataParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static java.lang.System.*;

@Component
@Slf4j
public class DataLoader implements ApplicationRunner {

    private final DataParser dataParser;
    private final UserRepository userRepository;
    private final EmailAddressRepository emailAddressRepository;
    private final PhoneNumberRepository phoneNumberRepository;

    @Autowired
    public DataLoader(UserRepository userRepository, @Qualifier("xmlDataParser") DataParser dataParser, EmailAddressRepository emailAddressRepository, PhoneNumberRepository phoneNumberRepository) {

        this.userRepository = userRepository;
        this.dataParser = dataParser;
        this.emailAddressRepository = emailAddressRepository;
        this.phoneNumberRepository = phoneNumberRepository;
    }

    public void run(ApplicationArguments args) throws Exception {

        Map<User, List<? extends Contact>> userListMap = dataParser.parseUsers(getDataFilePath());

        for (Map.Entry<User, List<? extends Contact>> entry : userListMap.entrySet()) {

            userRepository.save(entry.getKey());
            entry.getValue().forEach(contact -> {
                if (contact instanceof EmailAddress) {
                    emailAddressRepository.save((EmailAddress)contact);
                } else if (contact instanceof PhoneNumber) {
                    phoneNumberRepository.save((PhoneNumber) contact);
                } else {
                    throw new RuntimeException("Cannot recognize of type of a parsed contact.");
                }
            });
        }
        log.info("Users database was initialized from file.");
    }

    /**
     * Method for getting path of file for data initialization use. To use it provide a parameter 'dataFilePath' during,
     * starting project, otherwise it will be used default location.
     *
     * @return path of file to initialize data.
     */
    private String getDataFilePath() {

        String path = getProperty("dataFilePath");

        if (Objects.equals(path, "") || Objects.isNull(path)) {
            path = getProperty("user.dir") + "/src/main/resources/data/test_users.xml";
        }
        log.debug("Data file was loaded: " + path);

        return path;
    }
}
