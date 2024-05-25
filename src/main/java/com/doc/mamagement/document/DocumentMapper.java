package com.doc.mamagement.document;

import com.doc.mamagement.category.CategoryMapper;
import com.doc.mamagement.category.dto.SubCategoryResult;
import com.doc.mamagement.document.dto.*;
import com.doc.mamagement.entity.category.Category;
import com.doc.mamagement.entity.document.Document;
import com.doc.mamagement.entity.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import vn.rananu.spring.mvc.mapper.BaseMapper;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DocumentMapper extends BaseMapper<DocumentResult, Document> {
    private final CategoryMapper categoryMapper;

    DocumentSelection toDocumentSelection(Document document) {
        return modelMapper.map(document, DocumentSelection.class);
    }

    DocumentDetailResult toDocumentResultDetail(Document document) {
        DocumentDetailResult documentDetailResult =  modelMapper.map(document, DocumentDetailResult.class);
        /* Chuyển đổi các văn bản liên quan kiểu Document sang kiểu RelatedDocumentResult và set lại cho dto DocumentDetailResult */
        List<RelatedDocumentResult> relatedDocumentResults = document.getRelatedDocuments()
                .stream()
                .map(this::toRelatedDocumentResult)
                .toList();
        documentDetailResult.setRelatedDocuments(relatedDocumentResults);

        List<SubCategoryResult> categoryResults = document.getSubCategories()
                .stream()
                .map(subCategory -> categoryMapper.toSubCategoryResult(
                        subCategory,
                        subCategory.getParentCategory().getId(),
                        subCategory.getParentCategory().getTitle())
                )
                .toList();
        documentDetailResult.setCategories(categoryResults);

        return documentDetailResult;
    }

    RelatedDocumentResult toRelatedDocumentResult(Document document) {
        return modelMapper.map(document, RelatedDocumentResult.class);
    }

    DocumentForEditResult toDocumentForEditResult(Document document) {
        DocumentForEditResult documentForEditResult = modelMapper.map(document, DocumentForEditResult.class);
        List<Long> subCategoryIds = document.getSubCategories().stream().map(Category::getId).toList();
        documentForEditResult.setSubCategoryIds(subCategoryIds);
        List<Long> viewerIds = document.getViewers().stream().map(User::getId).toList();
        documentForEditResult.setViewerIds(viewerIds);
        List<String> relatedDocumentCodes = document.getRelatedDocuments().stream().map(Document::getCode).toList();
        documentForEditResult.setRelatedDocumentCodes(relatedDocumentCodes);

        return documentForEditResult;
    }
}
