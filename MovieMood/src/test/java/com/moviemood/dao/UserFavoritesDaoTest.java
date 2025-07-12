package com.moviemood.dao;

import com.moviemood.util.TestDatabaseHelper;
import org.apache.commons.dbcp2.BasicDataSource;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

/**
 * Tests for UserFavoritesDao using H2 in-memory database.
 */
public class UserFavoritesDaoTest {

    private BasicDataSource testDataSource;
    private UserFavoritesDao userFavoritesDao;

    @Before
    public void setUp() throws SQLException {
        testDataSource = TestDatabaseHelper.createTestDataSource();
        TestDatabaseHelper.validateTestDatabase(testDataSource);

        TestDatabaseHelper.createUsersTable(testDataSource);
        TestDatabaseHelper.createUserFavoritesTable(testDataSource);

        userFavoritesDao = new UserFavoritesDao(testDataSource);

        insertTestUsers();
    }

    @After
    public void tearDown() throws SQLException {
        if (testDataSource != null) {
            TestDatabaseHelper.dropAllTables(testDataSource);
            testDataSource.close();
        }
    }

    private void insertTestUsers() throws SQLException {
        String sql = "INSERT INTO users (id, username, email, password_hash, is_verified) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = testDataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, 1);
            stmt.setString(2, "alice");
            stmt.setString(3, "alice@test.com");
            stmt.setString(4, "hash1");
            stmt.setBoolean(5, true);
            stmt.executeUpdate();

            stmt.setInt(1, 2);
            stmt.setString(2, "bob");
            stmt.setString(3, "bob@test.com");
            stmt.setString(4, "hash2");
            stmt.setBoolean(5, true);
            stmt.executeUpdate();
        }
    }

    private void addFavoriteDirectly(int userId, int movieId) throws SQLException {
        String sql = "INSERT INTO user_favorites (user_id, movie_id) VALUES (?, ?)";
        try (Connection conn = testDataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, movieId);
            stmt.executeUpdate();
        }
    }

    @Test
    public void testGetUserFavorites_Empty() {
        List<Integer> favorites = userFavoritesDao.getUserFavorites(1);

        assertNotNull("Favorites list should not be null", favorites);
        assertEquals("Favorites list should be empty", 0, favorites.size());
    }

    @Test
    public void testGetUserFavorites_WithMovies() throws SQLException {
        addFavoriteDirectly(1, 100);
        addFavoriteDirectly(1, 200);

        List<Integer> favorites = userFavoritesDao.getUserFavorites(1);

        assertNotNull("Favorites list should not be null", favorites);
        assertEquals("Should have 2 favorites", 2, favorites.size());
        assertTrue("Should contain movie 100", favorites.contains(100));
        assertTrue("Should contain movie 200", favorites.contains(200));
    }

    @Test
    public void testAddMovieToFavorites_Success() {
        boolean result = userFavoritesDao.addMovieToFavorites(1, 100);

        assertTrue("Should successfully add movie to favorites", result);
        assertTrue("Movie should be in favorites", userFavoritesDao.isMovieInFavorites(1, 100));
        assertEquals("Favorites count should be 1", 1, userFavoritesDao.getFavoritesCount(1));
    }





    @Test
    public void testRemoveMovieFromFavorites_Success() throws SQLException {
        addFavoriteDirectly(1, 100);

        boolean result = userFavoritesDao.removeMovieFromFavorites(1, 100);

        assertTrue("Should successfully remove movie from favorites", result);
        assertFalse("Movie should not be in favorites", userFavoritesDao.isMovieInFavorites(1, 100));
        assertEquals("Favorites count should be 0", 0, userFavoritesDao.getFavoritesCount(1));
    }

    @Test
    public void testRemoveMovieFromFavorites_NotFound() {
        boolean result = userFavoritesDao.removeMovieFromFavorites(1, 999);

        assertFalse("Should return false when movie not in favorites", result);
    }



    @Test
    public void testIsMovieInFavorites_True() throws SQLException {
        addFavoriteDirectly(1, 100);

        assertTrue("Movie should be in favorites", userFavoritesDao.isMovieInFavorites(1, 100));
    }

    @Test
    public void testIsMovieInFavorites_False() {
        assertFalse("Movie should not be in favorites", userFavoritesDao.isMovieInFavorites(1, 999));
    }

    @Test
    public void testGetFavoritesCount_Zero() {
        assertEquals("Count should be 0 for user with no favorites", 0, userFavoritesDao.getFavoritesCount(1));
    }

    @Test
    public void testGetFavoritesCount_Multiple() throws SQLException {
        addFavoriteDirectly(1, 100);
        addFavoriteDirectly(1, 200);
        addFavoriteDirectly(1, 300);

        assertEquals("Count should be 3", 3, userFavoritesDao.getFavoritesCount(1));
    }



    @Test
    public void testGetUserFavorites_DifferentUsers() throws SQLException {
        addFavoriteDirectly(1, 100);
        addFavoriteDirectly(2, 200);

        List<Integer> user1Favorites = userFavoritesDao.getUserFavorites(1);
        List<Integer> user2Favorites = userFavoritesDao.getUserFavorites(2);

        assertEquals("User 1 should have 1 favorite", 1, user1Favorites.size());
        assertEquals("User 2 should have 1 favorite", 1, user2Favorites.size());
        assertTrue("User 1 should have movie 100", user1Favorites.contains(100));
        assertTrue("User 2 should have movie 200", user2Favorites.contains(200));
    }

    @Test
    public void testGetFavoritesCount_NonExistentUser() {
        assertEquals("Non-existent user should have 0 favorites", 0, userFavoritesDao.getFavoritesCount(999));
    }

    @Test
    public void testComplexScenario() throws SQLException {
        // Add multiple movies to favorites
        userFavoritesDao.addMovieToFavorites(1, 100);
        userFavoritesDao.addMovieToFavorites(1, 200);
        userFavoritesDao.addMovieToFavorites(1, 300);

        // Verify all are added
        assertEquals("Should have 3 favorites", 3, userFavoritesDao.getFavoritesCount(1));

        // Remove one movie
        userFavoritesDao.removeMovieFromFavorites(1, 200);

        // Verify the removal
        assertEquals("Should have 2 favorites", 2, userFavoritesDao.getFavoritesCount(1));
        assertFalse("Movie 200 should not be in favorites", userFavoritesDao.isMovieInFavorites(1, 200));
        assertTrue("Movie 100 should still be in favorites", userFavoritesDao.isMovieInFavorites(1, 100));
        assertTrue("Movie 300 should still be in favorites", userFavoritesDao.isMovieInFavorites(1, 300));
    }
}