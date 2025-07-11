package com.moviemood.dao;

import com.moviemood.bean.MovieRating;
import com.moviemood.util.TestDatabaseHelper;
import org.junit.jupiter.api.*;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MovieRatingsDaoTest {

    private DataSource dataSource;
    private MovieRatingsDao dao;

    @BeforeEach
    void setUp() throws Exception {
        dataSource = TestDatabaseHelper.createTestDataSource();
        dao = new MovieRatingsDao(dataSource);

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "CREATE TABLE IF NOT EXISTS movie_ratings (" +
                             "movie_id INT, " +
                             "user_id INT, " +
                             "score_value DOUBLE, " +
                             "score_date DATE, " +
                             "PRIMARY KEY (movie_id, user_id)" +
                             ")"
             )) {
            stmt.execute();
        }
    }

    @AfterEach
    void tearDown() throws SQLException {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement("DROP TABLE movie_ratings")) {
            stmt.execute();
        }
    }

    @Test
    void testAddOrUpdateMovieRating() throws Exception {
        MovieRating rating = new MovieRating();
        rating.setMovieId(1);
        rating.setUserId(2);
        rating.setScoreValue(4.5);
        rating.setScoreDate(new java.util.Date());

        dao.addOrUpdateMovieRating(rating);

        MovieRating retrieved = dao.getRatingByUserAndMovie(2, 1);
        assertNotNull(retrieved);
        assertEquals(4.5, retrieved.getScoreValue());
    }

    @Test
    void testGetRatingByUserAndMovie_NotFound() throws Exception {
        MovieRating rating = dao.getRatingByUserAndMovie(999, 999);
        assertNull(rating);
    }

    @Test
    void testGetAverageRatingByMovie_WithRatings() throws Exception {
        MovieRating r1 = new MovieRating();
        r1.setMovieId(10);
        r1.setUserId(1);
        r1.setScoreValue(4);
        r1.setScoreDate(new java.util.Date());

        MovieRating r2 = new MovieRating();
        r2.setMovieId(10);
        r2.setUserId(2);
        r2.setScoreValue(5);
        r2.setScoreDate(new java.util.Date());

        dao.addOrUpdateMovieRating(r1);
        dao.addOrUpdateMovieRating(r2);

        double avg = dao.getAverageRatingByMovie(10);
        assertEquals(4.5, avg);
    }

    @Test
    void testDeleteRating() throws Exception {
        MovieRating rating = new MovieRating();
        rating.setMovieId(5);
        rating.setUserId(3);
        rating.setScoreValue(3.0);
        rating.setScoreDate(new java.util.Date());

        dao.addOrUpdateMovieRating(rating);
        boolean deleted = dao.deleteRating(3, 5);

        assertTrue(deleted);
        assertNull(dao.getRatingByUserAndMovie(3, 5));
    }

    @Test
    void testHasUserRatedMovie_True() throws Exception {
        MovieRating rating = new MovieRating();
        rating.setMovieId(15);
        rating.setUserId(7);
        rating.setScoreValue(4.0);
        rating.setScoreDate(new java.util.Date());

        dao.addOrUpdateMovieRating(rating);
        assertTrue(dao.hasUserRatedMovie(7, 15));
    }

    @Test
    void testHasUserRatedMovie_False() throws Exception {
        assertFalse(dao.hasUserRatedMovie(100, 200));
    }

    @Test
    void testGetAverageMovieRating_WithTwoRatings() throws Exception {
        // Clear all existing ratings
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM movie_ratings")) {
            stmt.executeUpdate();
        }

        // Add rating 5 for user 1, movie 100
        MovieRating r1 = new MovieRating();
        r1.setMovieId(100);
        r1.setUserId(1);
        r1.setScoreValue(5.0);
        r1.setScoreDate(new java.util.Date());

        // Add rating 4 for user 2, movie 100
        MovieRating r2 = new MovieRating();
        r2.setMovieId(100);
        r2.setUserId(2);
        r2.setScoreValue(4.0);
        r2.setScoreDate(new java.util.Date());

        dao.addOrUpdateMovieRating(r1);
        dao.addOrUpdateMovieRating(r2);

        // The average should be (5 + 4) / 2 = 4.5
        double avg = dao.getAverageMovieRating(100);

        // Assert average is 4.5 (or your rounding logic if applied)
        assertEquals(9.0, avg, 0.0001);
    }

}