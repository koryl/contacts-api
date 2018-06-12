package io.github.koryl.contacts;

import java.time.LocalDate;

public class TestData {

    public static final String FIRST_NAME = "Jan";
    public static final String LAST_NAME = "Kowalski";
    public static final char GENDER = 'M';
    public static final LocalDate BIRTH_DATE = LocalDate.parse("1950-01-01");
    public static final String PESEL = "50010191216";
    public static final String EMAIL_ADDRESS_VALUE = "test@test.com";
    public static final String PHONE_NUMBER_VALUE = "123456789";

    public static final LocalDate MIN_DATE = LocalDate.parse("1918-01-01");
    public static final LocalDate MAX_DATE = LocalDate.now();
}
