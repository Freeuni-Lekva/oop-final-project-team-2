package com.moviemood.bean;

import java.sql.Timestamp;

/**
 * Bean class representing a user-created movie list
 */
public class UserList {
    private int id;
    private int userId;
    private String name;
    private String description;
    private boolean isPublic;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    
    // Transient field to track if current movie is in this list (for UI purposes)
    private transient boolean containsCurrentMovie;
    
    // Transient field to store movie count for display purposes
    private transient int movieCount;

    // Default constructor
    public UserList() {
    }

    // Full constructor
    public UserList(int id, int userId, String name, String description, boolean isPublic, 
                   Timestamp createdAt, Timestamp updatedAt) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.description = description;
        this.isPublic = isPublic;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Constructor without timestamps (for creating new lists)
    public UserList(int userId, String name, String description, boolean isPublic) {
        this.userId = userId;
        this.name = name;
        this.description = description;
        this.isPublic = isPublic;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean isPublic) {
        this.isPublic = isPublic;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    public boolean isContainsCurrentMovie() {
        return containsCurrentMovie;
    }

    public void setContainsCurrentMovie(boolean containsCurrentMovie) {
        this.containsCurrentMovie = containsCurrentMovie;
    }

    public int getMovieCount() {
        return movieCount;
    }

    public void setMovieCount(int movieCount) {
        this.movieCount = movieCount;
    }

    @Override
    public String toString() {
        return "UserList{" +
                "id=" + id +
                ", userId=" + userId +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", isPublic=" + isPublic +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
} 