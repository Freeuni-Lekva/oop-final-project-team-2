package com.moviemood.servlets;

import com.moviemood.bean.Movie;
import com.moviemood.bean.User;
import com.moviemood.config.Config;
import com.moviemood.repository.tmdb.TmdbMovieRepository;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

import org.apache.commons.dbcp2.BasicDataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;


@WebServlet("/movie-preferences")
public class MoviePreferencesServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            TmdbMovieRepository movieRepo = TmdbMovieRepository.getInstance();
            List<Movie> movies = movieRepo.fetchAll(1);
            request.setAttribute("movies", movies);
            request.setAttribute("POSTER_BASE", Config.get("posterPathBase"));
            request.getRequestDispatcher("/movie-preferences.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error loading movies");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String[] selectedMovieIds = request.getParameterValues("selectedMovies");

        if (selectedMovieIds == null || selectedMovieIds.length < 3 || selectedMovieIds.length > 4) {
            request.setAttribute("error", "Please select 3-4 movies");
            doGet(request, response);
            return;
        }

        HttpSession session = request.getSession(false);
        User currentUser = null;
        if (session != null) {
            currentUser = (User) session.getAttribute("user");
        }

        if (currentUser == null) {
            response.sendRedirect("/login");
            return;
        }

        int userId = currentUser.getId();

        try {
            BasicDataSource dataSource = (BasicDataSource) getServletContext().getAttribute("dataSource");

            // Save preferences in database
            for (String movieIdStr : selectedMovieIds) {
                int movieId = Integer.parseInt(movieIdStr);
                saveUserPreference(dataSource, userId, movieId);
            }

            response.sendRedirect("/Home?firstTime=true");

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error saving preferences. Please try again.");
            doGet(request, response);
        }
    }

    private void saveUserPreference(BasicDataSource dataSource, int userId, int movieId) throws SQLException {
        String sql = "INSERT IGNORE INTO user_movie_preferences (user_id, movie_id) VALUES (?, ?)";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setInt(2, movieId);
            stmt.executeUpdate();
        }
    }
}
