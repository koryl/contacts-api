package io.github.koryl.contacts.validation;


import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * The annotated element must be date from past, not older then min date value (default: 1918-01-01).
 */
@Documented
@Constraint(validatedBy = CorrectDateRangeValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface InCorrectDateRange {
    String message() default "{io.github.koryl.contacts.validation.InCorrectDateRange.message}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    String min() default "1918-01-01";
}