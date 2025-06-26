package com.moviemood.dao;

import com.moviemood.bean.MovieReview;
import org.apache.commons.dbcp2.BasicDataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MovieReviewsDao {
    private final BasicDataSource dataSource;

    public MovieReviewsDao(BasicDataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Get all reviews for a specific movie
     */
    public List<MovieReview> getMovieReviews(int movieId) throws SQLException {
        List<MovieReview> reviews = new ArrayList<>();
        String sql = "SELECT id, user_id, movie_id, review_text, created_at FROM movie_reviews WHERE movie_id = ? ORDER BY created_at DESC";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, movieId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    MovieReview review = new MovieReview(
                            rs.getInt("id"),
                            rs.getInt("user_id"),
                            rs.getInt("movie_id"),
                            rs.getString("review_text"),
                            rs.getTimestamp("created_at")
                    );
                    reviews.add(review);
                }
            }
        }

        return reviews;
    }

    /**
     * Get all reviews written by a specific user
     */
    public List<MovieReview> getUserMovieReviews(int userId) throws SQLException {
        List<MovieReview> reviews = new ArrayList<>();
        String sql = "SELECT id, user_id, movie_id, review_text, created_at FROM movie_reviews WHERE user_id = ? ORDER BY created_at DESC";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    MovieReview review = new MovieReview(
                            rs.getInt("id"),
                            rs.getInt("user_id"),
                            rs.getInt("movie_id"),
                            rs.getString("review_text"),
                            rs.getTimestamp("created_at")
                    );
                    reviews.add(review);
                }
            }
        }

        return reviews;
    }

    /**
     * Get a specific review by its ID
     */
    public MovieReview getMovieReview(int id) throws SQLException {
        String sql = "SELECT id, user_id, movie_id, review_text, created_at FROM movie_reviews WHERE id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new MovieReview(
                            rs.getInt("id"),
                            rs.getInt("user_id"),
                            rs.getInt("movie_id"),
                            rs.getString("review_text"),
                            rs.getTimestamp("created_at")
                    );
                }
            }
        }

        return null; // Review not found
    }

    /**
     * Delete a review by its ID
     */
    public void deleteMovieReview(int id) throws SQLException {
        String sql = "DELETE FROM movie_reviews WHERE id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    /**
     * Add a new movie review
     */
    public void addMovieReview(MovieReview review) throws SQLException {
        String sql = "INSERT INTO movie_reviews (user_id, movie_id, review_text) VALUES (?, ?, ?)";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, review.getUserId());
            stmt.setInt(2, review.getMovieId());
            stmt.setString(3, review.getReviewText());
            stmt.executeUpdate();
        }
    }


}