package com.moviemood.repository.api;

import com.moviemood.Enums.MovieCategory;
import com.moviemood.bean.Movie;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface MovieRepository {

    List<Movie> fetchAll(int page) throws IOException;

    // 🔹 TMDB Category-based fetch (popular, top-rated, etc.)
    List<Movie> fetchByCategory(MovieCategory category, int page) throws IOException;

    // 🔹 Search movie by title or keyword
    List<Movie> search(String query, int page) throws IOException;

    // 🔹 Filter with several types
    public List<Movie> discoverWithFilters(String genre, String year, String runtime, int page) throws IOException;

    // 🔹 Get detailed movie info by TMDB ID
    Optional<Movie> findById(int tmdbId) throws IOException;

    // 🔹 Filter movies by genre
    List<Movie> fetchByGenre(int genreId, int page) throws IOException;

    // 🔹 Filter by release year
    List<Movie> fetchByYear(int year, int page) throws IOException;

    // 🔹 Discover endpoint: sort + filter
    List<Movie> discover(String sortBy, int page) throws IOException;

    // 🔹 Get similar movies for a given movie
    List<Movie> fetchSimilar(int tmdbId) throws IOException;

    // 🔹 Get recommended movies for a given movie
    List<Movie> fetchRecommendations(int tmdbId) throws IOException;

    // 🔹 (Optional) Get random popular movie
    Optional<Movie> fetchRandomPopular();
}
