package com.moviemood.servlets;

import com.moviemood.bean.MovieReview;
import com.moviemood.dao.MovieReviewsDao;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/add-review")
public class AddReviewServlet extends HttpServlet {

    private MovieReviewsDao reviewsDao;

    @Override
    public void init() throws ServletException {
        // Get the DAO from the ServletContext (injected by a ContextListener)
        reviewsDao = (MovieReviewsDao) getServletContext().getAttribute("reviewsDao");
        if (reviewsDao == null) {
            throw new IllegalStateException("MovieReviewsDao not initialized in ServletContext");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Get review data from request
        String reviewText = request.getParameter("reviewText");
        int userId = Integer.parseInt(request.getParameter("userId"));
        int movieId = Integer.parseInt(request.getParameter("movieId"));

        // Create and populate review object
        MovieReview review = new MovieReview();
        review.setUserId(userId);
        review.setMovieId(movieId);
        review.setReviewText(reviewText);

        // Save the review using DAO
        try {
            reviewsDao.addMovieReview(review);
        } catch (SQLException e) {
            throw new ServletException("Error saving movie review", e);
        }

        // Redirect back to previous page
        String referer = request.getHeader("Referer");
        response.sendRedirect(referer != null ? referer : "home.jsp");
    }
}
