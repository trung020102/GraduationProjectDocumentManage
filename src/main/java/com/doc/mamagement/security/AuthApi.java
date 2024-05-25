package com.doc.mamagement.security;

import com.doc.mamagement.security.dto.LoginParam;
import com.doc.mamagement.security.jwt.JwtService;
import com.doc.mamagement.utility.Helper;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class AuthApi {
    @NotNull
    @Qualifier(SecurityBeanName.USER_DAO_AUTHENTICATION_PROVIDER)
    private final DaoAuthenticationProvider userAuthenticationProvider;
    private final JwtService jwtService;
    private final Helper helper;

    @PostMapping("/api/login")
    public ResponseEntity<?> uniLogin(@Valid @RequestBody LoginParam loginParam) {
        Authentication authentication = userAuthenticationProvider.authenticate(
                new UsernamePasswordAuthenticationToken(loginParam.getUsername(),
                loginParam.getPassword())
        );
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        String accessToken = jwtService.generateToken(principal.getId().toString(), principal.getUsername());
        ResponseCookie cookie = ResponseCookie.from("JWT", accessToken)
                .httpOnly(false).secure(false).path("/")
                .maxAge(SecurityConfiguration.tokenExpirationTime)
                .build();

        Map<String, String> extraData = new HashMap<>();
        extraData.put("redirectUrl", "/");
        extraData.put("message", helper.getValueFromI18n("message.success.login"));

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).body(extraData);
    }
}
