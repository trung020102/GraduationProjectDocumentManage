package com.doc.mamagement.document.dto;

import com.doc.mamagement.utility.datatable.DatatablePagination;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
@Getter
@Setter
public class DocumentFilter extends DatatablePagination  {
    private String keyword;
    private LocalDate startDate;
    private LocalDate endDate;
}
