package com.shop.controller;

import com.shop.dto.UserDto;
import com.shop.entity.User;
import com.shop.enums.Role;
import com.shop.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    // Redirect root to login
    @GetMapping("/")
    public String index(HttpSession session) {
        if (session.getAttribute("userId") != null) {
            String role = (String) session.getAttribute("role");
            return "SELLER".equals(role) ? "redirect:/seller/dashboard" : "redirect:/customer/dashboard";
        }
        return "redirect:/login";
    }

    // Show login page
    @GetMapping("/login")
    public String loginPage(HttpSession session) {
        if (session.getAttribute("userId") != null) {
            String role = (String) session.getAttribute("role");
            return "SELLER".equals(role) ? "redirect:/seller/dashboard" : "redirect:/customer/dashboard";
        }
        return "auth/login";
    }

    // Handle login form submission
    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        HttpSession session,
                        Model model) {
        try {
            User user = userService.login(username, password);
            // Store in session
            session.setAttribute("userId", user.getId());
            session.setAttribute("username", user.getUsername());
            session.setAttribute("role", user.getRole().name()); // Stored as "SELLER" or "CUSTOMER"

            if (user.getRole() == Role.SELLER) {
                return "redirect:/seller/dashboard";
            } else {
                return "redirect:/customer/dashboard";
            }
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "auth/login";
        }
    }

    // Show register page
    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("userDto", new UserDto());
        model.addAttribute("roles", Role.values());
        return "auth/register";
    }

    // Handle register form submission
    @PostMapping("/register")
    public String register(@ModelAttribute UserDto userDto, Model model) {
        try {
            userService.register(userDto);
            return "redirect:/login?registered=true";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("userDto", userDto);
            model.addAttribute("roles", Role.values());
            return "auth/register";
        }
    }

    // Logout
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login?logout=true";
    }
}
