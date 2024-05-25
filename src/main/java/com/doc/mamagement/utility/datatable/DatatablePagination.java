package com.doc.mamagement.utility.datatable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DatatablePagination {
    private Integer draw;              // current page
    private Integer start;             // start from
    private Integer length;            // number of records each page
}
