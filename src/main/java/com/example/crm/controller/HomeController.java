package com.example.crm.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Collection;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getName())) {
            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
            if (hasRole(authorities, "ROLE_ADMIN") || hasRole(authorities, "ROLE_MARKETING")) {
                return "redirect:/me/blog";
            }
            if (hasRole(authorities, "ROLE_SALES")) {
                return "redirect:/customers";
            }
            if (hasRole(authorities, "ROLE_OPS")) {
                return "redirect:/mail/logs";
            }
        }
        return "redirect:/blog";
    }

    private boolean hasRole(Collection<? extends GrantedAuthority> authorities, String roleName) {
        return authorities.stream().anyMatch(a -> roleName.equals(a.getAuthority()));
    }
}
