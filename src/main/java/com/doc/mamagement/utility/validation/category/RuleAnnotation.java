package com.doc.mamagement.utility.validation.category;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.ReportAsSingleViolation;

import java.lang.annotation.*;

public class RuleAnnotation {
    @Target({ElementType.FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    @Constraint(validatedBy = Validator.ValidCategoryIdValidator.class)
    @ReportAsSingleViolation
    @Documented
    public @interface ValidCategoryId {
        String message() default "Invalid category ID value";

        Class<?>[] groups() default {};

        Class<? extends Payload>[] payload() default {};
    }

    @Target({ElementType.FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    @Constraint(validatedBy = Validator.ValidCategoryIdsValidator.class)
    @ReportAsSingleViolation
    @Documented
    public @interface ValidCategoryIds {
        String message() default "Invalid category IDs value";

        Class<?>[] groups() default {};

        Class<? extends Payload>[] payload() default {};
    }
}
