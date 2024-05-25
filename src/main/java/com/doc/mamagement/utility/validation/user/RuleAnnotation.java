package com.doc.mamagement.utility.validation.user;


import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.ReportAsSingleViolation;

import java.lang.annotation.*;

public class RuleAnnotation {
    @Documented
    @Target({ElementType.METHOD, ElementType.FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    @Constraint(validatedBy = Validator.ValidUserRoleValidator.class)
    @ReportAsSingleViolation
    public @interface ValidUserRole {
        String message() default "Invalid role code value";

        Class<?>[] groups() default {};

        Class<? extends Payload>[] payload() default {};
    }

    @Documented
    @Target({ElementType.FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    @Constraint(validatedBy = Validator.UniqueUsernameValidator.class)
    @ReportAsSingleViolation
    public @interface UniqueUsername {
        String message() default "Username existed";

        Class<?>[] groups() default {};

        Class<? extends Payload>[] payload() default {};
    }

    @Documented
    @Target({ElementType.FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    @Constraint(validatedBy = Validator.ExistedUsernameValidator.class)
    @ReportAsSingleViolation
    public @interface ExistedUsername {
        String message() default "Username doesn't exist";

        Class<?>[] groups() default {};

        Class<? extends Payload>[] payload() default {};
    }

    @Target({ElementType.FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    @Constraint(validatedBy = Validator.ValidUserIdValidator.class)
    @ReportAsSingleViolation
    @Documented
    public @interface ValidUserId {
        String message() default "Invalid user ID value";

        Class<?>[] groups() default {};

        Class<? extends Payload>[] payload() default {};
    }

    @Documented
    @Target({ElementType.METHOD, ElementType.FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    @Constraint(validatedBy = Validator.CheckOldPasswordValidator.class)
    @ReportAsSingleViolation
    public @interface CheckOldPassword {
        String message() default "Incorrect old password value";

        Class<?>[] groups() default {};

        Class<? extends Payload>[] payload() default {};
    }

    @Documented
    @Target({ElementType.METHOD, ElementType.FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    @Constraint(validatedBy = Validator.CheckEmailExistValidator.class)
    @ReportAsSingleViolation
    public @interface CheckEmailExist {
        String message() default "Email has existed";

        Class<?>[] groups() default {};

        Class<? extends Payload>[] payload() default {};
    }

    @Documented
    @Target({ElementType.PARAMETER, ElementType.METHOD, ElementType.FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    @Constraint(validatedBy = Validator.CheckAvatarValidator.class)
    @ReportAsSingleViolation
    public @interface CheckAvatar {
        String message() default "Invalid Avatar";

        Class<?>[] groups() default {};

        Class<? extends Payload>[] payload() default {};
    }

    @Target({ElementType.FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    @Constraint(validatedBy = Validator.CheckValidUserIdsValidator.class)
    @ReportAsSingleViolation
    @Documented
    public @interface ValidUserIds {
        String message() default "Invalid user IDs value";

        Class<?>[] groups() default {};

        Class<? extends Payload>[] payload() default {};
    }
}
