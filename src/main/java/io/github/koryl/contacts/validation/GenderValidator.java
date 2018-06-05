package io.github.koryl.contacts.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class GenderValidator implements ConstraintValidator<Gender, Character> {

    public boolean isValid(Character value, ConstraintValidatorContext context) {
        if (value == null) return false;
        else return (value.equals('F') || value.equals('M'));
    }
}
