package com.moviemood.bean;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class MovieReview {
    private int id;
    private int userId;
    private int movieId;
    private String reviewText;
    private Timestamp createdAt;

    // Constructors
    public MovieReview() {
    }

    public MovieReview(int id,int userId, int movieId, String reviewText, Timestamp createdAt) {
        this.id = id;
        this.userId = userId;
        this.movieId = movieId;
        this.reviewText = reviewText;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    // Getters and setters
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getMovieId() {
        return movieId;
    }

    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }

    public String getReviewText() {
        return reviewText;
    }

    public void setReviewText(String reviewText) {
        this.reviewText = reviewText;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public String getFormattedDate() {
        if (createdAt == null) return "";
        return new SimpleDateFormat("MMMM d, yyyy").format(createdAt);
    }

    // Optional: toString()
    @Override
    public String toString() {
        return "MovieReview{" +
                "userId=" + userId +
                ", movieId=" + movieId +
                ", reviewText='" + reviewText + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
