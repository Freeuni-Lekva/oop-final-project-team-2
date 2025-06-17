package com.example.client;

import com.example.model.Movie;
import com.example.model.MovieResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class TMDbClient {
    private static final Logger logger = LoggerFactory.getLogger(TMDbClient.class);

    private static final String BASE_URL = "https://api.themoviedb.org/3";
    private final String apiKey;
    private final OkHttpClient httpClient;
    private final Gson gson;

    public TMDbClient(String apiKey) {
        this.apiKey = apiKey;
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
        this.gson = new GsonBuilder().create();
    }

    /**
     * Get popular movies with pagination
     */
    public MovieResponse getPopularMovies(int page) throws IOException {
        String url = String.format("%s/movie/popular?api_key=%s&page=%d", BASE_URL, apiKey, page);
        //System.out.println(url);
        return executeRequest(url, MovieResponse.class);
    }

    /**
     * Get top rated movies with pagination
     */
    public MovieResponse getTopRatedMovies(int page) throws IOException {
        String url = String.format("%s/movie/top_rated?api_key=%s&page=%d", BASE_URL, apiKey, page);
        //System.out.println(url);
        return executeRequest(url, MovieResponse.class);
    }

    /**
     * Get now playing movies with pagination
     */
    public MovieResponse getNowPlayingMovies(int page) throws IOException {
        String url = String.format("%s/movie/now_playing?api_key=%s&page=%d", BASE_URL, apiKey, page);
        return executeRequest(url, MovieResponse.class);
    }

    /**
     * Get upcoming movies with pagination
     */
    public MovieResponse getUpcomingMovies(int page) throws IOException {
        String url = String.format("%s/movie/upcoming?api_key=%s&page=%d", BASE_URL, apiKey, page);
        return executeRequest(url, MovieResponse.class);
    }

    /**
     * Get movie details including IMDB ID
     */
    public Movie getMovieDetails(int movieId) throws IOException {
        String url = String.format("%s/movie/%d?api_key=%s", BASE_URL, movieId, apiKey);
        return executeRequest(url, Movie.class);
    }

    /**
     * Get specific number of movies from popular movies
     * This method handles pagination automatically
     */
    public List<Movie> getMovies(int count) throws IOException {
        return getMovies(count, "popular");
    }

    /**
     * Get specific number of movies from a category
     * Categories: "popular", "top_rated", "now_playing", "upcoming"
     */
    public List<Movie> getMovies(int count, String category) throws IOException {
        List<Movie> allMovies = new ArrayList<>();
        int page = 1;
        int moviesPerPage = 20; // TMDb returns 20 movies per page

        logger.info("Fetching {} movies from {} category", count, category);

        while (allMovies.size() < count) {
            MovieResponse response;

            switch (category.toLowerCase()) {
                case "popular":
                    response = getPopularMovies(page);
                    break;
                case "top_rated":
                    response = getTopRatedMovies(page);
                    break;
                case "now_playing":
                    response = getNowPlayingMovies(page);
                    break;
                case "upcoming":
                    response = getUpcomingMovies(page);
                    break;
                default:
                    throw new IllegalArgumentException("Invalid category: " + category);
            }

            if (response.getResults() == null || response.getResults().isEmpty()) {
                logger.warn("No more movies available. Got {} movies total.", allMovies.size());
                break;
            }

            // Add movies to our list
            List<Movie> movies = response.getResults();
            int needed = Math.min(movies.size(), count - allMovies.size());
            allMovies.addAll(movies.subList(0, needed));

            logger.info("Fetched page {}, got {} movies, total so far: {}",
                    page, movies.size(), allMovies.size());

            // Check if we've reached the last page
            if (page >= response.getTotalPages()) {
                logger.info("Reached last page ({}). Total movies available: {}",
                        response.getTotalPages(), response.getTotalResults());
                break;
            }

            page++;

            // Add a small delay to be respectful to the API
            try {
                Thread.sleep(100); // 100ms delay between requests
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        logger.info("Successfully fetched {} movies", allMovies.size());
        return allMovies;
    }

    /**
     * Get detailed information for a list of movies (including IMDB IDs)
     */
    public List<Movie> getDetailedMovies(List<Movie> movies) throws IOException {
        List<Movie> detailedMovies = new ArrayList<>();

        logger.info("Fetching detailed information for {} movies", movies.size());

        for (int i = 0; i < movies.size(); i++) {
            Movie movie = movies.get(i);
            try {
                Movie detailedMovie = getMovieDetails(movie.getId());
                detailedMovies.add(detailedMovie);

                if ((i + 1) % 10 == 0) {
                    logger.info("Processed {} / {} movies", i + 1, movies.size());
                }

                // Add delay to respect rate limits
                Thread.sleep(50); // 50ms delay between detail requests

            } catch (Exception e) {
                logger.error("Error fetching details for movie ID {}: {}", movie.getId(), e.getMessage());
                // Add the original movie without detailed info
                detailedMovies.add(movie);
            }
        }

        logger.info("Successfully fetched detailed information for {} movies", detailedMovies.size());
        return detailedMovies;
    }

    private <T> T executeRequest(String url, Class<T> responseType) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Accept", "application/json")
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Request failed: " + response.code() + " " + response.message());
            }

            String responseBody = response.body().string();
            return gson.fromJson(responseBody, responseType);
        }
    }

    public void close() {
        try {
            // Shutdown the dispatcher's executor service
            httpClient.dispatcher().executorService().shutdown();

            // Wait for tasks to complete
            if (!httpClient.dispatcher().executorService().awaitTermination(5, TimeUnit.SECONDS)) {
                httpClient.dispatcher().executorService().shutdownNow();
            }

            // Evict all connections from the connection pool
            httpClient.connectionPool().evictAll();

            // Close the cache if it exists
            if (httpClient.cache() != null) {
                httpClient.cache().close();
            }

        } catch (Exception e) {
            logger.warn("Error during HTTP client shutdown: {}", e.getMessage());
        }
    }
}