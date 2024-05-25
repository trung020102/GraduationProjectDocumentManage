package com.doc.mamagement.web;

import com.doc.mamagement.category.CategoryService;
import com.doc.mamagement.category.dto.CategoryResult;
import com.doc.mamagement.entity.user.Role;
import com.doc.mamagement.security.UserPrincipal;
import com.doc.mamagement.user.UserService;
import com.doc.mamagement.user.dto.UserInfoResult;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

/* This class adds specified models to all views by using @ControllerAdvice and @ModelAttribute */
@ControllerAdvice
@RequiredArgsConstructor
public class ModelToViewHandler {
    private final UserService userService;
    private final CategoryService categoryService;

    /* This method pass 'user' object to all returned views in this controller */
    @ModelAttribute("user")
    public UserInfoResult getCurrentUser(@AuthenticationPrincipal UserPrincipal principal) {
        return principal != null ? userService.findByUser(principal.getId()) : null;
    }

    @ModelAttribute("userRoles")
    public List<Role> getAllUserRoles() {
        return userService.getAllRoles();
    }

    @ModelAttribute("categories")
    public List<CategoryResult> getAllCategories() {
        return categoryService.getAllParentCategory();
    }

    /* This attribute for display elements by role ADMIN or MODERATOR */
    @ModelAttribute("isAdminOrModerator")
    public boolean isAdminOrModerator(@AuthenticationPrincipal UserPrincipal principal) {
        if (principal == null) return false;
        return principal.isAdminOrModerator();
    }

    @ModelAttribute("documentListPath")
    public String getDocumentListPath(@AuthenticationPrincipal UserPrincipal principal) {
        if (principal == null)
            return null;

        if (principal.isAdminOrModerator())
            return "/document/manage";

        return "/home";
    }
}
