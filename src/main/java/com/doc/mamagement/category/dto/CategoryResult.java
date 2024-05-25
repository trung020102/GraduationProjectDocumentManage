package com.doc.mamagement.category.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CategoryResult {
    private Long id;
    private String title;
    private List<SubCategoryResult> subCategories;
    /* This attribute has total quantity of document in all sub-categories */
    private Integer documentCount;
}
