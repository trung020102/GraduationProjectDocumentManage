package com.doc.mamagement.user;

import com.doc.mamagement.document.dto.DocumentSelection;
import com.doc.mamagement.entity.document.Document;
import com.doc.mamagement.entity.user.Role;
import com.doc.mamagement.entity.user.RoleConstant;
import com.doc.mamagement.entity.user.User;
import com.doc.mamagement.user.dto.UserInfoParam;
import com.doc.mamagement.user.dto.UserInfoResult;
import com.doc.mamagement.user.dto.UserRegister;
import com.doc.mamagement.user.dto.UserSelection;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import vn.rananu.spring.mvc.mapper.BaseMapper;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class UserMapper extends BaseMapper<UserInfoResult, User> {
    private final PasswordEncoder passwordEncoder;

    public User toEntity(UserRegister userRegister) {
        RoleConstant settingRole = RoleConstant.parse(userRegister.getRoleCode());
        User newUser = new User();
        newUser.setUsername(userRegister.getUsername());
        newUser.setPasswordHash(passwordEncoder.encode(userRegister.getPassword()));
        newUser.setFullName(userRegister.getFullName());
        newUser.setIsDisabled(false);
        newUser.setRole(new Role(settingRole.name(), settingRole.getValue()));
        newUser.setCreatedAt(LocalDateTime.now());

        return newUser;
    }

    UserSelection toUserSelection(User user) {
        return modelMapper.map(user, UserSelection.class);
    }
}
