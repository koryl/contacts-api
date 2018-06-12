package io.github.koryl.contacts.domain.entity.contact;

import io.github.koryl.contacts.domain.entity.user.User;

public interface Contact {

    long getId();

    String getValue();

    User getUser();

    void setId(long id);

    void setValue(String value);

    void setUser(User user);
}
