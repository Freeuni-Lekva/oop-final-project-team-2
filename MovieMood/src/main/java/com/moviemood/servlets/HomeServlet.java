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

@WebServlet("/Home")
public class HomeServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPost(req, resp);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        TmdbMovieRepository moviesRepo=new TmdbMovieRepository();
        List<Movie> movies=moviesRepo.fetchAll(1);
        List<Movie> recomededMovies=moviesRepo.fetchSimilar(278);
        req.setAttribute("movies",movies);
        req.setAttribute("recomededMovies",recomededMovies);
        req.setAttribute("POSTER_BASE", Config.get("posterPathBase"));
        req.getRequestDispatcher("/movies.jsp").forward(req, resp);
    }
}
