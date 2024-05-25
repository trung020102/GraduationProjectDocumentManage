package com.doc.mamagement.web;

import com.doc.mamagement.security.RoleAuthorization;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    @RoleAuthorization.AdminAuthorization
    @GetMapping
    public ModelAndView toIndex() {
        return new ModelAndView("user/index");
    }

    @RoleAuthorization.AdminAuthorization
    @GetMapping("/create")
    public ModelAndView toUserCreatePage() {
        return new ModelAndView("user/create");
    }

    @RoleAuthorization.AuthenticatedUser
    @GetMapping("/profile")
    public ModelAndView toUserProfilePage() {
        return new ModelAndView("user/profile");
    }
}
