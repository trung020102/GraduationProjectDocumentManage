package com.doc.mamagement.user.dto;

import com.doc.mamagement.utility.datatable.DatatablePagination;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class UserFilter extends DatatablePagination {
    private String keyword;
    private List<String> keywordBy;
    private LocalDate startDate;
    private LocalDate endDate;
    private List<String> roles;
    private List<Boolean> status;


    /*
    * This setter convert string value ("true", "false") to boolean value
    * (In request payload, status list only contains string values)
    * */
    public void setStatus(List<String> statusList) {
       this.status = statusList.stream().map(Boolean::parseBoolean).toList();
    }
}
