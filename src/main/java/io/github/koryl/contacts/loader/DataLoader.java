package io.github.koryl.contacts.loader;

import io.github.koryl.contacts.dao.UserRepository;
import io.github.koryl.contacts.domain.entity.User;
import io.github.koryl.contacts.loader.parser.Parser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

import static java.lang.System.*;

@Component
@Slf4j
public class DataLoader implements ApplicationRunner {

    private final Parser parser;
    private final UserRepository userRepository;

    @Autowired
    public DataLoader(UserRepository userRepository, @Qualifier("xmlParser") Parser parser) {
        this.userRepository = userRepository;
        this.parser = parser;
    }

    public void run(ApplicationArguments args) throws Exception {

        List<User> users = parser.parseUsers(getDataFilePath());
        saveAllUsers(users);
        log.info("Users database was initialized from file.");
    }

    private void saveAllUsers(List<User> users) {

        userRepository.saveAll(users);
    }

    /**
     * Method for getting path of file for data initialization use. To use it provide as parameter 'dataFilePath',
     * otherwise it will be used default location.
     *
     * @return path of file to initialize data.
     */
    private String getDataFilePath() {

        String path = getProperty("dataFilePath");

        if (Objects.equals(path, "") || Objects.isNull(path)) {
            path = getProperty("user.dir") + "/src/main/resources/data/test_users.xml";
        }

        return path;
    }
}
