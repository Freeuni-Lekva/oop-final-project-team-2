package com.moviemood.servlets;

import com.moviemood.bean.User;
import com.moviemood.bean.Movie;
import com.moviemood.config.Config;
import com.moviemood.dao.UserWatchlistDao;
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

@WebServlet("/watchlist")
public class WatchlistServlet extends HttpServlet {

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
        
        List<Movie> watchlistMovies = new ArrayList<>();
        
        // Get real user watchlist from database
        try {
            UserWatchlistDao watchlistDao = (UserWatchlistDao) getServletContext().getAttribute("watchlistDao");
            if (watchlistDao != null) {
                List<Integer> movieIds = watchlistDao.getUserWatchList(currentUser.getId());
                watchlistMovies = fetchMoviesFromTmdb(movieIds);
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Continue with empty list on error
        }
        
        request.setAttribute("watchlistMovies", watchlistMovies);
        request.setAttribute("currentUser", currentUser);
        request.setAttribute("POSTER_BASE", Config.get("posterPathBase"));
        
        request.getRequestDispatcher("/watchlist.jsp").forward(request, response);
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