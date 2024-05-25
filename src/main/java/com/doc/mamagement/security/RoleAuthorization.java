package com.doc.mamagement.security;

import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class RoleAuthorization {
    public static final String CODE_ADMIN = "ADMIN";
    public static final String CODE_MODERATOR = "MODERATOR";
    public static final String CODE_USER = "USER";
    public static final String PREFIX = "hasAnyAuthority('";
    public static final String SUFFIX = "')";
    public static final String MIDDLE = "','";

    public static final String ADMIN_MODERATOR_AUTHORIZATION = PREFIX + CODE_ADMIN + MIDDLE + CODE_MODERATOR + SUFFIX;
    public static final String ADMIN_AUTHORIZATION = PREFIX + CODE_ADMIN + SUFFIX;
    public static final String MODERATOR_AUTHORIZATION = PREFIX + CODE_MODERATOR + SUFFIX;
    public static final String USER_AUTHORIZATION = PREFIX + CODE_USER + SUFFIX;
    public static final String AUTHENTICATED = "isAuthenticated()";

    @Retention(RetentionPolicy.RUNTIME)
    @PreAuthorize(ADMIN_MODERATOR_AUTHORIZATION)
    public @interface AdminAndModeratorAuthorization {
    }

    @Retention(RetentionPolicy.RUNTIME)
    @PreAuthorize(ADMIN_AUTHORIZATION)
    public @interface AdminAuthorization {
    }

    @Retention(RetentionPolicy.RUNTIME)
    @PreAuthorize(MODERATOR_AUTHORIZATION)
    public @interface ModeratorAuthorization {
    }

    @Retention(RetentionPolicy.RUNTIME)
    @PreAuthorize(USER_AUTHORIZATION)
    public @interface UserAuthorization {
    }

    @Retention(RetentionPolicy.RUNTIME)
    @PreAuthorize(AUTHENTICATED)
    public @interface AuthenticatedUser {
    }
}
