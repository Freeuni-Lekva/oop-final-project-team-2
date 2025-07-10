package com.moviemood.servlets;

import com.moviemood.bean.Movie;
import com.moviemood.bean.User;
import com.moviemood.config.Config;
import com.moviemood.dao.UserFavoritesDao;
import com.moviemood.dao.UserWatchlistDao;
import com.moviemood.repository.tmdb.TmdbMovieRepository;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Optional;

@WebServlet("/movie/details")
public class MovieDetailsServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPost(req, resp);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int movie_id=Integer.parseInt(req.getParameter("id"));
        TmdbMovieRepository moviesRepo=TmdbMovieRepository.getInstance();
        Optional<Movie> movieOpt=moviesRepo.findById(movie_id);
        if(movieOpt.isPresent()){
            Movie movie=movieOpt.get();
            req.setAttribute("movie",movie);
            req.setAttribute("backDropPathBaseURL", Config.get("backDropPathBase"));
            req.setAttribute("POSTER_BASE",Config.get("posterPathBase"));
            Optional<String> trailerKeyOpt = moviesRepo.fetchYoutubeTrailerKey(movie_id);
            trailerKeyOpt.ifPresent(trailerKey -> req.setAttribute("trailerKey", trailerKey));
            
            // Check if user is logged in and get watchlist/favorites status
            HttpSession session = req.getSession(false);
            User currentUser = null;
            boolean isInWatchlist = false;
            boolean isInFavorites = false;
            
            if (session != null) {
                currentUser = (User) session.getAttribute("user");
            }
            
            if (currentUser != null) {
                try {
                    UserWatchlistDao watchlistDao = (UserWatchlistDao) getServletContext().getAttribute("watchlistDao");
                    UserFavoritesDao favoritesDao = (UserFavoritesDao) getServletContext().getAttribute("favoritesDao");
                    
                    if (watchlistDao != null) {
                        isInWatchlist = watchlistDao.isMovieInWatchlist(currentUser.getId(), movie_id);
                    }
                    if (favoritesDao != null) {
                        isInFavorites = favoritesDao.isMovieInFavorites(currentUser.getId(), movie_id);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    // Continue with default false values
                }
            }
            
            req.setAttribute("currentUser", currentUser);
            req.setAttribute("isInWatchlist", isInWatchlist);
            req.setAttribute("isInFavorites", isInFavorites);
            
            req.getRequestDispatcher("/movie-details.jsp").forward(req,resp);
        }else{
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Movie not found");
        }
    }
}
