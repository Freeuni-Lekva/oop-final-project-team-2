package com.moviemood.servlets;

import com.moviemood.bean.MovieRating;
import com.moviemood.bean.User;
import com.moviemood.dao.MovieRatingsDao;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;

@WebServlet("/rating")
public class AddRatingServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            // Get user from session
            HttpSession session = request.getSession();
            User user = (User) session.getAttribute("user");

            if (user == null) {
                response.getWriter().write("{\"success\": false, \"message\": \"User not logged in\"}");
                return;
            }

            // Get parameters
            String movieIdStr = request.getParameter("movieId");
            String ratingStr = request.getParameter("rating");

            if (movieIdStr == null || ratingStr == null) {
                response.getWriter().write("{\"success\": false, \"message\": \"Missing parameters\"}");
                return;
            }

            int movieId = Integer.parseInt(movieIdStr);
            int userId = user.getId();
            double ratingValue = Double.parseDouble(ratingStr);

            // Get DAO from servlet context
            MovieRatingsDao movieRatingsDao = (MovieRatingsDao) getServletContext().getAttribute("movieRatingsDao");

            // Create MovieRating object
            MovieRating rating = new MovieRating();
            rating.setMovieId(movieId);
            rating.setUserId(userId);
            rating.setScoreValue(ratingValue);
            rating.setScoreDate(new java.util.Date());

            // Add or update rating
            movieRatingsDao.addOrUpdateMovieRating(rating);

            response.getWriter().write("{\"success\": true, \"message\": \"Rating submitted successfully\"}");

        } catch (NumberFormatException e) {
            response.getWriter().write("{\"success\": false, \"message\": \"Invalid number format\"}");
        } catch (SQLException e) {
            e.printStackTrace();
            response.getWriter().write("{\"success\": false, \"message\": \"Database error: " + e.getMessage() + "\"}");
        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().write("{\"success\": false, \"message\": \"Error: " + e.getMessage() + "\"}");
        }
    }
}