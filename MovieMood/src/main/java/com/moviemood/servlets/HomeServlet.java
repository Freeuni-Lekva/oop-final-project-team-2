package com.moviemood.servlets;

import com.moviemood.Enums.MovieCategory;
import com.moviemood.bean.Movie;
import com.moviemood.bean.User;
import com.moviemood.config.Config;
import com.moviemood.dao.UserMoviePreferencesDao;
import com.moviemood.repository.tmdb.TmdbMovieRepository;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
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
        UserMoviePreferencesDao preferencesDao = (UserMoviePreferencesDao) getServletContext().getAttribute("moviePreferencesDao");
        User user=(User) req.getSession().getAttribute("user");
        if(user!=null){
            try {
                List<Integer> preferedMovies=preferencesDao.getUserPreferences(user.getId());
                List<Movie> recommendedMovies=new ArrayList<>();
                for(int i=0;i<preferedMovies.size();i++){
                    recommendedMovies.addAll(moviesRepo.fetchRecommendations(preferedMovies.get(i)));
                }
                req.setAttribute("recomededMovies", recommendedMovies);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        }
        req.setAttribute("movies", movies);
        req.setAttribute("POSTER_BASE", Config.get("posterPathBase"));
        req.setAttribute("currentPage", page);
        req.setAttribute("totalPages", 500);

        req.getRequestDispatcher("/movies.jsp").forward(req, resp);
    }

}