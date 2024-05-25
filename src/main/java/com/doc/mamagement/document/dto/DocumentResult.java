package com.doc.mamagement.document.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class DocumentResult {
    private String code;
    private String numberSign;
    private LocalDate issuedDate;
    private String title;
}
