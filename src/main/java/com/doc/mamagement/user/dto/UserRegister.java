package com.doc.mamagement.user.dto;

import com.doc.mamagement.utility.validation.user.RuleAnnotation;
import com.doc.mamagement.utility.validation.ValidationConstant;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserRegister {
    @NotBlank(message="{validation.required.username}")
    @Size(max = ValidationConstant.MAX_TEXT_INPUT_LENGTH, message = "{validation.size.username}")
    @RuleAnnotation.UniqueUsername(message="{validation.duplicate.username}")
    private String username;

    @NotBlank(message="{validation.required.password}")
    @Size(max = ValidationConstant.MAX_TEXT_INPUT_LENGTH, message = "{validation.size.password}")
    private String password;

    @Size(max = ValidationConstant.MAX_TEXT_INPUT_LENGTH, message = "{validation.size.fullName}")
    private String fullName;

    @NotBlank(message="{validation.required.userRole}")
    @RuleAnnotation.ValidUserRole(message="{validation.invalid.roleCode}")
    private String roleCode;
}
