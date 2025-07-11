package com.moviemood.bean;

import java.util.Date;

public class MovieRating {
    private int movieId;
    private int userId;
    private double scoreValue;
    private Date scoreDate;

    // Default constructor
    public MovieRating() {
        this.scoreDate = new Date(); // Set current date by default
    }

    // Constructor with parameters
    public MovieRating(int movieId, int userId, double scoreValue) {
        this.movieId = movieId;
        this.userId = userId;
        this.scoreValue = scoreValue;
        this.scoreDate = new Date();
    }

    // Constructor with all parameters
    public MovieRating(int movieId, int userId, double scoreValue, Date scoreDate) {
        this.movieId = movieId;
        this.userId = userId;
        this.scoreValue = scoreValue;
        this.scoreDate = scoreDate;
    }

    // Getters and Setters
    public int getMovieId() {
        return movieId;
    }

    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public double getScoreValue() {
        return scoreValue;
    }

    public void setScoreValue(double scoreValue) {
        this.scoreValue = scoreValue;
    }

    public Date getScoreDate() {
        return scoreDate;
    }

    public void setScoreDate(Date scoreDate) {
        this.scoreDate = scoreDate;
    }

    // Utility methods
    public String getFormattedScoreValue() {
        return String.format("%.1f", scoreValue);
    }

    public String getFormattedDate() {
        if (scoreDate != null) {
            return new java.text.SimpleDateFormat("MMM dd, yyyy").format(scoreDate);
        }
        return "";
    }

    public int getScoreAsInt() {
        return (int) Math.round(scoreValue);
    }

    public String getStarsDisplay() {
        int fullStars = (int) scoreValue;
        boolean hasHalfStar = (scoreValue - fullStars) >= 0.5;
        StringBuilder stars = new StringBuilder();

        // Add full stars
        for (int i = 0; i < fullStars; i++) {
            stars.append("★");
        }

        // Add half star if needed
        if (hasHalfStar) {
            stars.append("☆");
        }

        // Add empty stars to make it 5 total
        int totalStars = fullStars + (hasHalfStar ? 1 : 0);
        for (int i = totalStars; i < 5; i++) {
            stars.append("☆");
        }

        return stars.toString();
    }

    @Override
    public String toString() {
        return "MovieRating{" +
                "movieId=" + movieId +
                ", userId=" + userId +
                ", scoreValue=" + scoreValue +
                ", scoreDate=" + scoreDate +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MovieRating that = (MovieRating) o;
        return movieId == that.movieId && userId == that.userId;
    }

    @Override
    public int hashCode() {
        return movieId * 31 + userId;
    }
}
