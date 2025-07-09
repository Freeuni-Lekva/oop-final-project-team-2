package com.moviemood.servlets;

import com.moviemood.Enums.MovieCategory;
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

        int page = 1;
        String pageParam = req.getParameter("page");
        if (pageParam != null && !pageParam.trim().isEmpty()) {
            try {
                page = Integer.parseInt(pageParam);
                page = Math.max(1, Math.min(page, 500));
            } catch (NumberFormatException ignored) {}
        }

        // Get filters
        String genreParam = req.getParameter("genre");
        String yearParam = req.getParameter("year");
        String categoryParam = req.getParameter("sort"); // category like popular, top_rated
        String runtimeParam = req.getParameter("runtime");
        String titleParam = req.getParameter("title");

        List<Movie> movies;

        // 🔍 1. Search by title if title is present
        if (titleParam != null && !titleParam.isEmpty()) {
            movies = moviesRepo.search(titleParam, page);
        }
        // 🎯 2. If ONLY category is selected (no genre/year/runtime), fetch by category
        else if (
                categoryParam != null && !categoryParam.isEmpty() &&
                        (genreParam == null || genreParam.isEmpty()) &&
                        (yearParam == null || yearParam.isEmpty()) &&
                        (runtimeParam == null || runtimeParam.isEmpty())
        ) {
            try {
                MovieCategory category = MovieCategory.valueOf(categoryParam.toUpperCase());
                movies = moviesRepo.fetchByCategory(category, page);
            } catch (IllegalArgumentException e) {
                movies = moviesRepo.fetchAll(page); // fallback
            }
        }
        // 🛠️ 3. Else use discover with genre/year/runtime
        else {
            movies = moviesRepo.discoverWithFilters(genreParam, yearParam, runtimeParam, page);
        }

        // Reuse base poster path and recommended movies
        List<Movie> recommendedMovies = moviesRepo.fetchSimilar(278);

        req.setAttribute("movies", movies);
        req.setAttribute("recomededMovies", recommendedMovies);
        req.setAttribute("POSTER_BASE", Config.get("posterPathBase"));
        req.setAttribute("currentPage", page);
        req.setAttribute("totalPages", 500);

        req.getRequestDispatcher("/movies.jsp").forward(req, resp);
    }

}