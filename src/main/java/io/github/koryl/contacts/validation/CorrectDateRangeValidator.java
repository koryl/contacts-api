package io.github.koryl.contacts.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class CorrectDateRangeValidator implements ConstraintValidator<InCorrectDateRange, LocalDate> {

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private InCorrectDateRange constraintAnnotation;

    public void initialize(InCorrectDateRange constraintAnnotation) {
        this.constraintAnnotation = constraintAnnotation;
    }

    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {

        final LocalDate min = LocalDate.parse(constraintAnnotation.min(), dateFormatter);
        final LocalDate max = LocalDate.now();
        return value == null || (value.isAfter(min) && value.isBefore(max));
    }
}
