package com.moviemood.servlets;

import com.moviemood.bean.User;
import com.moviemood.bean.Movie;
import com.moviemood.config.Config;
import com.moviemood.dao.UserFavoritesDao;
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
import java.util.List;
import java.util.Optional;

@WebServlet("/favorites")
public class FavoritesServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        User currentUser = null;
        if (session != null) {
            currentUser = (User) session.getAttribute("user");
        }
        
        // Require login
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }
        
        // Determine whose favorites to show
        String username = request.getParameter("user");
        User targetUser = currentUser; // Default to current user
        boolean isOwnFavorites = true;
        
        if (username != null && !username.trim().isEmpty()) {
            // Viewing someone else's favorites
            try {
                UserDao userDao = (UserDao) getServletContext().getAttribute("userDao");
                if (userDao != null) {
                    User foundUser = userDao.getUserByUsername(username);
                    if (foundUser != null) {
                        targetUser = foundUser;
                        isOwnFavorites = (currentUser.getId() == foundUser.getId());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                // Fall back to current user if error
            }
        }
        
        List<Movie> favoritesMovies = new ArrayList<>();
        
        // Get user favorites from database
        try {
            UserFavoritesDao favoritesDao = (UserFavoritesDao) getServletContext().getAttribute("favoritesDao");
            if (favoritesDao != null) {
                List<Integer> movieIds = favoritesDao.getUserFavorites(targetUser.getId());
                favoritesMovies = fetchMoviesFromTmdb(movieIds);
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Continue with empty list on error
        }
        
        request.setAttribute("favoritesMovies", favoritesMovies);
        request.setAttribute("currentUser", currentUser);
        request.setAttribute("targetUser", targetUser);
        request.setAttribute("isOwnFavorites", isOwnFavorites);
        request.setAttribute("POSTER_BASE", Config.get("posterPathBase"));
        
        request.getRequestDispatcher("/favorites.jsp").forward(request, response);
    }
    
    /**
     * Fetch movie details from TMDB API for given movie IDs
     */
    private List<Movie> fetchMoviesFromTmdb(List<Integer> movieIds) {
        List<Movie> movies = new ArrayList<>();
        TmdbMovieRepository movieRepo = TmdbMovieRepository.getInstance();
        
        for (Integer movieId : movieIds) {
            try {
                Optional<Movie> movieOpt = movieRepo.findById(movieId);
                if (movieOpt.isPresent()) {
                    movies.add(movieOpt.get());
                }
            } catch (IOException e) {
                e.printStackTrace();
                // Continue with other movies if one fails
            }
        }
        
        return movies;
    }

} 