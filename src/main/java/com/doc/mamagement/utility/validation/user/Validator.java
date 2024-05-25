package com.doc.mamagement.utility.validation.user;

import com.doc.mamagement.entity.user.RoleConstant;
import com.doc.mamagement.entity.user.User;
import com.doc.mamagement.security.UserPrincipal;
import com.doc.mamagement.user.UserRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashSet;
import java.util.List;

public class Validator {
    public static class ValidUserRoleValidator implements ConstraintValidator<RuleAnnotation.ValidUserRole, String> {
        @Override
        public boolean isValid(String value, ConstraintValidatorContext context) {
            /* Return true when the value is empty in order to avoid merging the same attribute error */
            if (value.isEmpty()) return true;

            return RoleConstant.getAllNames().contains(value);
        }
    }
    
    @RequiredArgsConstructor
    public static class UniqueUsernameValidator implements ConstraintValidator<RuleAnnotation.UniqueUsername, String> {
        private final UserRepository userRepository;

        @Override
        public boolean isValid(String value, ConstraintValidatorContext context) {
            return Boolean.FALSE.equals(userRepository.existsByUsername(value).block());
        }
    }

    @RequiredArgsConstructor
    public static class ExistedUsernameValidator implements ConstraintValidator<RuleAnnotation.ExistedUsername, String> {
        private final UserRepository userRepository;

        @Override
        public boolean isValid(String value, ConstraintValidatorContext context) {
            return Boolean.TRUE.equals(userRepository.existsByUsername(value).block());
        }
    }

    @RequiredArgsConstructor
    public static class ValidUserIdValidator implements ConstraintValidator<RuleAnnotation.ValidUserId, Long> {
        private final UserRepository userRepository;

        @Override
        public boolean isValid(Long value, ConstraintValidatorContext context) {
            return Boolean.TRUE.equals(userRepository.existsById(value).block());
        }
    }

    @RequiredArgsConstructor
    public static class CheckOldPasswordValidator implements ConstraintValidator<RuleAnnotation.CheckOldPassword, String> {
        private final PasswordEncoder passwordEncoder;

        @Override
        public boolean isValid(String value, ConstraintValidatorContext context) {
            /* Return true when the value is empty in order to avoid merging the same attribute error */
            if (value.isEmpty()) return true;

            UserPrincipal principal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            passwordEncoder.matches(value, principal.getPassword());
            return passwordEncoder.matches(value, principal.getPassword());
        }
    }

    @RequiredArgsConstructor
    public static class CheckEmailExistValidator implements ConstraintValidator<RuleAnnotation.CheckEmailExist, String> {
        private final UserRepository userRepository;

        @Override
        public boolean isValid(String value, ConstraintValidatorContext context) {
            UserPrincipal principal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            return Boolean.FALSE.equals(userRepository.existsByEmailAndIdNot(value, principal.getId()).block());
        }
    }

    public static class CheckAvatarValidator implements ConstraintValidator<RuleAnnotation.CheckAvatar, MultipartFile> {
        @Value("${avatar.multipart.max-file-size}")
        private Long maxFileSize;

        @Override
        public boolean isValid(MultipartFile avatar, ConstraintValidatorContext context) {
            if (avatar.isEmpty())
                return false;

            context.disableDefaultConstraintViolation();

            String contentType = avatar.getContentType();
            if (!isSupportedContentType(contentType)) {
                context.buildConstraintViolationWithTemplate("{message.fail.user.avatarType}")
                        .addConstraintViolation();
                return false;
            }

            long avatarSize = avatar.getSize();
            if (avatarSize > maxFileSize) {
                context.buildConstraintViolationWithTemplate("{message.fail.user.avatarSize}")
                        .addConstraintViolation();
                return false;
            }

            return true;
        }

        private boolean isSupportedContentType(String contentType) {
            return contentType.equals("image/png")
                    || contentType.equals("image/jpg")
                    || contentType.equals("image/jpeg");
        }
    }
    
    @RequiredArgsConstructor
    public static class CheckValidUserIdsValidator implements ConstraintValidator<RuleAnnotation.ValidUserIds, List<Long>> {
        private final UserRepository userRepository;

        @Override
        public boolean isValid(List<Long> inputIds, ConstraintValidatorContext context) {
            List<User> availableUsers = userRepository.findAll().collectList().block();
            List<Long> availableUserIds = availableUsers.stream().map(User::getId).toList();

            return new HashSet<>(availableUserIds).containsAll(inputIds);
        }
    }
}
