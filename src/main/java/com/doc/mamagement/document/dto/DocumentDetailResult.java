package com.doc.mamagement.document.dto;

import com.doc.mamagement.category.dto.SubCategoryResult;
import  lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class DocumentDetailResult {
    private String code;
    private String numberSign;
    private LocalDateTime createdAt;
    private LocalDate issuedDate;
    private String title;
    private String urgencyLevel;
    private String securityLevel;
    //loai, co quan, linh vuc
    private List<SubCategoryResult> categories;
    private String signer;
    private String recipient;
    private DocumentFile mainDocumentFile;
    private List<DocumentFile> attachedDocumentFiles;
    private List<RelatedDocumentResult> relatedDocuments;
    private int pageNumber;
}
