package com.doc.mamagement.entity.user;


import java.util.Arrays;
import java.util.List;

public enum RoleConstant {
    ADMIN("Administrator"),
    MODERATOR("Moderator"),
    USER("User");

    private final String value;

    RoleConstant(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static RoleConstant parse(String value) {
        RoleConstant[] values = values();
        for (RoleConstant role : values) {
            if (role.name().equals(value)) {
                return role;
            }
        }
        throw new IllegalArgumentException("Cannot parse " + value);
    }

    public static List<String> getAllNames() {
        return Arrays.stream(RoleConstant.values()).map(RoleConstant::name).toList();
    }
}
