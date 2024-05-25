package com.doc.mamagement.security.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
public class LoginParam {
    @NotBlank(message = "{validation.required.username}")
    private String username;
    @NotBlank(message="{validation.required.password}")
    private String password;
}


