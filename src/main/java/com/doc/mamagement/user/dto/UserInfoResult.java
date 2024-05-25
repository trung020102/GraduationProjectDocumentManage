package com.doc.mamagement.user.dto;

import com.doc.mamagement.entity.user.Role;
import com.doc.mamagement.utility.TimeUtility;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class UserInfoResult {
    private String username;
    private String fullName;
    private String email;
    private Boolean isDisabled;
    private String avatar;
    private Role role;
    private String roleCode;
    private LocalDateTime createdAt;

    /* This setter is used to convert Role to String with role name value for displaying role */
    public String getRole() {
        return this.role.getName();
    }

    public String getRoleCode() {
        return this.role.getCode();
    }

    public String getCreatedAt() {
        return TimeUtility.toStringDateTime(this.createdAt, TimeUtility.DATE_AND_TIME_PATTERN);
    }
}
