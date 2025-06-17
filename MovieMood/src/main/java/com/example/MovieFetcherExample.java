package com.example;

import com.example.client.TMDbClient;
import com.example.model.Movie;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

public class MovieFetcherExample {
    private static final Logger logger = LoggerFactory.getLogger(MovieFetcherExample.class);

    // Replace with your actual API key from TMDb
    // Get your API key from: https://www.themoviedb.org/settings/api
    private static final String API_KEY = "e95e2804a169c17be1a00690449b5bd1";

    public static void main(String[] args) {
        // Check if API key is set
        if ("YOUR_API_KEY_HERE".equals(API_KEY)) {
            System.err.println("ERROR: Please set your TMDb API key!");
            System.err.println("1. Go to https://www.themoviedb.org/settings/api");
            System.err.println("2. Get your API key");
            System.err.println("3. Replace 'YOUR_API_KEY_HERE' in this file");
            return;
        }

        TMDbClient client = new TMDbClient(API_KEY);

        try {
            // Start with a small test
            System.out.println("=== Testing API Connection ===");
            List<Movie> testMovies = client.getMovies(5, "popular");
            if (testMovies.isEmpty()) {
                System.err.println("No movies returned. Check your API key.");
                return;
            }
            System.out.println("✓ API connection successful!");
            printMovieSummary(testMovies, "Test Movies");

            // Ask user what they want to do
            System.out.println("\n=== Choose what to fetch ===");
            System.out.println("Fetching large amounts of movies...");

            // Example 1: Get 100 popular movies (smaller number for testing)
            System.out.println("\n=== Fetching 100 Popular Movies ===");
            List<Movie> movies100 = client.getMovies(100, "popular");
            printMovieSummary(movies100, "Popular Movies (100)");

            // Example 2: Get detailed information for first 10 movies
            System.out.println("\n=== Getting Detailed Information (First 10) ===");
            List<Movie> first10 = movies100.subList(0, Math.min(10, movies100.size()));
            List<Movie> detailedMovies = client.getDetailedMovies(first10);
            printDetailedMovies(detailedMovies);

            // Uncomment these lines if you want to fetch larger amounts:
            /*
            // Get 1000 movies
            System.out.println("\n=== Fetching 1000 Popular Movies ===");
            List<Movie> movies1000 = client.getMovies(1000, "popular");
            printMovieSummary(movies1000, "Popular Movies (1000)");

            // Get 2000 movies
            System.out.println("\n=== Fetching 2000 Top Rated Movies ===");
            List<Movie> movies2000 = client.getMovies(2000, "top_rated");
            printMovieSummary(movies2000, "Top Rated Movies (2000)");
            */

        } catch (IOException e) {
            logger.error("Error fetching movies: {}", e.getMessage(), e);
            System.err.println("Error: " + e.getMessage());
        } finally {
            // Ensure proper cleanup
            try {
                client.close();
                // Force garbage collection to help clean up
                System.gc();
                // Small delay to allow cleanup
                Thread.sleep(100);
            } catch (Exception e) {
                logger.warn("Error during cleanup: {}", e.getMessage());
            }
        }
    }

    private static void printMovieSummary(List<Movie> movies, String category) {
        System.out.println("\n" + category + " - Total: " + movies.size());
        System.out.println("─".repeat(50));

        // Print first 10 movies as examples
        int limit = Math.min(10, movies.size());
        for (int i = 0; i < limit; i++) {
            Movie movie = movies.get(i);
            System.out.printf("%d. %s (%s) - Rating: %.1f/10\n",
                    i + 1,
                    movie.getTitle(),
                    movie.getReleaseDate() != null ? movie.getReleaseDate().substring(0, 4) : "N/A",
                    movie.getVoteAverage());
        }

        if (movies.size() > 10) {
            System.out.println("... and " + (movies.size() - 10) + " more movies");
        }
    }

    private static void printDetailedMovies(List<Movie> movies) {
        System.out.println("\nDetailed Movie Information (first 10):");
        System.out.println("─".repeat(80));

        int limit = Math.min(10, movies.size());
        for (int i = 0; i < limit; i++) {
            Movie movie = movies.get(i);
            System.out.printf("\n%d. %s (%s)\n",
                    i + 1,
                    movie.getTitle(),
                    movie.getReleaseDate() != null ? movie.getReleaseDate().substring(0, 4) : "N/A");

            if (movie.getOverview() != null && !movie.getOverview().isEmpty()) {
                String shortOverview = movie.getOverview().length() > 100 ?
                        movie.getOverview().substring(0, 100) + "..." : movie.getOverview();
                System.out.println("   Description: " + shortOverview);
            }

            System.out.println("   Rating: " + movie.getVoteAverage() + "/10 (" + movie.getVoteCount() + " votes)");

            if (movie.getRuntime() > 0) {
                System.out.println("   Runtime: " + movie.getRuntime() + " minutes");
            }

            if (movie.getImdbId() != null) {
                System.out.println("   IMDB: " + movie.getImdbUrl());
            }

            if (movie.getPosterPath() != null) {
                System.out.println("   Poster: " + movie.getFullPosterUrl());
            }

//            if (movie.getGenres() != null && !movie.getGenres().isEmpty()) {
//                System.out.print("   Genres: ");
//                for (int j = 0; j < movie.getGenres().size(); j++) {
//                    System.out.print(movie.getGenres().get(j).getName());
//                    if (j < movie.getGenres().size() - 1) System.out.print(", ");
//                }
//                System.out.println();
//            }
        }
    }
}