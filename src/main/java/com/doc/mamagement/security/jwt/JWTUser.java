package com.doc.mamagement.security.jwt;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class JWTUser {
    private String userId;
    private String username;
}
