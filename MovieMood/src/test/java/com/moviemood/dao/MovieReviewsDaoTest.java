package com.moviemood.dao;

import com.moviemood.bean.MovieReview;
import com.moviemood.util.TestDatabaseHelper;
import org.apache.commons.dbcp2.BasicDataSource;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;

import java.sql.SQLException;
import java.util.List;

/**
 * Tests for MovieReviewsDao using H2 in-memory database.
 */
public class MovieReviewsDaoTest {

    private BasicDataSource testDataSource;
    private MovieReviewsDao reviewsDao;

    @Before
    public void setUp() throws SQLException {
        testDataSource = TestDatabaseHelper.createTestDataSource();
        TestDatabaseHelper.validateTestDatabase(testDataSource);

        TestDatabaseHelper.createUsersTable(testDataSource);
        TestDatabaseHelper.createMovieReviewsTable(testDataSource);

        reviewsDao = new MovieReviewsDao(testDataSource);
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
    public void testAddMovieReview() throws SQLException {
        int userId = insertTestUser("testuser", "test@gmail.com", "031");

        MovieReview review = new MovieReview(0, userId, 550, "Great movie", null);
        reviewsDao.addMovieReview(review);

        List<MovieReview> reviews = reviewsDao.getMovieReviews(550);
        assertEquals(1, reviews.size());
        assertEquals("Great movie", reviews.get(0).getReviewText());
    }

    @Test
    public void testGetMovieReviews() throws SQLException {
        int userId1 = insertTestUser("shalva", "shalva@gmail.com", "haha");
        int userId2 = insertTestUser("emzari", "emzari@emzari.com", "123");

        MovieReview review1 = new MovieReview(0, userId1, 550, "kaia", null);
        MovieReview review2 = new MovieReview(0, userId2, 550, "cudia", null);
        MovieReview review3 = new MovieReview(0, userId1, 600, "ise ra", null);
        reviewsDao.addMovieReview(review1);
        reviewsDao.addMovieReview(review2);
        reviewsDao.addMovieReview(review3);

        List<MovieReview> movieReviews = reviewsDao.getMovieReviews(550);
        assertEquals(2, movieReviews.size());
        List<MovieReview> otherMovieReviews = reviewsDao.getMovieReviews(600);
        assertEquals(1, otherMovieReviews.size());
    }

    @Test
    public void testGetUserMovieReviews() throws SQLException {
        int userId1 = insertTestUser("shalva", "shalva@gmail.com", "bla");
        int userId2 = insertTestUser("emzari", "emzari@emzari.com", "123");

        MovieReview review1 = new MovieReview(0, userId1, 550, "kaia", null);
        MovieReview review2 = new MovieReview(0, userId2, 550, "cudia", null);
        MovieReview review3 = new MovieReview(0, userId1, 600, "ise ra", null);

        reviewsDao.addMovieReview(review1);
        reviewsDao.addMovieReview(review2);
        reviewsDao.addMovieReview(review3);

        List<MovieReview> user1Reviews = reviewsDao.getUserMovieReviews(userId1);
        List<MovieReview> user2Reviews = reviewsDao.getUserMovieReviews(userId2);

        assertEquals(2, user1Reviews.size());
        assertEquals(1, user2Reviews.size());
    }

    @Test
    public void testGetMovieReview() throws SQLException {
        int userId = insertTestUser("shalva", "shalva@gmail.com", "bla");

        MovieReview review = new MovieReview(0, userId, 550, "kaia", null);
        reviewsDao.addMovieReview(review);

        List<MovieReview> reviews = reviewsDao.getMovieReviews(550);
        int reviewId = reviews.get(0).getId();

        MovieReview retrievedReview = reviewsDao.getMovieReview(reviewId);
        assertNotNull(retrievedReview);
        assertEquals("kaia", retrievedReview.getReviewText());
        assertEquals(userId, retrievedReview.getUserId());
        assertEquals(550, retrievedReview.getMovieId());
    }

    @Test
    public void testGetMovieReview_NotFound() throws SQLException {
        MovieReview review = reviewsDao.getMovieReview(99999);
        assertNull(review);
    }

    @Test
    public void testDeleteMovieReview() throws SQLException {
        int userId = insertTestUser("shalva", "shalva@gmail.com", "bla");

        MovieReview review1 = new MovieReview(0, userId, 550, "good", null);
        MovieReview review2 = new MovieReview(0, userId, 550, "not good", null);

        reviewsDao.addMovieReview(review1);
        reviewsDao.addMovieReview(review2);
        List<MovieReview> reviews = reviewsDao.getMovieReviews(550);
        assertEquals(2, reviews.size());

        int reviewId = reviews.get(0).getId();
        reviewsDao.deleteMovieReview(reviewId);

        List<MovieReview> remainingReviews = reviewsDao.getMovieReviews(550);
        assertEquals(1, remainingReviews.size());
    }

    @Test
    public void testGetUserReviewCount() throws SQLException {
        int userId1 = insertTestUser("gaga", "gaga@gmail.com", "blu");
        int userId2 = insertTestUser("chika", "chika@chika", "chika");

        MovieReview review1 = new MovieReview(0, userId1, 550, "", null);
        MovieReview review2 = new MovieReview(0, userId1, 600, "f", null);
        MovieReview review3 = new MovieReview(0, userId2, 550, "a", null);

        reviewsDao.addMovieReview(review1);
        reviewsDao.addMovieReview(review2);
        reviewsDao.addMovieReview(review3);
        int user1Count = reviewsDao.getUserReviewCount(userId1);
        int user2Count = reviewsDao.getUserReviewCount(userId2);

        assertEquals(2, user1Count);
        assertEquals(1, user2Count);
    }

    @Test
    public void testGetUserReviewCount_NoReviews() throws SQLException {
        int userId = insertTestUser("testuser", "test@gmail.com", "1");

        int count = reviewsDao.getUserReviewCount(userId);
        assertEquals(0, count);
    }

    @Test
    public void testGetMovieReviews_EmptyList() throws SQLException {
        List<MovieReview> reviews = reviewsDao.getMovieReviews(99999);

        assertNotNull(reviews);
        assertEquals(0, reviews.size());
        assertTrue(reviews.isEmpty());
    }

}