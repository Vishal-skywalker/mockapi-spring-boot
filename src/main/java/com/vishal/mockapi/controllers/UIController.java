package com.vishal.mockapi.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.vishal.mockapi.entity.ResetPasswordToken;
import com.vishal.mockapi.entity.Resource;
import com.vishal.mockapi.entity.UserEntity;
import com.vishal.mockapi.repository.ResetPasswordTokenRepo;
import com.vishal.mockapi.repository.ResourceRepo;
import com.vishal.mockapi.repository.UserRepo;
import com.vishal.mockapi.service.EmailService;
import com.vishal.mockapi.service.UserService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class UIController {

    @Autowired
    private UserService userService;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private ResourceRepo resourceRepo;
    @Autowired
    private EmailService emailService;
    @Autowired
    private ResetPasswordTokenRepo resetPasswordTokenRepo;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/admin")
    public String admin() {
        return "admin";
    }

    @GetMapping("/")
    public String home(Model model) {
        UserEntity user = userService.getCurrentUser();
        if (user != null) {
            model.addAttribute("username", user.getEmail());
            model.addAttribute("name", user.getName());
            List<Resource> resources = resourceRepo.findByOwnerId(user.getId());
            model.addAttribute("resources", resources);
        }
        return "home";
    }

    @GetMapping("/edit-profile")
    public String editProfile(Model model) {
        UserEntity user = userService.getCurrentUser();
        if (user != null) {
            setUserModelAttributes(model, user);
        }
        return "edit-profile";
    }

    private void setUserModelAttributes(Model model, UserEntity user) {
        model.addAttribute("username", user.getEmail());
        model.addAttribute("name", user.getName());
    }

    @PostMapping("/edit-profile")
    public String postEditProfile(Model model, UserEntity userToUpdate) {
        UserEntity user = userService.getCurrentUser();
        if (user != null && userToUpdate != null) {
            if (!user.getEmail().equals(userToUpdate.getEmail())) {
                UserService.UserValidationResult validationResult = userService.validateUser(userToUpdate);
                if (validationResult.isError) {
                    model.addAttribute("error", validationResult.errorMessage);
                    setUserModelAttributes(model, user);
                    return "edit-profile";
                }
            }
            userRepo.updateUserById(user.getId(), userToUpdate.getEmail(), userToUpdate.getName());
            return "redirect:/";
        }
        return editProfile(model);
    }

    @PostMapping("/change-password")
    public String postChangePassword(Model model, String oldPassword, String newPassword, String confirmPassword) {
        UserEntity user = userService.getCurrentUser();
        if (user != null && oldPassword != null && newPassword != null && confirmPassword != null) {
            if (oldPassword.equals(newPassword)) {
                model.addAttribute("error", "Same password provided!");
                return "change-password";
            }
            if (!newPassword.equals(confirmPassword)) {
                model.addAttribute("error", "New password and confirm password do not match!");
                return "change-password";
            }
            if (!userService.passwordEncoder.matches(oldPassword, user.getPassword())) {
                model.addAttribute("error", "Old password is incorrect!");
                return "change-password";
            }
            userRepo.updateUserPassword(user.getId(), userService.passwordEncoder.encode(confirmPassword));
            return "redirect:/";
        }
        return "change-password";
    }

    @GetMapping("/change-password")
    public String changePassword() {
        return "change-password";
    }

    @GetMapping("/register")
    public String register(HttpSession session) {
        return "register";
    }

    @PostMapping("/register")
    public String postRegister(UserEntity user, Model model, HttpServletRequest request) {
        Boolean isError = !userService.registerUser(user);
        if (isError) {
            model.addAttribute("error", "Invalid Request!");
            return "register";
        }
        try {
            request.login(user.getEmail(), user.getRawPassword());
        } catch (ServletException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return "redirect:";
    }

    @PostMapping("/validateUser")
    @ResponseBody
    public UserService.UserValidationResult postValidateUser(@RequestBody UserEntity user) {
        return userService.validateUser(user);
    }

    @GetMapping("/forgot-password")
    public String forgotPassword() {
        return "forgot-password";
    }

    @PostMapping("/forgot-password")
    public String postForgotPassword(String email, Model model, HttpServletRequest request) {
        List<UserEntity> users = userRepo.findByEmail(email);
        if (users.isEmpty()) {
            model.addAttribute("error", "User not found!");
            return "forgot-password";
        }
        String token = userService.generatePasswordResetToken(users.get(0));
        String appUrl = getAppUrl(request);
        String resetLink = appUrl + "/reset-password?token=" + token;
        emailService.sendSimpleMessage(email, "Password Reset Request",
                "To reset your password, please click the link below:\n" + resetLink);
        model.addAttribute("message", "Password reset link sent to your email.");
        return "forgot-password";
    }

    @GetMapping("/reset-password")
    public String resetPassword(String token, Model model) {
        if (token == null || token.isEmpty()) {
            model.addAttribute("error", "Invalid password reset token.");
            return "reset-password";
        }
        ResetPasswordToken resetToken = resetPasswordTokenRepo.findByToken(token)
                .orElse(null);
        if (resetToken == null || resetToken.getExpiryDate().isBefore(java.time.LocalDateTime.now())) {
            model.addAttribute("error", "Invalid or expired password reset token.");
            return "reset-password";
        }
        model.addAttribute("email", maskEmail(resetToken.getUser().getEmail()));
        model.addAttribute("token", token);
        return "reset-password";
    }

    @PostMapping("/reset-password")
    public String postResetPassword(String token, String newPassword, String confirmPassword, Model model) {
        if (token == null || token.isEmpty()) {
            model.addAttribute("error", "Invalid password reset token.");
            return "reset-password";
        }
        ResetPasswordToken resetToken = resetPasswordTokenRepo.findByToken(token)
                .orElse(null);
        if (resetToken == null || resetToken.getExpiryDate().isBefore(java.time.LocalDateTime.now())) {
            model.addAttribute("error", "Invalid or expired password reset token.");
            return "reset-password";
        }
        if (newPassword == null || confirmPassword == null || !newPassword.equals(confirmPassword)) {
            model.addAttribute("error", "Passwords do not match!");
            return "reset-password";
        }
        UserEntity user = resetToken.getUser();
        userRepo.updateUserPassword(user.getId(), userService.passwordEncoder.encode(newPassword));
        resetPasswordTokenRepo.delete(resetToken);
        model.addAttribute("message", "Password has been successfully reset.");
        return "login";
    }

    private String maskEmail(String email) {
        if (email == null || email.isEmpty()) {
            return "";
        }
        int atIndex = email.indexOf('@');
        if (atIndex <= 1) {
            return email; // No masking needed
        }
        String firstChar = email.substring(0, 1);
        String lastChar = email.substring(atIndex - 1, atIndex);
        String maskedPart = "*".repeat(Math.max(0, atIndex - 2));
        return firstChar + maskedPart + lastChar + email.substring(atIndex);
    }

    public String getAppUrl(HttpServletRequest request) {
        int port = request.getServerPort();
        if ((request.getScheme().equals("http") && port == 80) ||
                (request.getScheme().equals("https") && port == 443)) {
            return String.format("%s://%s", request.getScheme(), request.getServerName());
        } else {
            return String.format("%s://%s:%d", request.getScheme(), request.getServerName(), port);
        }
    }
}
