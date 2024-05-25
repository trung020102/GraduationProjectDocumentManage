package com.doc.mamagement.utility.validation.general;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.commons.beanutils.BeanUtils;

public class GeneralValidator {
    public static class FieldMatchValidator implements ConstraintValidator<GeneralRuleAnnotation.FieldMatch, Object> {
        private String firstFieldName;
        private String secondFieldName;
        private String errorMessage;

        @Override
        public void initialize(final GeneralRuleAnnotation.FieldMatch constraintAnnotation) {
            firstFieldName = constraintAnnotation.first();
            secondFieldName = constraintAnnotation.second();
            errorMessage = constraintAnnotation.errorMessage();
        }

        @Override
        public boolean isValid(final Object value, final ConstraintValidatorContext context) {
            boolean isValid;
            try {
                final Object firstObj = BeanUtils.getProperty(value, firstFieldName);
                final Object secondObj = BeanUtils.getProperty(value, secondFieldName);

                isValid = firstObj == null && secondObj == null || firstObj != null && firstObj.equals(secondObj);
            } catch (final Exception ignore) {
                return false;
            }
            if (!isValid) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(errorMessage)
                        .addNode(secondFieldName)
                        .addConstraintViolation();
            }

            return isValid;
        }
    }
}
