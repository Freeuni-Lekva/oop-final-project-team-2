package com.moviemood.dao;

import com.moviemood.bean.UserList;
import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserListDao {

    private final BasicDataSource dataSource;

    public UserListDao(BasicDataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Create a new user list
     *
     * @param userList The UserList object to create
     * @return The created UserList with generated ID, or null if failed
     */
    public UserList createList(UserList userList) {
        String sql = "INSERT INTO user_lists (user_id, name, description, is_public) VALUES (?, ?, ?, ?)";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setInt(1, userList.getUserId());
            statement.setString(2, userList.getName());
            statement.setString(3, userList.getDescription());
            statement.setBoolean(4, userList.isPublic());

            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        userList.setId(generatedKeys.getInt(1));
                        return userList;
                    }
                }
            }

        } catch (SQLException e) {
            System.err.println("Error creating list for user ID: " + userList.getUserId());
            e.printStackTrace();
            throw new RuntimeException("Failed to create list", e);
        }

        return null;
    }

    /**
     * Get all lists for a specific user
     *
     * @param userId The user ID
     * @return List of UserList objects
     */
    public List<UserList> getUserLists(int userId) {
        List<UserList> lists = new ArrayList<>();
        String sql = "SELECT id, user_id, name, description, is_public, created_at, updated_at " +
                "FROM user_lists WHERE user_id = ? ORDER BY updated_at DESC";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, userId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    UserList list = new UserList(
                            resultSet.getInt("id"),
                            resultSet.getInt("user_id"),
                            resultSet.getString("name"),
                            resultSet.getString("description"),
                            resultSet.getBoolean("is_public"),
                            resultSet.getTimestamp("created_at"),
                            resultSet.getTimestamp("updated_at")
                    );
                    lists.add(list);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving lists for user ID: " + userId);
            e.printStackTrace();
            throw new RuntimeException("Failed to retrieve user lists", e);
        }

        return lists;
    }

    /**
     * Get only public lists for a specific user (for viewing other users' profiles)
     *
     * @param userId The user ID
     * @return List of public UserList objects
     */
    public List<UserList> getPublicUserLists(int userId) {
        List<UserList> lists = new ArrayList<>();
        String sql = "SELECT id, user_id, name, description, is_public, created_at, updated_at " +
                "FROM user_lists WHERE user_id = ? AND is_public = 1 ORDER BY updated_at DESC";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, userId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    UserList list = new UserList(
                            resultSet.getInt("id"),
                            resultSet.getInt("user_id"),
                            resultSet.getString("name"),
                            resultSet.getString("description"),
                            resultSet.getBoolean("is_public"),
                            resultSet.getTimestamp("created_at"),
                            resultSet.getTimestamp("updated_at")
                    );
                    lists.add(list);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving public lists for user ID: " + userId);
            e.printStackTrace();
            throw new RuntimeException("Failed to retrieve public user lists", e);
        }

        return lists;
    }

    /**
     * Get a specific list by ID
     *
     * @param listId The list ID
     * @return UserList object or null if not found
     */
    public UserList getListById(int listId) {
        String sql = "SELECT id, user_id, name, description, is_public, created_at, updated_at " +
                "FROM user_lists WHERE id = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, listId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return new UserList(
                            resultSet.getInt("id"),
                            resultSet.getInt("user_id"),
                            resultSet.getString("name"),
                            resultSet.getString("description"),
                            resultSet.getBoolean("is_public"),
                            resultSet.getTimestamp("created_at"),
                            resultSet.getTimestamp("updated_at")
                    );
                }
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving list by ID: " + listId);
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Update a list's details
     *
     * @param userList The UserList object with updated information
     * @return true if successfully updated, false otherwise
     */
    public boolean updateList(UserList userList) {
        String sql = "UPDATE user_lists SET name = ?, description = ?, is_public = ? WHERE id = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, userList.getName());
            statement.setString(2, userList.getDescription());
            statement.setBoolean(3, userList.isPublic());
            statement.setInt(4, userList.getId());

            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error updating list ID: " + userList.getId());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Delete a list and all its items
     *
     * @param listId The list ID to delete
     * @return true if successfully deleted, false otherwise
     */
    public boolean deleteList(int listId) {
        String sql = "DELETE FROM user_lists WHERE id = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, listId);

            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error deleting list ID: " + listId);
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Add a movie to a list
     *
     * @param listId  The list ID
     * @param movieId The movie ID to add
     * @return true if successfully added, false if already exists or error occurred
     */
    public boolean addMovieToList(int listId, int movieId) {
        String sql = "INSERT INTO user_list_items (list_id, movie_id) VALUES (?, ?)";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, listId);
            statement.setInt(2, movieId);

            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            // Check if it's a duplicate key error (movie already in list)
            if (e.getErrorCode() == 1062 || e.getMessage().contains("Duplicate entry")) {
                System.out.println("Movie " + movieId + " is already in list " + listId);
                return false;
            }

            System.err.println("Error adding movie to list. List ID: " + listId + ", Movie ID: " + movieId);
            e.printStackTrace();
            throw new RuntimeException("Failed to add movie to list", e);
        }
    }

    /**
     * Remove a movie from a list
     *
     * @param listId  The list ID
     * @param movieId The movie ID to remove
     * @return true if successfully removed, false if not found or error occurred
     */
    public boolean removeMovieFromList(int listId, int movieId) {
        String sql = "DELETE FROM user_list_items WHERE list_id = ? AND movie_id = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, listId);
            statement.setInt(2, movieId);

            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error removing movie from list. List ID: " + listId + ", Movie ID: " + movieId);
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Get all movie IDs in a specific list
     *
     * @param listId The list ID
     * @return List of movie IDs in the list
     */
    public List<Integer> getListMovies(int listId) {
        List<Integer> movieIds = new ArrayList<>();
        String sql = "SELECT movie_id FROM user_list_items WHERE list_id = ? ORDER BY added_at DESC";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, listId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    movieIds.add(resultSet.getInt("movie_id"));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving movies for list ID: " + listId);
            e.printStackTrace();
            throw new RuntimeException("Failed to retrieve list movies", e);
        }

        return movieIds;
    }

    /**
     * Check if a movie is in a specific list
     *
     * @param listId  The list ID
     * @param movieId The movie ID to check
     * @return true if movie is in list, false otherwise
     */
    public boolean isMovieInList(int listId, int movieId) {
        String sql = "SELECT 1 FROM user_list_items WHERE list_id = ? AND movie_id = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, listId);
            statement.setInt(2, movieId);

            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }

        } catch (SQLException e) {
            System.err.println("Error checking if movie is in list. List ID: " + listId + ", Movie ID: " + movieId);
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Get the count of lists for a user
     *
     * @param userId The user ID
     * @return Number of lists the user has created
     */
    public int getListsCount(int userId) {
        String sql = "SELECT COUNT(*) FROM user_lists WHERE user_id = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, userId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error getting lists count for user ID: " + userId);
            e.printStackTrace();
        }

        return 0;
    }

    /**
     * Get the count of movies in a specific list
     *
     * @param listId The list ID
     * @return Number of movies in the list
     */
    public int getListMovieCount(int listId) {
        String sql = "SELECT COUNT(*) FROM user_list_items WHERE list_id = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, listId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error getting movie count for list ID: " + listId);
            e.printStackTrace();
        }

        return 0;
    }
} 