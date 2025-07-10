package com.moviemood.servlets;

import com.moviemood.bean.User;
import com.moviemood.bean.Movie;
import com.moviemood.config.Config;

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

@WebServlet("/watchlist")
public class WatchlistServlet extends HttpServlet {

    private static final User DEMO_USER = new User(1, "DemoUser", "demo@moviemood.com", "hashedpassword", null);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        User currentUser = null;
        if (session != null) {
            currentUser = (User) session.getAttribute("user");
        }
        
        List<Movie> watchlistMovies = new ArrayList<>();
        boolean isDemoWatchlist = false;
        
        if (currentUser == null) {
            // Create demo watchlist for testing
            isDemoWatchlist = true;
            watchlistMovies = createDemoWatchlist();
        } else {
            // TODO: Get real user watchlist when authentication is ready
            // For now, show demo even for logged in users
            isDemoWatchlist = true;
            watchlistMovies = createDemoWatchlist();
        }
        
        request.setAttribute("watchlistMovies", watchlistMovies);
        request.setAttribute("isDemoWatchlist", isDemoWatchlist);
        request.setAttribute("currentUser", currentUser);
        request.setAttribute("POSTER_BASE", Config.get("posterPathBase"));
        
        request.getRequestDispatcher("/watchlist.jsp").forward(request, response);
    }
    
    private List<Movie> createDemoWatchlist() {
        List<Movie> movies = new ArrayList<>();
        
        // Add some demo movies with real TMDB data
        Movie movie1 = new Movie();
        movie1.setId(278);
        movie1.setTitle("The Gldani Redemption");
        movie1.setOverview("Two imprisoned men bond over a number of years, finding solace and eventual redemption through acts of common decency.");
        movie1.setPosterPath("/q6y0Go1tsGEsmtFryDOJo3dEmqu.jpg");
        movie1.setReleaseDate(LocalDate.of(1994, 9, 23));
        movie1.setPopularity(2550.0);
        movies.add(movie1);
            
        Movie movie2 = new Movie();
        movie2.setId(238);
        movie2.setTitle("The Godfather");
        movie2.setOverview("The aging patriarch of an organized crime dynasty transfers control of his clandestine empire to his reluctant son.");
        movie2.setPosterPath("/3bhkrj58Vtu7enYsRolD1fZdja1.jpg");
        movie2.setReleaseDate(LocalDate.of(1972, 3, 14));
        movie2.setPopularity(1850.0);
        movies.add(movie2);
            
        Movie movie3 = new Movie();
        movie3.setId(424);
        movie3.setTitle("Schindler's List");
        movie3.setOverview("In German-occupied Poland during World War II, Oskar Schindler gradually becomes concerned for his Jewish workforce.");
        movie3.setPosterPath("/sF1U4EUQS8YHUYjNl3pMGNIQyr0.jpg");
        movie3.setReleaseDate(LocalDate.of(1993, 12, 15));
        movie3.setPopularity(1320.0);
        movies.add(movie3);
            
        Movie movie4 = new Movie();
        movie4.setId(389);
        movie4.setTitle("12 Men");
        movie4.setOverview("A jury holdout attempts to prevent a miscarriage of justice by forcing his colleagues to reconsider the evidence.");
        movie4.setPosterPath("/ow3wq89wM8qd5X7hWKxiRfsFf9C.jpg");
        movie4.setReleaseDate(LocalDate.of(1957, 4, 10));
        movie4.setPopularity(950.0);
        movies.add(movie4);
            
        Movie movie5 = new Movie();
        movie5.setId(155);
        movie5.setTitle("The Dark Knight");
        movie5.setOverview("When the menace known as the Joker wreaks havoc and chaos on the people of Gotham, Batman must accept one of the greatest psychological and physical tests.");
        movie5.setPosterPath("/qJ2tW6WMUDux911r6m7haRef0WH.jpg");
        movie5.setReleaseDate(LocalDate.of(2008, 7, 18));
        movie5.setPopularity(2750.0);
        movies.add(movie5);

        return movies;
    }
} 