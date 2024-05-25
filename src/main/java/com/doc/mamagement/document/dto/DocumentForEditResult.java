package com.doc.mamagement.document.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class DocumentForEditResult {
    private String code;
    private String numberSign;
    private LocalDate issuedDate;
    private String title;
    private String urgencyLevel;
    private String securityLevel;
    private String signer;
    private String recipient;
    private DocumentFile mainDocumentFile;
    private List<DocumentFile> attachedDocumentFiles;
    private int pageNumber;
    private List<String> relatedDocumentCodes;
    private List<Long> subCategoryIds;
    private List<Long> viewerIds;
}
