package com.doc.mamagement.category.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubCategoryResult {
    private Long id;
    private String title;
    private String parentTitle;
    private Long parentId;
    /* This attribute has total quantity of document inside */
    private Integer documentCount;
}
