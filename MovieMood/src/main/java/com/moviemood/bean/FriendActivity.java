package com.moviemood.bean;

import java.time.LocalDateTime;

/**
 * Represents a friend's activity for the activity feed
 */
public class FriendActivity {
    private int id;
    private int userId;
    private String username;
    private String profilePicture;
    private String activityType;
    private int movieId;
    private String movieTitle;
    private int listId;
    private String listName;
    private LocalDateTime timestamp;
    private String additionalInfo;

    public FriendActivity() {}

    public FriendActivity(int userId, String username, String activityType, int movieId, String movieTitle, LocalDateTime timestamp) {
        this.userId = userId;
        this.username = username;
        this.activityType = activityType;
        this.movieId = movieId;
        this.movieTitle = movieTitle;
        this.timestamp = timestamp;
    }

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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public String getActivityType() {
        return activityType;
    }

    public void setActivityType(String activityType) {
        this.activityType = activityType;
    }

    public int getMovieId() {
        return movieId;
    }

    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }

    public String getMovieTitle() {
        return movieTitle;
    }

    public void setMovieTitle(String movieTitle) {
        this.movieTitle = movieTitle;
    }

    public int getListId() {
        return listId;
    }

    public void setListId(int listId) {
        this.listId = listId;
    }

    public String getListName() {
        return listName;
    }

    public void setListName(String listName) {
        this.listName = listName;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getAdditionalInfo() {
        return additionalInfo;
    }

    public void setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
    }

    public String getActivityDescription() {
        switch (activityType) {
            case "liked":
                return "liked";
            case "added_to_watchlist":
                return "added to watchlist";
            case "added_to_list":
                return "added to list \"" + listName + "\"";
            case "created_list":
                return "created list \"" + listName + "\"";
            case "reviewed":
                return "reviewed";
            default:
                return "interacted with";
        }
    }
} 