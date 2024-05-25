package com.doc.mamagement.user.dto;

import com.doc.mamagement.utility.validation.ValidationConstant;
import com.doc.mamagement.utility.validation.general.GeneralRuleAnnotation;
import com.doc.mamagement.utility.validation.user.RuleAnnotation;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@GeneralRuleAnnotation.FieldMatch(
        first = "newPassword",
        second = "confirmNewPassword",
        errorMessage = "{validation.noMatching.confirmNewPassword}"
)
public class UserPasswordParam {
    @NotBlank(message = "{validation.required.oldPassword}")
    @RuleAnnotation.CheckOldPassword(message = "{validation.incorrect.oldPassword}")
    private String oldPassword;

    @NotBlank(message = "{validation.required.newPassword}")
    @Size(max = ValidationConstant.MAX_TEXT_INPUT_LENGTH, message = "{validation.size.newPassword}")
    private String newPassword;

    @NotBlank(message = "{validation.required.confirmNewPassword}")
    private String confirmNewPassword;
}
