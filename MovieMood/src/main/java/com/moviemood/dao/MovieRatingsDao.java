package com.moviemood.dao;

import com.moviemood.bean.MovieRating;
import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MovieRatingsDao {
    private final DataSource dataSource;

    public MovieRatingsDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Add or update a movie rating
     * @param rating The MovieRating object to save
     * @throws SQLException if database operation fails
     */
    public void addOrUpdateMovieRating(MovieRating rating) throws SQLException {
        String sql = "MERGE INTO movie_ratings (movie_id, user_id, score_value, score_date) " +
                "KEY (movie_id, user_id) VALUES (?, ?, ?, ?)";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, rating.getMovieId());
            statement.setInt(2, rating.getUserId());
            statement.setDouble(3, rating.getScoreValue());
            statement.setDate(4, new java.sql.Date(rating.getScoreDate().getTime()));

            statement.executeUpdate();
        }
    }


    /**
     * Get a specific rating by user and movie
     * @param userId The user ID
     * @param movieId The movie ID
     * @return MovieRating object or null if not found
     * @throws SQLException if database operation fails
     */
    public MovieRating getRatingByUserAndMovie(int userId, int movieId) throws SQLException {
        String sql = "SELECT * FROM movie_ratings WHERE user_id = ? AND movie_id = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);
            statement.setInt(2, movieId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return createMovieRatingFromResultSet(resultSet);
                }
                return null;
            }
        }
    }

    /**
     * Get average rating for a specific movie
     * @param movieId The movie ID
     * @return Average rating or 0.0 if no ratings exist
     * @throws SQLException if database operation fails
     */
    public double getAverageRatingByMovie(int movieId) throws SQLException {
        String sql = "SELECT AVG(score_value) as avg_rating FROM movie_ratings WHERE movie_id = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, movieId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getDouble("avg_rating");
                }
                return 0.0;
            }
        }
    }

    /**
     * Delete a rating by user and movie
     * @param userId The user ID
     * @param movieId The movie ID
     * @return true if rating was deleted, false if not found
     * @throws SQLException if database operation fails
     */
    public boolean deleteRating(int userId, int movieId) throws SQLException {
        String sql = "DELETE FROM movie_ratings WHERE user_id = ? AND movie_id = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);
            statement.setInt(2, movieId);

            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        }
    }

    /**
     * Check if a user has rated a specific movie
     * @param userId The user ID
     * @param movieId The movie ID
     * @return true if user has rated the movie, false otherwise
     * @throws SQLException if database operation fails
     */
    public boolean hasUserRatedMovie(int userId, int movieId) throws SQLException {
        String sql = "SELECT 1 FROM movie_ratings WHERE user_id = ? AND movie_id = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);
            statement.setInt(2, movieId);

            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    /**
     * Helper method to create MovieRating object from ResultSet
     * @param resultSet The ResultSet containing rating data
     * @return MovieRating object
     * @throws SQLException if database operation fails
     */
    private MovieRating createMovieRatingFromResultSet(ResultSet resultSet) throws SQLException {
        MovieRating rating = new MovieRating();
        rating.setMovieId(resultSet.getInt("movie_id"));
        rating.setUserId(resultSet.getInt("user_id"));
        rating.setScoreValue(resultSet.getDouble("score_value"));
        rating.setScoreDate(resultSet.getDate("score_date"));
        return rating;
    }

    /**
     * Get the average rating for a specific movie.
     * Returns -1 if the movie has no ratings.
     */
    public double getAverageMovieRating(int movieId) throws SQLException {
        String sql = "SELECT AVG(score_value) AS avg_rating FROM movie_ratings WHERE movie_id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, movieId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    double avg = rs.getDouble("avg_rating");
                    if (rs.wasNull()) {
                        return -1; // No ratings available
                    }
                    return Math.round(avg*2 * 10.0) / 10.0; // Rounded to 1 decimal place
                }
            }
        }

        return -1;
    }


}