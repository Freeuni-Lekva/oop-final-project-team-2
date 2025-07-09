package com.moviemood.servlets;

import com.moviemood.bean.Movie;
import com.moviemood.config.Config;
import com.moviemood.repository.tmdb.TmdbMovieRepository;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet(urlPatterns = {"/Home", "/moviemood", "/movies", "/films"})
public class HomeServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPost(req, resp);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        TmdbMovieRepository moviesRepo = TmdbMovieRepository.getInstance();

        // Get page parameter from request, default to 1 if not present
        int page = 1;
        String pageParam = req.getParameter("page");
        if (pageParam != null && !pageParam.trim().isEmpty()) {
            try {
                page = Integer.parseInt(pageParam);
                // Ensure page is within valid range
                if (page < 1) {
                    page = 1;
                } else if (page > 500) {
                    page = 500;
                }
            } catch (NumberFormatException e) {
                // If parsing fails, default to page 1
                page = 1;
            }
        }

        // Fetch movies for the specified page
        List<Movie> movies = moviesRepo.fetchAll(page);
        List<Movie> recomededMovies = moviesRepo.fetchSimilar(278);

        // Set attributes for JSP
        req.setAttribute("movies", movies);
        req.setAttribute("recomededMovies", recomededMovies);
        req.setAttribute("POSTER_BASE", Config.get("posterPathBase"));
        req.setAttribute("currentPage", page); // Add current page to JSP
        req.setAttribute("totalPages", 500); // Add total pages to JSP

        req.getRequestDispatcher("/movies.jsp").forward(req, resp);
    }
}