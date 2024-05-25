package com.doc.mamagement.utility.validation.document;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.ReportAsSingleViolation;

import java.lang.annotation.*;

public class RuleAnnotation {
    @Target({ElementType.FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    @Constraint(validatedBy = Validator.ValidDocumentCodesValidator.class)
    @ReportAsSingleViolation
    @Documented
    public @interface ValidDocumentCodes {
        String message() default "Invalid document IDs value";

        Class<?>[] groups() default {};

        Class<? extends Payload>[] payload() default {};
    }

    @Target({ElementType.FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    @Constraint(validatedBy = Validator.CheckDuplicateDocumentCodeValidator.class)
    @ReportAsSingleViolation
    @Documented
    public @interface CheckDuplicateDocumentCode {
        String message() default "Duplicate document code value";

        Class<?>[] groups() default {};

        Class<? extends Payload>[] payload() default {};
    }

    @Target({ElementType.FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    @Constraint(validatedBy = Validator.CheckExistedDocumentCodeValidator.class)
    @ReportAsSingleViolation
    @Documented
    public @interface CheckExistedDocumentCode {
        String message() default "Document code doesn't exist";

        Class<?>[] groups() default {};

        Class<? extends Payload>[] payload() default {};
    }


}
