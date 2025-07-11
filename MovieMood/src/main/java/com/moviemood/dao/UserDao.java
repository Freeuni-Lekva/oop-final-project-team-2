package com.moviemood.dao;

import com.moviemood.exceptions.UserAlreadyExistsException;
import org.apache.commons.dbcp2.BasicDataSource;

import com.moviemood.bean.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * This is DAO for the User entity.
 * Handles all database operations related to users, including:
 * - Retrieving a user by email
 * - Inserting a new user
 * This class assumes that the "users" table exists in the connected MySQL database.
 */

public class UserDao {
    private final BasicDataSource dataSource;

    public UserDao(BasicDataSource dataSource) { this.dataSource = dataSource; }

    /**
     * Retrieves a user from the database by email.
     * takes email as a parameter.
     * returns User if exists or null if does not exist.
     */
    public User getUserByEmail(String email) {

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM users WHERE email = ?")) {
            statement.setString(1, email);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("id");
                String username = rs.getString("username");
                String passwordHash = rs.getString("password_hash");
                String remember_token = rs.getString("remember_token");

                // Now get and set verification fields.
                boolean verified = rs.getBoolean("is_verified");
                String verificationCode = rs.getString("verification_code");
                Timestamp verificationCodeExpiry = rs.getTimestamp("verification_code_expiry");
                
                // Safely get profile picture (column might not exist in older databases)
                String profilePicture = null;
                try {
                    profilePicture = rs.getString("profile_picture");
                } catch (SQLException e) {
                    // Column doesn't exist yet, use null
                }

                // Create and return User
                User user = new User(id, username, email, passwordHash, remember_token);
                user.setVerified(verified);
                user.setVerificationCode(verificationCode);
                user.setVerificationCodeExpiry(verificationCodeExpiry);
                user.setProfilePicture(profilePicture);

                return user;

            } else {
                return null;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to make connection to database", e);
        }
    }


    /**
     * Retrieves a user from the database by username.
     * takes username as a parameter.
     * returns User if exists or null if does not exist.
     */
    public User getUserByUsername(String username) {

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM users WHERE username = ?")) {
            statement.setString(1, username);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("id");
                String email = rs.getString("email");
                String passwordHash = rs.getString("password_hash");
                String remember_token = rs.getString("remember_token");


                // Now get and set verification fields.
                boolean verified = rs.getBoolean("is_verified");
                String verificationCode = rs.getString("verification_code");
                Timestamp verificationCodeExpiry = rs.getTimestamp("verification_code_expiry");
                
                // Safely get profile picture (column might not exist in older databases)
                String profilePicture = null;
                try {
                    profilePicture = rs.getString("profile_picture");
                } catch (SQLException e) {
                    // Column doesn't exist yet, use null
                }

                // Create and return User
                User user = new User(id, username, email, passwordHash, remember_token);
                user.setVerified(verified);
                user.setVerificationCode(verificationCode);
                user.setVerificationCodeExpiry(verificationCodeExpiry);
                user.setProfilePicture(profilePicture);

                return user;

            } else {
                return null;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to make connection to database", e);
        }
    }

    /**
     * Searches for users by username using partial matching
     * @param searchQuery The search query to match against usernames
     * @return List of users whose usernames contain the search query
     */
    public List<User> searchUserByQuery(String searchQuery) {
        List<User> users = new ArrayList<>();
        String query = "SELECT * FROM users WHERE username LIKE ? ORDER BY username";
        
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            
            statement.setString(1, "%" + searchQuery + "%");
            ResultSet rs = statement.executeQuery();
            
            while (rs.next()) {
                int id = rs.getInt("id");
                String username = rs.getString("username");
                String email = rs.getString("email");
                String passwordHash = rs.getString("password_hash");
                String remember_token = rs.getString("remember_token");

                // Safely get verification fields (columns might not exist in older databases)
                boolean verified = false;
                String verificationCode = null;
                Timestamp verificationCodeExpiry = null;
                try {
                    verified = rs.getBoolean("is_verified");
                    verificationCode = rs.getString("verification_code");
                    verificationCodeExpiry = rs.getTimestamp("verification_code_expiry");
                } catch (SQLException e) {
                    // Columns don't exist yet, use defaults
                }

                // Safely get profile picture (column might not exist in older databases)
                String profilePicture = null;
                try {
                    profilePicture = rs.getString("profile_picture");
                } catch (SQLException e) {
                    // Column doesn't exist yet, use null
                }

                User user = new User(id, username, email, passwordHash, remember_token);
                user.setVerified(verified);
                user.setVerificationCode(verificationCode);
                user.setVerificationCodeExpiry(verificationCodeExpiry);
                user.setProfilePicture(profilePicture);
                users.add(user);
            }
            
        } catch (SQLException e) {
            throw new RuntimeException("Failed to search users", e);
        }
        
        return users;
    }

    /**
     * Inserts a user in the database.
     * takes username, email and password (hashed) as parameters
     * !!!! SEE OVERLOADED VERSION BELOW
     */

    public void insertUser(String username, String email, String passwordHash) throws UserAlreadyExistsException {
        if (getUserByEmail(email) != null) {
            throw new UserAlreadyExistsException("Email is already registered");
        }

        if (getUserByUsername(username) != null) {
            throw new UserAlreadyExistsException("Username is already taken");
        }


        String query = "INSERT INTO users (username, email, password_hash, remember_token) VALUES (?, ?, ?, ?)";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, username);
            statement.setString(2, email);
            statement.setString(3, passwordHash);
            statement.setNull(4, java.sql.Types.VARCHAR); // No token

            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0) {
                throw new RuntimeException("Insert failed. no rows affected.");
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to make connection to database", e);
        }

    }

    /**
     * Overloading of insertUser method!!!
     * Inserts the user in the database.
     * takes username, email password (hashed). In this overloaded
     * version this method also takes verificationCode and expiry as parameters
     */
    public void insertUser(String username, String email, String passwordHash,
                           String verificationCode, Timestamp expiry) throws UserAlreadyExistsException {
        if (getUserByEmail(email) != null) {
            throw new UserAlreadyExistsException("Email is already registered");
        }

        if (getUserByUsername(username) != null) {
            throw new UserAlreadyExistsException("Username is already taken");
        }


        String query = "INSERT INTO users (username, email, password_hash, remember_token, " +
                "is_verified, verification_code, verification_code_expiry) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, username);
            statement.setString(2, email);
            statement.setString(3, passwordHash);
            statement.setNull(4, java.sql.Types.VARCHAR); // No token
            statement.setBoolean(5, false);
            statement.setString(6, verificationCode);
            statement.setTimestamp(7, expiry);

            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0) {
                throw new RuntimeException("Insert failed. no rows affected.");
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to make connection to database", e);
        }
    }


    /*
     * Updates token.
     */
    public void updateRememberToken(String username, String token) {
        String query = "UPDATE users SET remember_token = ? WHERE username = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, token);
            statement.setString(2, username);

            int rows = statement.executeUpdate();

            if (rows == 0) {
                throw new RuntimeException("No user found to update remember token.");
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to update remember token", e);
        }
    }


    /*
     * Searches user using token and returns Use object.
     */
    public User getUserByToken(String token) {
        String query = "SELECT * FROM users WHERE remember_token = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, token);
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                int id = rs.getInt("id");
                String username = rs.getString("username");
                String email = rs.getString("email");
                String passwordHash = rs.getString("password_hash");

                // Now get and set verification fields.
                boolean verified = rs.getBoolean("is_verified");
                String verificationCode = rs.getString("verification_code");
                Timestamp verificationCodeExpiry = rs.getTimestamp("verification_code_expiry");
                
                // Safely get profile picture (column might not exist in older databases)
                String profilePicture = null;
                try {
                    profilePicture = rs.getString("profile_picture");
                } catch (SQLException e) {
                    // Column doesn't exist yet, use null
                }

                User user = new User(id, username, email, passwordHash, token);
                user.setVerified(verified);
                user.setVerificationCode(verificationCode);
                user.setVerificationCodeExpiry(verificationCodeExpiry);
                user.setProfilePicture(profilePicture);

                return user;
            } else {
                return null;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to retrieve user by token", e);
        }
    }


    /*
     * For the user, sets is_verified to true.
     * Returns true if succeeds, false otherwise.
     */
    public boolean verifyUser(int userID) {
        String query = "UPDATE users\n" +
                "SET is_verified = true,\n" +
                "    verification_code = NULL,\n" +
                "    verification_code_expiry = NULL\n" +
                "WHERE id = ?;";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, userID);
            int rs = statement.executeUpdate();
            return rs > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /*
     * updates verification code for user.
     * returns true if succeeds, false otherwise.
     */
    public boolean updateVerificationCode(String email, String verificationCode, Timestamp expiry) {
        String query = "UPDATE users\n" +
                "SET verification_code = ?,\n" +
                "    verification_code_expiry = ?\n" +
                "WHERE email = ?;";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, verificationCode);
            statement.setTimestamp(2, expiry);
            statement.setString(3, email);
            int rs = statement.executeUpdate();
            return rs > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /*
     * Updates username for a user.
     * Returns true if succeeds, false otherwise.
     */
    public boolean updateUsername(int userId, String newUsername) throws UserAlreadyExistsException {
        // Check if username is already taken by another user
        User existingUser = getUserByUsername(newUsername);
        if (existingUser != null && existingUser.getId() != userId) {
            throw new UserAlreadyExistsException("Username is already taken");
        }

        String query = "UPDATE users SET username = ? WHERE id = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, newUsername);
            statement.setInt(2, userId);
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /*
     * Updates password for a user.
     * Returns true if succeeds, false otherwise.
     */
    public boolean updatePassword(int userId, String newPasswordHash) {
        String query = "UPDATE users SET password_hash = ? WHERE id = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, newPasswordHash);
            statement.setInt(2, userId);
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /*
     * Updates profile picture for a user.
     * Returns true if succeeds, false otherwise.
     */
    public boolean updateProfilePicture(int userId, String profilePicturePath) {
        String query = "UPDATE users SET profile_picture = ? WHERE id = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, profilePicturePath);
            statement.setInt(2, userId);
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }







}
