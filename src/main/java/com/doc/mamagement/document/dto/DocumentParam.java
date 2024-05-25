package com.doc.mamagement.document.dto;

import com.doc.mamagement.document.validation_group.DocumentCreation;
import com.doc.mamagement.document.validation_group.DocumentUpdating;
import com.doc.mamagement.utility.validation.document.RuleAnnotation;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DocumentParam {
    @NotBlank(message = "{validation.required.code}", groups = {DocumentCreation.class, DocumentUpdating.class})
    @RuleAnnotation.CheckDuplicateDocumentCode(
            message = "{validation.duplicate.documentCode}",
            groups = DocumentCreation.class
    )
    @RuleAnnotation.CheckExistedDocumentCode(
            message = "{validation.notExisted.documentCode}",
            groups = DocumentUpdating.class
    )
    private String code;

    @NotBlank(message = "{validation.invalid.numberSign}", groups = {DocumentCreation.class, DocumentUpdating.class})
    private String numberSign;

    @NotNull(message = "{validation.invalid.issuedDate}", groups = {DocumentCreation.class, DocumentUpdating.class})
    private LocalDate issuedDate;

    @NotBlank(message = "{validation.invalid.urgencyLevel}", groups = {DocumentCreation.class, DocumentUpdating.class})
    private String urgencyLevel;

    @NotBlank(message = "{validation.invalid.securityLevel}", groups = {DocumentCreation.class, DocumentUpdating.class})
    private String securityLevel;

    @NotNull(message = "{validation.invalid.mainDocumentFile}", groups = {DocumentCreation.class, DocumentUpdating.class})
    private DocumentFile mainDocumentFile;

    private List<DocumentFile> attachedDocumentFiles;

    //Số trang
    private int pageNumber;

    // ID của danh mục con trong các ô select Loại văn bản, Cơ quan ban hành, Lĩnh vực hoạt động,...
    @NotEmpty(message = "{validation.invalid.categoryIds}", groups = {DocumentCreation.class, DocumentUpdating.class})
    @com.doc.mamagement.utility.validation.category.RuleAnnotation.ValidCategoryIds(
            message = "{validation.invalid.categoryIds}",
            groups = {DocumentCreation.class, DocumentUpdating.class}
    )
    private List<Long> subCategoryIds;

    @NotBlank(message = "{validation.invalid.title}", groups = {DocumentCreation.class, DocumentUpdating.class})
    private String title;

    @RuleAnnotation.ValidDocumentCodes(message = "{validation.invalid.documentIds}", groups = {DocumentCreation.class, DocumentUpdating.class})
    private List<String> relatedDocumentCodes;

    @NotBlank(message = "{validation.invalid.signer}", groups = {DocumentCreation.class, DocumentUpdating.class})
    private String signer;

    private String recipient;

    @NotEmpty(message = "{validation.invalid.userIds}", groups = {DocumentCreation.class, DocumentUpdating.class})
    @com.doc.mamagement.utility.validation.user.RuleAnnotation.ValidUserIds(
            message = "{validation.invalid.userIds}",
            groups = {DocumentCreation.class, DocumentUpdating.class}
    )
    private List<Long> viewers;
}
