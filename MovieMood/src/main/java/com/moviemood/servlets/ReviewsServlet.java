package com.moviemood.servlets;

import com.moviemood.bean.Movie;
import com.moviemood.bean.MovieReview;
import com.moviemood.bean.User;
import com.moviemood.config.Config;
import com.moviemood.dao.MovieReviewsDao;
import com.moviemood.dao.UserDao;
import com.moviemood.repository.tmdb.TmdbMovieRepository;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@WebServlet("/reviews")
public class ReviewsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        User currentUser = null;
        if (session != null) {
            currentUser = (User) session.getAttribute("user");
        }
        
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }
        
        // Determine whose reviews to show
        String username = request.getParameter("user");
        User targetUser = currentUser; // Default to current user
        boolean isOwnReviews = true;
        
        if (username != null && !username.trim().isEmpty()) {
            // Viewing someone else's reviews
            try {
                UserDao userDao = (UserDao) getServletContext().getAttribute("userDao");
                if (userDao != null) {
                    User foundUser = userDao.getUserByUsername(username);
                    if (foundUser != null) {
                        targetUser = foundUser;
                        isOwnReviews = (currentUser.getId() == foundUser.getId());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                // Fall back to current user if error
            }
        }
        
        try {
            MovieReviewsDao reviewsDao = (MovieReviewsDao) getServletContext().getAttribute("reviewsDao");
            TmdbMovieRepository movieRepo = TmdbMovieRepository.getInstance();
            
            if (reviewsDao == null) {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Reviews service not available");
                return;
            }
            
            List<MovieReview> userReviews = reviewsDao.getUserMovieReviews(targetUser.getId());
            Map<Integer, Movie> movieMap = new HashMap<>();
            
            for (MovieReview review : userReviews) {
                try {
                    Optional<Movie> movieOpt = movieRepo.findById(review.getMovieId());
                    if (movieOpt.isPresent()) {
                        movieMap.put(review.getMovieId(), movieOpt.get());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            
            request.setAttribute("userReviews", userReviews);
            request.setAttribute("movieMap", movieMap);
            request.setAttribute("POSTER_BASE", Config.get("posterPathBase"));
            request.setAttribute("currentUser", currentUser);
            request.setAttribute("targetUser", targetUser);
            request.setAttribute("isOwnReviews", isOwnReviews);
            
            request.getRequestDispatcher("/reviews.jsp").forward(request, response);
            
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error loading reviews");
        }
    }
} 