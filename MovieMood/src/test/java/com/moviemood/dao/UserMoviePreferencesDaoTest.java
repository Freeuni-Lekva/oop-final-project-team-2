package com.moviemood.dao;

import com.moviemood.util.TestDatabaseHelper;
import org.apache.commons.dbcp2.BasicDataSource;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;

import java.sql.SQLException;
import java.util.List;

/**
 * Tests for UserMoviePreferencesDao using H2 in-memory database.
 */
public class UserMoviePreferencesDaoTest {

    private BasicDataSource testDataSource;
    private UserMoviePreferencesDao preferencesDao;

    @Before
    public void setUp() throws SQLException {
        testDataSource = TestDatabaseHelper.createTestDataSource();
        TestDatabaseHelper.validateTestDatabase(testDataSource);

        TestDatabaseHelper.createUsersTable(testDataSource);
        TestDatabaseHelper.createUserMoviePreferencesTable(testDataSource);

        preferencesDao = new UserMoviePreferencesDao(testDataSource);
    }

    @After
    public void tearDown() throws SQLException {
        if (testDataSource != null) {
            TestDatabaseHelper.dropAllTables(testDataSource);
            testDataSource.close();
        }
    }

    private int insertTestUser(String username, String email, String passwordHash) throws SQLException {
        String sql = "INSERT INTO users (username, email, password_hash, is_verified) VALUES (?, ?, ?, ?)";
        try (var conn = testDataSource.getConnection();
             var stmt = conn.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, username);
            stmt.setString(2, email);
            stmt.setString(3, passwordHash);
            stmt.setBoolean(4, false);
            stmt.executeUpdate();

            try (var rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
                throw new SQLException("Failed to get generated user ID");
            }
        }
    }

    @Test
    public void testSaveUserPreference1() throws SQLException {
        int userId = insertTestUser("testuser", "test@gmail.com", "a");

        preferencesDao.saveUserPreference(userId, "testuser", 100);

        List<Integer> preferences = preferencesDao.getUserPreferences(userId);
        assertEquals(1, preferences.size());
        assertEquals(Integer.valueOf(100), preferences.get(0));
    }

    @Test
    public void testSaveUserPreference2_Multiple() throws SQLException {
        int userId = insertTestUser("gaga", "gaga@gmail.com", "haha");

        preferencesDao.saveUserPreference(userId, "gaga", 100);
        preferencesDao.saveUserPreference(userId, "gaga", 200);
        preferencesDao.saveUserPreference(userId, "gaga", 300);

        List<Integer> preferences = preferencesDao.getUserPreferences(userId);
        assertEquals(3, preferences.size());
        assertTrue(preferences.contains(100));
        assertTrue(preferences.contains(200));
        assertTrue(preferences.contains(300));
    }

    @Test
    public void testSaveUserPreference3_Duplicate() throws SQLException {
        int userId = insertTestUser("levani", "levani@levani", "o");

        preferencesDao.saveUserPreference(userId, "levani", 100);
        preferencesDao.saveUserPreference(userId, "levani", 100);

        List<Integer> preferences = preferencesDao.getUserPreferences(userId);
        assertEquals(1, preferences.size());
    }

    @Test
    public void testGetUserPreferences_Empty() throws SQLException {
        List<Integer> preferences = preferencesDao.getUserPreferences(999);

        assertNotNull(preferences);
        assertEquals(0, preferences.size());
        assertTrue(preferences.isEmpty());
    }

    @Test
    public void testDeleteUserPreference1() throws SQLException {
        int userId = insertTestUser("chika", "chika@chika", "chika");

        preferencesDao.saveUserPreference(userId, "chika", 100);
        preferencesDao.saveUserPreference(userId, "chika", 200);

        preferencesDao.deleteUserPreference(userId, 100);

        List<Integer> preferences = preferencesDao.getUserPreferences(userId);
        assertEquals(1, preferences.size());
        assertEquals(Integer.valueOf(200), preferences.get(0));
    }

    @Test
    public void testDeleteUserPreference2_NonExistent() throws SQLException {
        int userId = insertTestUser("bla", "kai@kai", "hashedpassword");

        preferencesDao.saveUserPreference(userId, "bla", 100);
        preferencesDao.deleteUserPreference(userId, 999);
        List<Integer> preferences = preferencesDao.getUserPreferences(userId);
        assertEquals(1, preferences.size());
        assertEquals(Integer.valueOf(100), preferences.get(0));
    }

    @Test
    public void testMultipleUsers() throws SQLException {
        int user1Id = insertTestUser("user1", "user1@gmail.com", "paroli1");
        int user2Id = insertTestUser("user2", "user2@gmail.com", "paroli2");

        preferencesDao.saveUserPreference(user1Id, "user1", 100);
        preferencesDao.saveUserPreference(user1Id, "user1", 200);
        preferencesDao.saveUserPreference(user2Id, "user2", 300);
        preferencesDao.saveUserPreference(user2Id, "user2", 400);

        List<Integer> user1Prefs = preferencesDao.getUserPreferences(user1Id);
        List<Integer> user2Prefs = preferencesDao.getUserPreferences(user2Id);

        assertEquals(2, user1Prefs.size());
        assertEquals(2, user2Prefs.size());

        assertTrue(user1Prefs.contains(100));
        assertTrue(user1Prefs.contains(200));
        assertTrue(user2Prefs.contains(300));
        assertTrue(user2Prefs.contains(400));
    }

}

