package com.doc.mamagement.utility.validation.general;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

public class GeneralRuleAnnotation {
    /**
     * Validation annotation to validate that 2 fields have the same value.
     * An array of fields and their matching confirmation fields can be supplied.
     * Example, compare 1 pair of fields:
     *
     * @FieldMatch(first = "password", second = "confirmPassword", message = "The password fields must match")
     * Example, compare more than 1 pair of fields:
     * @FieldMatch.List({
     * @FieldMatch(first = "password", second = "confirmPassword", message = "The password fields must match"),
     * @FieldMatch(first = "email", second = "confirmEmail", message = "The email fields must match")})
     */
    @Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @Constraint(validatedBy = GeneralValidator.FieldMatchValidator.class)
    @Documented
    public @interface FieldMatch {
        String message() default "Fields do not match";

        Class<?>[] groups() default {};

        Class<? extends Payload>[] payload() default {};

        /**
         * @return The first field
         */
        String first();

        /**
         * @return The second field
         */
        String second();

        /**
         * @return the Error Message
         */
        String errorMessage();

        /**
         * Defines several <code>@FieldMatch</code> annotations on the same element
         *
         * @see GeneralRuleAnnotation.FieldMatch
         */
        @Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
        @Retention(RetentionPolicy.RUNTIME)
        @Documented
        @interface List {
            GeneralRuleAnnotation.FieldMatch[] value();
        }
    }
}
