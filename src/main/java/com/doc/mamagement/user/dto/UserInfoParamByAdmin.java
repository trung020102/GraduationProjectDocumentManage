package com.doc.mamagement.user.dto;

import com.doc.mamagement.utility.validation.ValidationConstant;
import com.doc.mamagement.utility.validation.user.RuleAnnotation;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserInfoParamByAdmin {
    @NotBlank(message="{validation.required.userId}")
    @RuleAnnotation.ExistedUsername(message = "{validation.noExisted.username}")
    private String username;

    @Size(max = ValidationConstant.MAX_TEXT_INPUT_LENGTH, message = "{validation.size.password}")
    private String newPassword;

    @NotBlank(message="{validation.required.userRole}")
    @RuleAnnotation.ValidUserRole(message="{validation.invalid.roleCode}")
    private String role;

    @NotNull(message = "{validation.required.status}")
    private Boolean isDisabled;

    /*
     * This setter convert string value ("true", "false") to boolean value
     * (In request payload, status is string value)
     * */
    public void setStatus(String status) {
        this.isDisabled = Boolean.parseBoolean(status);
    }
}
