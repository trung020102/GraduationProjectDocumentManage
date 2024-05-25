package com.doc.mamagement.category;

import com.doc.mamagement.category.dto.CategoryResult;
import com.doc.mamagement.category.dto.SubCategoryResult;
import com.doc.mamagement.entity.category.Category;
import com.doc.mamagement.entity.document.Document;
import com.doc.mamagement.entity.user.User;
import com.doc.mamagement.security.UserPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import vn.rananu.spring.mvc.mapper.BaseMapper;

import java.util.List;

@Component
public class CategoryMapper extends BaseMapper<CategoryResult, Category> {
    public SubCategoryResult toSubCategoryResult(Category subCategory, Long parentId, String parentTitle) {
        SubCategoryResult subCategoryResult = modelMapper.map(subCategory, SubCategoryResult.class);
        subCategoryResult.setParentId(parentId);
        subCategoryResult.setParentTitle(parentTitle);
        Integer documentCount = subCategory.getDocuments() != null ? adjustDocumentCount(subCategory.getDocuments()) : 0;
        subCategoryResult.setDocumentCount(documentCount);

        return subCategoryResult;
    }

    private int adjustDocumentCount(List<Document> documents) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();
        /* If current user has USER role, only count the document that the user can view */
        if (principal instanceof UserPrincipal userPrincipal) {
            if (userPrincipal.isNormalUser()) {
                return (int) documents
                        .stream()
                        .filter(document -> document.getViewers().contains(new User(userPrincipal.getUsername())))
                        .count();
            }
        }

        return documents.size();
    }

    public CategoryResult toDTO(Category category) {
        return modelMapper.map(category, CategoryResult.class);
    }
}
