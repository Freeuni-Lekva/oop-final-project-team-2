package com.moviemood.dao;

import com.moviemood.bean.UserList;
import com.moviemood.util.TestDatabaseHelper;
import org.apache.commons.dbcp2.BasicDataSource;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class UserListDaoTest {

    private BasicDataSource testDataSource;
    private UserListDao userListDao;

    @Before
    public void setUp() throws SQLException {
        testDataSource = TestDatabaseHelper.createTestDataSource();
        TestDatabaseHelper.validateTestDatabase(testDataSource);

        TestDatabaseHelper.createUsersTable(testDataSource);
        TestDatabaseHelper.createUserListsTable(testDataSource);
        TestDatabaseHelper.createUserListItemsTable(testDataSource);

        userListDao = new UserListDao(testDataSource);

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

    private int insertListDirectly(int userId, String name, String description, boolean isPublic) throws SQLException {
        String sql = "INSERT INTO user_lists (user_id, name, description, is_public) VALUES (?, ?, ?, ?)";
        try (Connection conn = testDataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, userId);
            stmt.setString(2, name);
            stmt.setString(3, description);
            stmt.setBoolean(4, isPublic);
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return -1;
    }

    private void addMovieToListDirectly(int listId, int movieId) throws SQLException {
        String sql = "INSERT INTO user_list_items (list_id, movie_id) VALUES (?, ?)";
        try (Connection conn = testDataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, listId);
            stmt.setInt(2, movieId);
            stmt.executeUpdate();
        }
    }

    // Tests for list management
    @Test
    public void testCreateList_Success() {
        UserList newList = new UserList(1, "My Favorites", "Movies I love", true);

        UserList createdList = userListDao.createList(newList);

        assertNotNull("Created list should not be null", createdList);
        assertTrue("List ID should be assigned", createdList.getId() > 0);
        assertEquals("User ID should match", 1, createdList.getUserId());
        assertEquals("Name should match", "My Favorites", createdList.getName());
        assertEquals("Description should match", "Movies I love", createdList.getDescription());
        assertTrue("Should be public", createdList.isPublic());
    }

    @Test
    public void testCreateList_PrivateList() {
        UserList newList = new UserList(1, "Private List", "My secret movies", false);

        UserList createdList = userListDao.createList(newList);

        assertNotNull("Created list should not be null", createdList);
        assertFalse("Should be private", createdList.isPublic());
    }











    @Test
    public void testGetListById_NotFound() {
        UserList foundList = userListDao.getListById(999);

        assertNull("Should return null for non-existent list", foundList);
    }


    @Test
    public void testDeleteList_Success() throws SQLException {
        int listId = insertListDirectly(1, "To Delete", "This will be deleted", true);
        addMovieToListDirectly(listId, 100);
        addMovieToListDirectly(listId, 200);

        boolean result = userListDao.deleteList(listId);

        assertTrue("Delete should succeed", result);
        assertNull("List should no longer exist", userListDao.getListById(listId));
        assertEquals("List items should be deleted (cascading)", 0, userListDao.getListMovies(listId).size());
    }

    @Test
    public void testDeleteList_NonExistent() {
        boolean result = userListDao.deleteList(999);

        assertFalse("Should return false for non-existent list", result);
    }

    // Tests for list item management
    @Test
    public void testAddMovieToList_Success() throws SQLException {
        int listId = insertListDirectly(1, "My List", "Test list", true);

        boolean result = userListDao.addMovieToList(listId, 100);

        assertTrue("Should successfully add movie to list", result);
        assertTrue("Movie should be in list", userListDao.isMovieInList(listId, 100));
        assertEquals("List should have 1 movie", 1, userListDao.getListMovieCount(listId));
    }




    @Test
    public void testRemoveMovieFromList_Success() throws SQLException {
        int listId = insertListDirectly(1, "My List", "Test list", true);
        addMovieToListDirectly(listId, 100);

        boolean result = userListDao.removeMovieFromList(listId, 100);

        assertTrue("Should successfully remove movie from list", result);
        assertFalse("Movie should not be in list", userListDao.isMovieInList(listId, 100));
        assertEquals("List should have 0 movies", 0, userListDao.getListMovieCount(listId));
    }

    @Test
    public void testRemoveMovieFromList_NotFound() throws SQLException {
        int listId = insertListDirectly(1, "My List", "Test list", true);

        boolean result = userListDao.removeMovieFromList(listId, 999);

        assertFalse("Should return false when movie not in list", result);
    }

    @Test
    public void testGetListMovies_Empty() throws SQLException {
        int listId = insertListDirectly(1, "Empty List", "No movies", true);

        List<Integer> movies = userListDao.getListMovies(listId);

        assertNotNull("Movies list should not be null", movies);
        assertEquals("Movies list should be empty", 0, movies.size());
    }

    @Test
    public void testGetListMovies_Multiple() throws SQLException {
        int listId = insertListDirectly(1, "Movie List", "Multiple movies", true);
        addMovieToListDirectly(listId, 100);
        addMovieToListDirectly(listId, 200);
        addMovieToListDirectly(listId, 300);

        List<Integer> movies = userListDao.getListMovies(listId);

        assertNotNull("Movies list should not be null", movies);
        assertEquals("Should have 3 movies", 3, movies.size());
        assertTrue("Should contain movie 100", movies.contains(100));
        assertTrue("Should contain movie 200", movies.contains(200));
        assertTrue("Should contain movie 300", movies.contains(300));
    }

    @Test
    public void testIsMovieInList_True() throws SQLException {
        int listId = insertListDirectly(1, "My List", "Test list", true);
        addMovieToListDirectly(listId, 100);

        assertTrue("Movie should be in list", userListDao.isMovieInList(listId, 100));
    }

    @Test
    public void testIsMovieInList_False() throws SQLException {
        int listId = insertListDirectly(1, "My List", "Test list", true);

        assertFalse("Movie should not be in list", userListDao.isMovieInList(listId, 999));
    }

    @Test
    public void testGetListsCount() throws SQLException {
        insertListDirectly(1, "List 1", "First list", true);
        insertListDirectly(1, "List 2", "Second list", false);
        insertListDirectly(2, "Bob's List", "Bob's list", true);

        assertEquals("User 1 should have 2 lists", 2, userListDao.getListsCount(1));
        assertEquals("User 2 should have 1 list", 1, userListDao.getListsCount(2));
    }

    @Test
    public void testGetListMovieCount() throws SQLException {
        int listId = insertListDirectly(1, "Movie List", "Test list", true);
        addMovieToListDirectly(listId, 100);
        addMovieToListDirectly(listId, 200);

        assertEquals("List should have 2 movies", 2, userListDao.getListMovieCount(listId));
    }

    @Test
    public void testCreateList_NullList() {
        UserList nullList = null;
        try {
            userListDao.createList(nullList);
            fail("Should throw exception for null list");
        } catch (Exception e) {
            // Expected
        }
    }

    @Test
    public void testGetListsCount_ZeroCount() {
        assertEquals("User with no lists should have count 0", 0, userListDao.getListsCount(1));
    }

    @Test
    public void testGetListMovieCount_EmptyList() throws SQLException {
        int listId = insertListDirectly(1, "Empty List", "No movies", true);
        assertEquals("Empty list should have count 0", 0, userListDao.getListMovieCount(listId));
    }


}