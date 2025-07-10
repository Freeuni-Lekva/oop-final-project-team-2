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
}
