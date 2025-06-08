package com.vishal.mockapi.entity;

import java.time.LocalDateTime;

import com.vishal.mockapi.service.ApiKeyGenerator;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "reset_password_tokens")
public class ResetPasswordToken {

    @Column(length = 1000)
    private String token;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;
    @Column(name = "expiry_date", nullable = false)
    private LocalDateTime expiryDate;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public ResetPasswordToken() {
        token = ApiKeyGenerator.generateApiKey(32);
        expiryDate = LocalDateTime.now().plusHours(1);
    }

    public String getToken() {
        return token;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public LocalDateTime getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDateTime expiryDate) {
        this.expiryDate = expiryDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
