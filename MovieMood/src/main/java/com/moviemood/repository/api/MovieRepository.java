package com.moviemood.repository.api;

import com.moviemood.Enums.MovieCategory;
import com.moviemood.bean.Movie;

import java.util.List;
import java.util.Optional;

public interface MovieRepository {

    // 🔹 TMDB Category-based fetch (popular, top-rated, etc.)
    List<Movie> fetchByCategory(MovieCategory category, int page);

    // 🔹 Search movie by title or keyword
    List<Movie> search(String query, int page);

    // 🔹 Get detailed movie info by TMDB ID
    Optional<Movie> findById(int tmdbId);

    // 🔹 Get movie by IMDb ID (if available)
    Optional<Movie> findByImdbId(String imdbId);

    // 🔹 Filter movies by genre
    List<Movie> fetchByGenre(int genreId, int page);

    // 🔹 Filter by release year
    List<Movie> fetchByYear(int year, int page);

    // 🔹 Discover endpoint: sort + filter
    List<Movie> discover(String sortBy, int page);

    // 🔹 Get similar movies for a given movie
    List<Movie> fetchSimilar(int tmdbId);

    // 🔹 Get recommended movies for a given movie
    List<Movie> fetchRecommendations(int tmdbId);

    // 🔹 (Optional) Recently released movies
    List<Movie> fetchRecentlyReleased();

    // 🔹 (Optional) Get random popular movie
    Optional<Movie> fetchRandomPopular();
}
