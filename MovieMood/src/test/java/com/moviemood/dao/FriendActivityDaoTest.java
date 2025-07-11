package com.moviemood.dao;

import com.moviemood.bean.FriendActivity;
import com.moviemood.util.TestDatabaseHelper;
import org.apache.commons.dbcp2.BasicDataSource;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Tests for FriendActivityDao using H2 in-memory database.
 */
public class FriendActivityDaoTest {

    private BasicDataSource testDataSource;
    private FriendActivityDao friendActivityDao;

    /**
     * Sets up fresh H2 database before each test.
     */
    @Before
    public void setUp() throws SQLException {
        testDataSource = TestDatabaseHelper.createTestDataSource();
        TestDatabaseHelper.validateTestDatabase(testDataSource);
        TestDatabaseHelper.createUsersTable(testDataSource);
        TestDatabaseHelper.createFriendshipsTable(testDataSource);
        TestDatabaseHelper.createUserFavoritesTable(testDataSource);
        TestDatabaseHelper.createUserWatchlistTable(testDataSource);
        TestDatabaseHelper.createMovieReviewsTable(testDataSource);
        TestDatabaseHelper.createUserListsTable(testDataSource);
        TestDatabaseHelper.createUserListItemsTable(testDataSource);
        TestDatabaseHelper.createFriendsActivityView(testDataSource);
        
        friendActivityDao = new FriendActivityDao(testDataSource);
    }

    /**
     * Cleans up database after each test.
     */
    @After
    public void tearDown() throws SQLException {
        if (testDataSource != null) {
            TestDatabaseHelper.dropAllTables(testDataSource);
            testDataSource.close();
        }
    }

    /**
     * Helper method to insert test users.
     */
    private void insertTestUsers() throws SQLException {
        insertUser(1, "user1", "user1@test.com");
        insertUser(2, "user2", "user2@test.com");
        insertUser(3, "user3", "user3@test.com");
    }
    
    private void insertUser(int id, String username, String email) throws SQLException {
        String sql = "INSERT INTO users (id, username, email, password_hash, is_verified) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = testDataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.setString(2, username);
            stmt.setString(3, email);
            stmt.setString(4, "hash" + id);
            stmt.setBoolean(5, true);
            stmt.executeUpdate();
        }
    }

    /**
     * Helper method to create friendships.
     */
    private void insertFriendships() throws SQLException {
        insertFriendship(1, 2);
        insertFriendship(1, 3);
    }
    
    private void insertFriendship(int user1Id, int user2Id) throws SQLException {
        String sql = "INSERT INTO friendships (user1_id, user2_id) VALUES (?, ?)";
        try (Connection conn = testDataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, user1Id);
            stmt.setInt(2, user2Id);
            stmt.executeUpdate();
        }
    }

    /**
     * Helper method to insert test activities.
     */
    private void insertTestActivities() throws SQLException {
        insertFavorite(2, 101, 1);
        insertWatchlist(3, 102, 2);
        insertReview(2, 103, "Great movie! Really enjoyed watching it with friends.", 3);
    }
    
    private void insertFavorite(int userId, int movieId, int hoursAgo) throws SQLException {
        String sql = "INSERT INTO user_favorites (user_id, movie_id, created_at) VALUES (?, ?, ?)";
        try (Connection conn = testDataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, movieId);
            stmt.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now().minusHours(hoursAgo)));
            stmt.executeUpdate();
        }
    }
    
    private void insertWatchlist(int userId, int movieId, int hoursAgo) throws SQLException {
        String sql = "INSERT INTO user_watchlist (user_id, movie_id, created_at) VALUES (?, ?, ?)";
        try (Connection conn = testDataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, movieId);
            stmt.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now().minusHours(hoursAgo)));
            stmt.executeUpdate();
        }
    }
    
    private void insertReview(int userId, int movieId, String reviewText, int hoursAgo) throws SQLException {
        String sql = "INSERT INTO movie_reviews (user_id, movie_id, review_text, created_at) VALUES (?, ?, ?, ?)";
        try (Connection conn = testDataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, movieId);
            stmt.setString(3, reviewText);
            stmt.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now().minusHours(hoursAgo)));
            stmt.executeUpdate();
        }
    }

    /**
     * Test getFriendActivities with activities from friends.
     */
    @Test
    public void testGetFriendActivities_WithActivities() throws SQLException {
        insertTestUsers();
        insertFriendships();
        insertTestActivities();
        
        List<FriendActivity> activities = friendActivityDao.getFriendActivities(1, 10);
        
        assertNotNull("Activities list should not be null", activities);
        assertEquals("Should have 3 activities", 3, activities.size());
        assertTrue("Activities should be sorted by time desc",
                activities.get(0).getTimestamp().isAfter(activities.get(1).getTimestamp()));
        
        FriendActivity firstActivity = activities.get(0);
        assertEquals("Should be user2", 2, firstActivity.getUserId());
        assertEquals("Should be user2", "user2", firstActivity.getUsername());
        assertEquals("Should be added_to_favorites", "added_to_favorites", firstActivity.getActivityType());
        assertEquals("Should be movie 101", 101, firstActivity.getMovieId());
    }

    /**
     * Test getFriendActivities with no friends.
     */
    @Test
    public void testGetFriendActivities_NoFriends() throws SQLException {
        insertTestUsers();
        insertTestActivities();
        List<FriendActivity> activities = friendActivityDao.getFriendActivities(1, 10);
        
        assertNotNull("Activities list should not be null", activities);
        assertTrue("Should have no activities when no friends", activities.isEmpty());
    }

    /**
     * Test getFriendActivities with friends but no activities.
     */
    @Test
    public void testGetFriendActivities_NoActivities() throws SQLException {
        insertTestUsers();
        insertFriendships();
        List<FriendActivity> activities = friendActivityDao.getFriendActivities(1, 10);
        
        assertNotNull("Activities list should not be null", activities);
        assertTrue("Should have no activities when friends have no activities", activities.isEmpty());
    }


} 