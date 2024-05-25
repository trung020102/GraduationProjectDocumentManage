package com.doc.mamagement.user.dto;

import com.doc.mamagement.utility.validation.ValidationConstant;
import com.doc.mamagement.utility.validation.user.RuleAnnotation;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserInfoParam {
    @Size(max = ValidationConstant.MAX_TEXT_INPUT_LENGTH, message = "{validation.size.fullName}")
    private String fullName;

    @Pattern(
            regexp = "|^[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?$",
            message ="{validation.pattern.email}"
    )
    @RuleAnnotation.CheckEmailExist(message = "{validation.duplicate.email}")
    private String email;
}
