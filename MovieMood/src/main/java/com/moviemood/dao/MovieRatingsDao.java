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
        String sql = "INSERT INTO movie_ratings (movie_id, user_id, score_value, score_date) " +
                "VALUES (?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE " +
                "score_value = VALUES(score_value), " +
                "score_date = VALUES(score_date)";

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
     * Get all ratings for a specific movie
     * @param movieId The movie ID
     * @return List of MovieRating objects
     * @throws SQLException if database operation fails
     */
    public List<MovieRating> getRatingsByMovie(int movieId) throws SQLException {
        String sql = "SELECT * FROM movie_ratings WHERE movie_id = ?";
        List<MovieRating> ratings = new ArrayList<>();

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, movieId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    ratings.add(createMovieRatingFromResultSet(resultSet));
                }
            }
        }

        return ratings;
    }

    /**
     * Get all ratings by a specific user
     * @param userId The user ID
     * @return List of MovieRating objects
     * @throws SQLException if database operation fails
     */
    public List<MovieRating> getRatingsByUser(int userId) throws SQLException {
        String sql = "SELECT * FROM movie_ratings WHERE user_id = ?";
        List<MovieRating> ratings = new ArrayList<>();

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    ratings.add(createMovieRatingFromResultSet(resultSet));
                }
            }
        }

        return ratings;
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
     * Get total number of ratings for a specific movie
     * @param movieId The movie ID
     * @return Number of ratings
     * @throws SQLException if database operation fails
     */
    public int getRatingCountByMovie(int movieId) throws SQLException {
        String sql = "SELECT COUNT(*) as rating_count FROM movie_ratings WHERE movie_id = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, movieId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("rating_count");
                }
                return 0;
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
     * Delete all ratings for a specific movie
     * @param movieId The movie ID
     * @return Number of ratings deleted
     * @throws SQLException if database operation fails
     */
    public int deleteAllRatingsForMovie(int movieId) throws SQLException {
        String sql = "DELETE FROM movie_ratings WHERE movie_id = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, movieId);
            return statement.executeUpdate();
        }
    }

    /**
     * Delete all ratings by a specific user
     * @param userId The user ID
     * @return Number of ratings deleted
     * @throws SQLException if database operation fails
     */
    public int deleteAllRatingsForUser(int userId) throws SQLException {
        String sql = "DELETE FROM movie_ratings WHERE user_id = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);
            return statement.executeUpdate();
        }
    }

    /**
     * Get top rated movies with their average ratings
     * @param limit Maximum number of movies to return
     * @return List of MovieRating objects with average ratings
     * @throws SQLException if database operation fails
     */
    public List<MovieRating> getTopRatedMovies(int limit) throws SQLException {
        String sql = "SELECT movie_id, AVG(score_value) as avg_rating, COUNT(*) as rating_count " +
                "FROM movie_ratings " +
                "GROUP BY movie_id " +
                "HAVING COUNT(*) >= 1 " +
                "ORDER BY avg_rating DESC " +
                "LIMIT ?";

        List<MovieRating> topRated = new ArrayList<>();

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, limit);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    MovieRating rating = new MovieRating();
                    rating.setMovieId(resultSet.getInt("movie_id"));
                    rating.setScoreValue(resultSet.getDouble("avg_rating"));
                    // Note: This is an aggregate result, so user_id and score_date are not meaningful
                    topRated.add(rating);
                }
            }
        }

        return topRated;
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
}