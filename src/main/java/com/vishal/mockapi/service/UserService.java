package com.vishal.mockapi.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.vishal.mockapi.entity.ResetPasswordToken;
import com.vishal.mockapi.entity.UserEntity;
import com.vishal.mockapi.repository.ResetPasswordTokenRepo;
import com.vishal.mockapi.repository.UserRepo;

@Service
public class UserService {
    @Autowired
    private UserRepo userRepo;

    @Autowired
    public PasswordEncoder passwordEncoder;

    @Autowired
    private ResetPasswordTokenRepo resetPasswordTokenRepo;

    @Value("${admin.username}")
    private String adminUsername;

    public boolean registerUser(UserEntity userEntity) {
        try {
            if (!validateUser(userEntity).isError) {
                userEntity.setRawPassword(userEntity.getPassword());
                userEntity.setPassword(passwordEncoder.encode(userEntity.getPassword()));
                if (userEntity.getEmail() != null && userEntity.getEmail().equals(adminUsername)) {
                    userEntity.setRole("ADMIN");
                }
                userRepo.save(userEntity);
                return true;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    public UserValidationResult validateUser(UserEntity user) {
        if (!userRepo.findByEmail(user.getEmail()).isEmpty()) {
            return new UserValidationResult("Email already exist");
        }
        return new UserValidationResult();
    }

    public UserEntity getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            String email = auth.getName();
            return userRepo.findByEmail(email).stream().findFirst().orElse(null);
        }
        return null;
    }

    public static class UserValidationResult {
        public String errorMessage;
        public Boolean isError;

        public UserValidationResult(String errorMessage) {
            this.errorMessage = errorMessage;
            this.isError = true;
        }

        public UserValidationResult() {
            this.isError = false;
        }

        public String getErrorMessage() {
            return this.errorMessage;
        }
    }

    public Iterable<UserEntity> getAll() {
        return userRepo.findAll();
    }

    public String generatePasswordResetToken(UserEntity user) {
        ResetPasswordToken resetPasswordToken = new ResetPasswordToken();
        resetPasswordToken.setUser(user);
        resetPasswordTokenRepo.save(resetPasswordToken);
        return resetPasswordToken.getToken();
    }

    public void makeDummyUsers() {
        List<UserEntity> usersToInsert = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            UserEntity user = new UserEntity();
            user.setName("User " + i);
            user.setEmail("user" + i + "@example.com");
            user.setPassword(passwordEncoder.encode("1234"));
            usersToInsert.add(user);
        }
        userRepo.saveAll(usersToInsert);
    }
}
