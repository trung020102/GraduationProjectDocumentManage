package com.doc.mamagement.web;

import com.doc.mamagement.security.UserPrincipal;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping
public class DashboardController {
    @GetMapping("/login")
    public String toLoginPage(@AuthenticationPrincipal UserPrincipal principal) {
        return principal != null ? "redirect:/home" : "auth/log-in";
    }

    @GetMapping({"/", ""})
    public String redirectToHomeOrLoginPage(@AuthenticationPrincipal UserPrincipal principal) {
        /* Redirect to log-in page  if user is not authenticated */
        if (principal == null) {
            return "redirect:/login";
        }

        /* Redirect to document management page if user role is ADMIN or MODERATOR */
        if (principal.isAdminOrModerator()) {
            return "redirect:/document/manage";
        }

        /* Redirect to homepage if user role is not ADMIN or MODERATOR */
        return "redirect:/home";
    }

    @GetMapping("/home")
    public ModelAndView toHomePage() {
        return new ModelAndView("document/manage");
    }
}
