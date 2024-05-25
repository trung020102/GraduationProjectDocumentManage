package com.doc.mamagement.utility.validation.category;

import com.doc.mamagement.category.CategoryRepository;
import com.doc.mamagement.entity.category.Category;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;

import java.util.HashSet;
import java.util.List;

public class Validator {
    @RequiredArgsConstructor
    public static class ValidCategoryIdValidator implements ConstraintValidator<RuleAnnotation.ValidCategoryId, Long> {
        private final CategoryRepository categoryRepository;

        @Override
        public boolean isValid(Long value, ConstraintValidatorContext context) {
            if (value == null)
                return true;

            return Boolean.TRUE.equals(categoryRepository.existsById(value).block());
        }
    }

    @RequiredArgsConstructor
    public static class ValidCategoryIdsValidator implements ConstraintValidator<RuleAnnotation.ValidCategoryIds, List<Long>> {
        private final CategoryRepository categoryRepository;

        @Override
        public boolean isValid(List<Long> inputCategoryIds, ConstraintValidatorContext context) {
            List<Category> categories = categoryRepository.findAll().collectList().block();
            List<Long> availableCategoryIds = categories.stream().map(Category::getId).toList();

            return new HashSet<>(availableCategoryIds).containsAll(inputCategoryIds);
        }
    }
}
