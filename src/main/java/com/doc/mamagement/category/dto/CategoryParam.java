package com.doc.mamagement.category.dto;

import com.doc.mamagement.utility.validation.ValidationConstant;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryParam {
    private Long id;

    @NotEmpty(message = "{validation.required.categoryName}")
    @Size(max = ValidationConstant.MAX_TEXT_INPUT_LENGTH, message = "{validation.size.categoryName}")
    private String title;

    private final Boolean isParent = true;
}
