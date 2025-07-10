package com.moviemood.bean;

import java.sql.Timestamp;

/**
 * This class is keeping information about user of website.
 */

public class User {
    private int id;
    private String username;
    private String email;
    private String hashedPassword;
    private String remember_token; // "Remember me" token
    private boolean isVerified;
    private String verificationCode;
    private Timestamp verificationCodeExpiry;
    private String profilePicture;



    public User(int id, String username, String email, String hashedPassword, String token) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.hashedPassword = hashedPassword;
        this.remember_token = token;
    }

    public int getId() { return id; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getHashedPassword() { return hashedPassword; }
    public String getToken() { return remember_token; }
    
    // Setters for updating user information
    public void setUsername(String username) { this.username = username; }
    public void setHashedPassword(String hashedPassword) { this.hashedPassword = hashedPassword; }
    public String getProfilePicture() { return profilePicture; }
    public void setProfilePicture(String profilePicture) { this.profilePicture = profilePicture; }

    // Getters and Setters for verification fields.
    public boolean isVerified() { return isVerified; }
    public void setVerified(boolean verified) { this.isVerified = verified; }
    public String getVerificationCode() { return verificationCode; }
    public void setVerificationCode(String verificationCode) { this.verificationCode = verificationCode; }
    public Timestamp getVerificationCodeExpiry() { return verificationCodeExpiry; }
    public void setVerificationCodeExpiry(Timestamp verificationCodeExpiry) { this.verificationCodeExpiry = verificationCodeExpiry; }
}

