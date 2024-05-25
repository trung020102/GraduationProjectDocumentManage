package com.doc.mamagement.category.dto;

import com.doc.mamagement.utility.validation.ValidationConstant;
import com.doc.mamagement.utility.validation.category.RuleAnnotation;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubCategoryParam {
    @RuleAnnotation.ValidCategoryId(message = "{validation.noExisted.categoryId}")
    private Long id;

    @NotEmpty(message = "{validation.required.categoryName}")
    @Size(max = ValidationConstant.MAX_TEXT_INPUT_LENGTH, message = "{validation.size.categoryName}")
    private String title;

    @NotNull(message = "{validation.required.categoryId}")
    @RuleAnnotation.ValidCategoryId(message = "{validation.noExisted.categoryId}")
    private Long parentId;

    private Boolean isParent = false;
}
