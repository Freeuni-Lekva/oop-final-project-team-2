package com.moviemood.dao;

import com.moviemood.util.TestDatabaseHelper;
import org.apache.commons.dbcp2.BasicDataSource;
import org.junit.*;
import org.mockito.Mockito;

import java.sql.*;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class UserWatchlistDaoTest {

    private BasicDataSource testDataSource;
    private UserWatchlistDao userWatchlistDao;



    @Before
    public void setUp() throws SQLException {
        testDataSource = TestDatabaseHelper.createTestDataSource();
        TestDatabaseHelper.validateTestDatabase(testDataSource);
        TestDatabaseHelper.createUsersTable(testDataSource);
        TestDatabaseHelper.createUserWatchlistTable(testDataSource);

        userWatchlistDao = new UserWatchlistDao(testDataSource);
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

            stmt.setInt(1, 1); stmt.setString(2, "alice"); stmt.setString(3, "alice@test.com");
            stmt.setString(4, "hash1"); stmt.setBoolean(5, true); stmt.executeUpdate();

            stmt.setInt(1, 2); stmt.setString(2, "bob"); stmt.setString(3, "bob@test.com");
            stmt.setString(4, "hash2"); stmt.setBoolean(5, true); stmt.executeUpdate();
        }
    }

    private void addToWatchlistDirectly(int userId, int movieId) throws SQLException {
        String sql = "INSERT INTO user_watchlist (user_id, movie_id) VALUES (?, ?)";
        try (Connection conn = testDataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId); stmt.setInt(2, movieId); stmt.executeUpdate();
        }
    }

    @Test
    public void testGetUserWatchList_Empty() {
        List<Integer> watchlist = userWatchlistDao.getUserWatchList(1);
        assertNotNull(watchlist);
        assertEquals(0, watchlist.size());
    }

    @Test
    public void testGetUserWatchList_WithMovies() throws SQLException {
        addToWatchlistDirectly(1, 100);
        addToWatchlistDirectly(1, 200);
        List<Integer> watchlist = userWatchlistDao.getUserWatchList(1);
        assertEquals(2, watchlist.size());
        assertTrue(watchlist.contains(100));
        assertTrue(watchlist.contains(200));
    }



    @Test
    public void testRemoveMovieFromWatchList_Success() throws SQLException {
        addToWatchlistDirectly(1, 100);
        boolean result = userWatchlistDao.removeMovieFromWatchList(1, 100);
        assertTrue(result);
        assertFalse(userWatchlistDao.isMovieInWatchlist(1, 100));
        assertEquals(0, userWatchlistDao.getWatchlistCount(1));
    }

    @Test
    public void testRemoveMovieFromWatchList_NotFound() {
        boolean result = userWatchlistDao.removeMovieFromWatchList(1, 999);
        assertFalse(result);
    }

    @Test
    public void testIsMovieInWatchlist_True() throws SQLException {
        addToWatchlistDirectly(1, 100);
        assertTrue(userWatchlistDao.isMovieInWatchlist(1, 100));
    }

    @Test
    public void testIsMovieInWatchlist_False() {
        assertFalse(userWatchlistDao.isMovieInWatchlist(1, 999));
    }

    @Test
    public void testGetWatchlistCount_Zero() {
        assertEquals(0, userWatchlistDao.getWatchlistCount(1));
    }

    @Test
    public void testGetWatchlistCount_Multiple() throws SQLException {
        addToWatchlistDirectly(1, 100);
        addToWatchlistDirectly(1, 200);
        addToWatchlistDirectly(1, 300);
        assertEquals(3, userWatchlistDao.getWatchlistCount(1));
    }

    @Test
    public void testComplexScenario() throws SQLException {
        userWatchlistDao.addMovieToWatchList(1, 100);
        userWatchlistDao.addMovieToWatchList(1, 200);
        userWatchlistDao.addMovieToWatchList(1, 300);
        assertEquals(3, userWatchlistDao.getWatchlistCount(1));

        userWatchlistDao.removeMovieFromWatchList(1, 200);
        assertEquals(2, userWatchlistDao.getWatchlistCount(1));
        assertFalse(userWatchlistDao.isMovieInWatchlist(1, 200));
        assertTrue(userWatchlistDao.isMovieInWatchlist(1, 100));
        assertTrue(userWatchlistDao.isMovieInWatchlist(1, 300));
    }



    @Test(expected = RuntimeException.class)
    public void testGetUserWatchList_throwsException() throws Exception {
        BasicDataSource mockDS = mock(BasicDataSource.class);
        when(mockDS.getConnection()).thenThrow(new SQLException("fail"));
        new UserWatchlistDao(mockDS).getUserWatchList(1);
    }

    @Test
    public void testAddMovieToWatchList_duplicateEntryReturnsFalse() throws Exception {
        BasicDataSource mockDS = mock(BasicDataSource.class);
        Connection mockConn = mock(Connection.class);
        PreparedStatement mockStmt = mock(PreparedStatement.class);
        when(mockDS.getConnection()).thenReturn(mockConn);
        when(mockConn.prepareStatement(anyString())).thenReturn(mockStmt);

        SQLException duplicate = new SQLException("Duplicate entry", "23000", 1062);
        when(mockStmt.executeUpdate()).thenThrow(duplicate);

        UserWatchlistDao dao = new UserWatchlistDao(mockDS);
        boolean result = dao.addMovieToWatchList(1, 101);
        assertFalse(result);
    }

    @Test(expected = RuntimeException.class)
    public void testAddMovieToWatchList_sqlErrorThrows() throws Exception {
        BasicDataSource mockDS = mock(BasicDataSource.class);
        Connection mockConn = mock(Connection.class);
        PreparedStatement mockStmt = mock(PreparedStatement.class);
        when(mockDS.getConnection()).thenReturn(mockConn);
        when(mockConn.prepareStatement(anyString())).thenReturn(mockStmt);

        SQLException otherError = new SQLException("Some other error");
        when(mockStmt.executeUpdate()).thenThrow(otherError);

        UserWatchlistDao dao = new UserWatchlistDao(mockDS);
        dao.addMovieToWatchList(1, 101);
    }



    @Test
    public void testGetWatchlistCount_sqlErrorReturnsZero() throws Exception {
        BasicDataSource mockDS = mock(BasicDataSource.class);
        when(mockDS.getConnection()).thenThrow(new SQLException("Count failed"));

        UserWatchlistDao dao = new UserWatchlistDao(mockDS);
        assertEquals(0, dao.getWatchlistCount(1));
    }
}
