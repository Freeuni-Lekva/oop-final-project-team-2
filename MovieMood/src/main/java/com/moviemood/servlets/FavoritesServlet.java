package com.moviemood.servlets;

import com.moviemood.bean.User;
import com.moviemood.bean.Movie;
import com.moviemood.config.Config;
import com.moviemood.dao.UserFavoritesDao;
import com.moviemood.repository.tmdb.TmdbMovieRepository;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@WebServlet("/favorites")
public class FavoritesServlet extends HttpServlet {

    private static final User DEMO_USER = new User(1, "DemoUser", "demo@moviemood.com", "hashedpassword", null);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        User currentUser = null;
        if (session != null) {
            currentUser = (User) session.getAttribute("user");
        }
        
        List<Movie> favoritesMovies = new ArrayList<>();
        boolean isDemoFavorites = false;
        
        if (currentUser == null) {
            // Create demo favorites for testing
            isDemoFavorites = true;
            favoritesMovies = createDemoFavorites();
        } else {
            // Get real user favorites from database
            try {
                UserFavoritesDao favoritesDao = (UserFavoritesDao) getServletContext().getAttribute("favoritesDao");
                if (favoritesDao != null) {
                    List<Integer> movieIds = favoritesDao.getUserFavorites(currentUser.getId());
                    favoritesMovies = fetchMoviesFromTmdb(movieIds);
                    isDemoFavorites = false;
                } else {
                    // Fallback to demo if DAO not available
                    isDemoFavorites = true;
                    favoritesMovies = createDemoFavorites();
                }
            } catch (Exception e) {
                e.printStackTrace();
                // Fallback to demo on error
                isDemoFavorites = true;
                favoritesMovies = createDemoFavorites();
            }
        }
        
        request.setAttribute("favoritesMovies", favoritesMovies);
        request.setAttribute("isDemoFavorites", isDemoFavorites);
        request.setAttribute("currentUser", currentUser);
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
    
    private List<Movie> createDemoFavorites() {
        List<Movie> movies = new ArrayList<>();
        
        // Add 3 demo favorite movies
        Movie movie1 = new Movie();
        movie1.setId(238);
        movie1.setTitle("The Godfather");
        movie1.setOverview("The aging patriarch of an organized crime dynasty transfers control of his clandestine empire to his reluctant son.");
        movie1.setPosterPath("/3bhkrj58Vtu7enYsRolD1fZdja1.jpg");
        movie1.setReleaseDate(LocalDate.of(1972, 3, 14));
        movie1.setPopularity(1850.0);
        movies.add(movie1);
            
        Movie movie2 = new Movie();
        movie2.setId(155);
        movie2.setTitle("The Dark Knight");
        movie2.setOverview("When the menace known as the Joker wreaks havoc and chaos on the people of Gotham, Batman must accept one of the greatest psychological and physical tests.");
        movie2.setPosterPath("/qJ2tW6WMUDux911r6m7haRef0WH.jpg");
        movie2.setReleaseDate(LocalDate.of(2008, 7, 18));
        movie2.setPopularity(2750.0);
        movies.add(movie2);
            
        Movie movie3 = new Movie();
        movie3.setId(550);
        movie3.setTitle("Fight Club");
        movie3.setOverview("A ticking-time-bomb insomniac and a slippery soap salesman channel primal male aggression into a shocking new form of therapy.");
        movie3.setPosterPath("/pB8BM7pdSp6B6Ih7QZ4DrQ3PmJK.jpg");
        movie3.setReleaseDate(LocalDate.of(1999, 10, 15));
        movie3.setPopularity(1980.0);
        movies.add(movie3);

        return movies;
    }
} 