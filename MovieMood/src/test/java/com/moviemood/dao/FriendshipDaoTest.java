package com.moviemood.dao;

import com.moviemood.bean.User;
import com.moviemood.bean.FriendSuggestion;
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
import java.util.Set;

public class FriendshipDaoTest {

    private BasicDataSource testDataSource;
    private FriendshipDao friendshipDao;

    @Before
    public void setUp() throws SQLException {
        testDataSource = TestDatabaseHelper.createTestDataSource();
        TestDatabaseHelper.validateTestDatabase(testDataSource);

        TestDatabaseHelper.createUsersTable(testDataSource);
        TestDatabaseHelper.createFriendshipsTable(testDataSource);
        TestDatabaseHelper.createFriendRequestsTable(testDataSource);
        
        friendshipDao = new FriendshipDao(testDataSource);
        
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
            
            stmt.setInt(1, 3);
            stmt.setString(2, "charlie");
            stmt.setString(3, "charlie@test.com");
            stmt.setString(4, "hash3");
            stmt.setBoolean(5, true);
            stmt.executeUpdate();
            
            stmt.setInt(1, 4);
            stmt.setString(2, "diana");
            stmt.setString(3, "diana@test.com");
            stmt.setString(4, "hash4");
            stmt.setBoolean(5, true);
            stmt.executeUpdate();
        }
    }

    @Test
    public void testCreateFriendship() {
        friendshipDao.createFriendship(1, 2);
        
        List<User> aliceFriends = friendshipDao.getFriendsByUserId(1);
        List<User> bobFriends = friendshipDao.getFriendsByUserId(2);
        
        assertEquals("Alice should have 1 friend", 1, aliceFriends.size());
        assertEquals("Bob should have 1 friend", 1, bobFriends.size());
        assertEquals("Alice's friend should be bob", "bob", aliceFriends.get(0).getUsername());
        assertEquals("Bob's friend should be alice", "alice", bobFriends.get(0).getUsername());
    }

    @Test
    public void testCreateFriendshipOrderDoesNotMatter() {
        friendshipDao.createFriendship(2, 1);
        
        List<User> aliceFriends = friendshipDao.getFriendsByUserId(1);
        List<User> bobFriends = friendshipDao.getFriendsByUserId(2);
        
        assertEquals("Alice should have 1 friend", 1, aliceFriends.size());
        assertEquals("Bob should have 1 friend", 1, bobFriends.size());
    }

    @Test
    public void testDeleteFriendship() {
        friendshipDao.createFriendship(1, 2);
        friendshipDao.deleteFriendship(1, 2);

        List<User> aliceFriends = friendshipDao.getFriendsByUserId(1);
        List<User> bobFriends = friendshipDao.getFriendsByUserId(2);
        
        assertEquals("Alice should have no friends", 0, aliceFriends.size());
        assertEquals("Bob should have no friends", 0, bobFriends.size());
    }

    @Test
    public void testGetFriendsByUserId() {
        friendshipDao.createFriendship(1, 2);
        friendshipDao.createFriendship(1, 3);
        
        List<User> aliceFriends = friendshipDao.getFriendsByUserId(1);
        
        assertEquals("Alice should have 2 friends", 2, aliceFriends.size());
        
        for (User friend : aliceFriends) {
            assertNotNull("Friend username should not be null", friend.getUsername());
            assertNotNull("Friend email should not be null", friend.getEmail());
            assertTrue("Friend ID should be valid", friend.getId() > 0);
        }
    }

    @Test
    public void testGetFriendSuggestions() throws SQLException {

        friendshipDao.createFriendship(1, 2);
        friendshipDao.createFriendship(2, 3);
        friendshipDao.createFriendship(1, 4);
        
        List<FriendSuggestion> suggestions = friendshipDao.getFriendSuggestions(1, 5);
        
        assertEquals("Alice should have 1 suggestion", 1, suggestions.size());
        
        FriendSuggestion suggestion = suggestions.get(0);
        assertEquals("Suggestion should be charlie", "charlie", suggestion.getUser().getUsername());
        assertEquals("Should have 1 mutual friend", 1, suggestion.getMutualFriendCount());
    }

    @Test
    public void testGetExcludedUserIds() throws SQLException {
        String sql = "INSERT INTO friend_requests (sender_id, receiver_id, status) VALUES (?, ?, ?)";
        try (Connection conn = testDataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, 1);
            stmt.setInt(2, 3);
            stmt.setString(3, "pending");
            stmt.executeUpdate();
        }
        
        Set<Integer> excludedIds = friendshipDao.getExcludedUserIds(1);
        
        assertTrue("Charlie should be excluded due to pending request", excludedIds.contains(3));
    }

    @Test
    public void testNoFriendsReturnsEmptyList() {
        List<User> friends = friendshipDao.getFriendsByUserId(1);
        
        assertNotNull("Friends list should not be null", friends);
        assertEquals("Friends list should be empty", 0, friends.size());
    }

    @Test
    public void testNoSuggestionsReturnsEmptyList() {
        List<FriendSuggestion> suggestions = friendshipDao.getFriendSuggestions(1, 5);
        
        assertNotNull("Suggestions list should not be null", suggestions);
        assertEquals("Suggestions list should be empty", 0, suggestions.size());
    }
} 