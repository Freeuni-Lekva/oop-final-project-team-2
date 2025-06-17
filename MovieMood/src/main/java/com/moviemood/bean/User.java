package com.moviemood.bean;

/**
 * This class is keeping information about user of website.
 */

public class User {
    private int id;
    private String username;
    private String email;
    private String hashedPassword;
    private String remember_token; // "Remember me" token

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
}

