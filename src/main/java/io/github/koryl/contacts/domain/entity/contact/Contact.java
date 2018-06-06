package io.github.koryl.contacts.domain.entity.contact;

import io.github.koryl.contacts.domain.entity.User;

public interface Contact {

    String value = null;
    User user = null;

    long getId();

    String getValue();

    User getUser();

    void setId(long id);

    void setValue(String value);

    void setUser(User user);
}
