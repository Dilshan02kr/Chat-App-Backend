package com.dilshan.chat_app.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.JoinColumnOrFormula;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String phoneNumber;

    @Column
    private String otp;

    @Column
    private LocalDateTime otpCreatedAt;

    @Column(nullable = false)
    private boolean isVerified;

    public User() {
    }

    public User(String name, String phoneNumber, String otp, boolean isVerified) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.otp = otp;
        this.isVerified = isVerified;
        this.otpCreatedAt = LocalDateTime.now();
    }

    public User(String name, String phoneNumber) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.otp = null;
        this.isVerified = false;
        this.otpCreatedAt = null;
    }

    public User(Long id, String name, String phoneNumber, String otp, boolean isVerified) {
        this.id = id;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.otp = otp;
        this.isVerified = isVerified;
        this.otpCreatedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
        this.otpCreatedAt = LocalDateTime.now();
    }

    public LocalDateTime getOtpCreatedAt() {
        return otpCreatedAt;
    }

    public void setOtpCreatedAt(LocalDateTime otpCreatedAt) {
        this.otpCreatedAt = otpCreatedAt;
    }

    public boolean isVerified() {
        return isVerified;
    }

    public void setVerified(boolean verified) {
        isVerified = verified;
    }
}
