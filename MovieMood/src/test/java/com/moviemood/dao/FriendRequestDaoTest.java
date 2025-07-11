package com.moviemood.dao;

import com.moviemood.bean.FriendRequest;
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
 * Tests for FriendRequestDao using H2 in-memory database.
 * Focuses on essential functionality without 100% coverage.
 */
public class FriendRequestDaoTest {

    private BasicDataSource testDataSource;
    private FriendRequestDao friendRequestDao;

    @Before
    public void setUp() throws SQLException {
        testDataSource = TestDatabaseHelper.createTestDataSource();
        TestDatabaseHelper.validateTestDatabase(testDataSource);

        TestDatabaseHelper.createUsersTable(testDataSource);
        TestDatabaseHelper.createFriendRequestsTable(testDataSource);
        friendRequestDao = new FriendRequestDao(testDataSource);
        
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
        }
    }

    @Test
    public void testSendRequest() throws SQLException {
        friendRequestDao.sendRequest(1, 2);
        
        List<FriendRequest> incomingRequests = friendRequestDao.getIncomingRequests(2);
        assertEquals("Should have 1 incoming request", 1, incomingRequests.size());
        
        FriendRequest request = incomingRequests.get(0);
        assertEquals("Sender should be user 1", 1, request.getSenderId());
        assertEquals("Receiver should be user 2", 2, request.getReceiverId());
        assertEquals("Status should be pending", "pending", request.getStatus());
        assertEquals("Sender username should be alice", "alice", request.getSenderUsername());
    }

    @Test
    public void testGetIncomingRequests() throws SQLException {
        friendRequestDao.sendRequest(1, 2);
        friendRequestDao.sendRequest(3, 2);
        
        List<FriendRequest> incomingRequests = friendRequestDao.getIncomingRequests(2);
        
        assertEquals("Should have 2 incoming requests", 2, incomingRequests.size());
        
        for (FriendRequest request : incomingRequests) {
            assertEquals("All requests should be for user 2", 2, request.getReceiverId());
            assertEquals("All requests should be pending", "pending", request.getStatus());
            assertNotNull("Sender username should be set", request.getSenderUsername());
        }
    }

    @Test
    public void testGetSentRequests() throws SQLException {
        friendRequestDao.sendRequest(1, 2);
        friendRequestDao.sendRequest(1, 3);
        
        List<FriendRequest> sentRequests = friendRequestDao.getSentRequests(1);
        
        assertEquals("Should have 2 sent requests", 2, sentRequests.size());
        
        for (FriendRequest request : sentRequests) {
            assertEquals("All requests should be from user 1", 1, request.getSenderId());
            assertEquals("All requests should be pending", "pending", request.getStatus());
            assertNotNull("Receiver username should be set", request.getReceiverUsername());
        }
    }

    @Test
    public void testUpdateRequestStatus() throws SQLException {
        friendRequestDao.sendRequest(1, 2);
        
        List<FriendRequest> requests = friendRequestDao.getIncomingRequests(2);
        int requestId = requests.get(0).getRequestId();
        friendRequestDao.updateRequestStatus(requestId, "accepted");
        
        List<FriendRequest> pendingRequests = friendRequestDao.getIncomingRequests(2);
        assertEquals("Should have no pending requests after accepting", 0, pendingRequests.size());
    }

    @Test
    public void testDeleteRequest() throws SQLException {
        friendRequestDao.sendRequest(1, 2);
        
        List<FriendRequest> requests = friendRequestDao.getIncomingRequests(2);
        int requestId = requests.get(0).getRequestId();
        
        friendRequestDao.deleteRequest(requestId);
        List<FriendRequest> remainingRequests = friendRequestDao.getIncomingRequests(2);
        assertEquals("Should have no requests after deletion", 0, remainingRequests.size());
    }

    @Test
    public void testCancelSentRequest() throws SQLException {
        friendRequestDao.sendRequest(1, 2); // alice -> bob
        boolean cancelled = friendRequestDao.cancelSentRequest(1, "bob");
        
        assertTrue("Request should be successfully cancelled", cancelled);
        
        List<FriendRequest> incomingRequests = friendRequestDao.getIncomingRequests(2);
        List<FriendRequest> sentRequests = friendRequestDao.getSentRequests(1);
        assertEquals("Should have no pending incoming requests", 0, incomingRequests.size());
        assertEquals("Should have no pending sent requests", 0, sentRequests.size());
    }

} 