package com.example.service;

import com.example.client.TMDbClient;
import com.example.model.Movie;
import com.example.repository.MovieRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

@Service
public class MovieSyncService {
    private static final Logger logger = LoggerFactory.getLogger(MovieSyncService.class);

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private TMDbClient tmdbClient;

    @Value("${app.movie-sync.enabled:true}")
    private boolean syncEnabled;

    @Value("${app.movie-sync.batch-size:20}")
    private int batchSize;

    @Value("${app.movie-sync.max-pages:50}")
    private int maxPages;

    private final AtomicBoolean syncInProgress = new AtomicBoolean(false);
    private int currentPage = 1;
    private String currentCategory = "popular";
    private final String[] categories = {"popular", "top_rated", "now_playing", "upcoming"};
    private int currentCategoryIndex = 0;

    @PostConstruct
    public void initialize() {
        if (syncEnabled) {
            logger.info("Movie sync service initialized. Sync enabled: {}", syncEnabled);
            // Check if initial sync is needed
            checkAndPerformInitialSync();
        } else {
            logger.info("Movie sync service disabled via configuration");
        }
    }

    /**
     * Check if database is empty and perform initial sync if needed
     */
    private void checkAndPerformInitialSync() {
        try {
            long movieCount = movieRepository.count();
            if (movieCount == 0) {
                logger.info("Database is empty. Starting initial sync...");
                performInitialSync();
            } else {
                logger.info("Database already contains {} movies. Skipping initial sync.", movieCount);
            }
        } catch (Exception e) {
            logger.error("Error during initial sync check", e);
        }
    }

    /**
     * Perform initial sync to populate database with basic movie data
     */
    @Transactional
    public void performInitialSync() {
        if (!syncInProgress.compareAndSet(false, true)) {
            logger.warn("Sync already in progress, skipping initial sync");
            return;
        }

        try {
            logger.info("Starting initial movie sync...");

            for (String category : categories) {
                logger.info("Syncing {} movies...", category);

                // Get first few pages of each category for initial population
                for (int page = 1; page <= 5; page++) {
                    try {
                        List<Movie> movies = fetchMoviesFromAPI(category, page);
                        if (movies.isEmpty()) {
                            break;
                        }

                        int savedCount = saveMoviesIfNotExists(movies);
                        logger.info("Saved {} new movies from {} category, page {}",
                                savedCount, category, page);

                        // Respect API rate limits
                        Thread.sleep(250); // 250ms delay between requests

                    } catch (Exception e) {
                        logger.error("Error syncing {} movies, page {}: {}",
                                category, page, e.getMessage());
                    }
                }
            }

            long totalMovies = movieRepository.count();
            logger.info("Initial sync completed. Total movies in database: {}", totalMovies);

        } finally {
            syncInProgress.set(false);
        }
    }

    /**
     * Scheduled method that runs every 10 seconds to sync movies incrementally
     */
    @Scheduled(fixedDelay = 10000) // 10 seconds
    public void scheduledMovieSync() {
        if (!syncEnabled || !syncInProgress.compareAndSet(false, true)) {
            return;
        }

        try {
            performIncrementalSync();
        } finally {
            syncInProgress.set(false);
        }
    }

    /**
     * Perform incremental sync - continues from where we left off
     */
    private void performIncrementalSync() {
        try {
            String category = categories[currentCategoryIndex];

            logger.debug("Syncing {} movies, page {}", category, currentPage);

            List<Movie> movies = fetchMoviesFromAPI(category, currentPage);

            if (movies.isEmpty()) {
                // Move to next category
                moveToNextCategory();
                return;
            }

            int savedCount = saveMoviesIfNotExists(movies);

            if (savedCount > 0) {
                logger.info("Saved {} new movies from {} category, page {}",
                        savedCount, category, currentPage);
            }

            // Move to next page
            currentPage++;

            // If we've reached max pages for this category, move to next category
            if (currentPage > maxPages) {
                moveToNextCategory();
            }

        } catch (Exception e) {
            logger.error("Error during incremental sync: {}", e.getMessage());
            // On error, move to next page to avoid getting stuck
            currentPage++;
            if (currentPage > maxPages) {
                moveToNextCategory();
            }
        }
    }

    private void moveToNextCategory() {
        currentCategoryIndex = (currentCategoryIndex + 1) % categories.length;
        currentPage = 1;

        if (currentCategoryIndex == 0) {
            logger.info("Completed full cycle of all categories. Starting over...");
        }
    }

    private List<Movie> fetchMoviesFromAPI(String category, int page) throws IOException {
        switch (category.toLowerCase()) {
            case "popular":
                return tmdbClient.getPopularMovies(page).getResults();
            case "top_rated":
                return tmdbClient.getTopRatedMovies(page).getResults();
            case "now_playing":
                return tmdbClient.getNowPlayingMovies(page).getResults();
            case "upcoming":
                return tmdbClient.getUpcomingMovies(page).getResults();
            default:
                throw new IllegalArgumentException("Invalid category: " + category);
        }
    }

    /**
     * Save movies to database, but only if they don't already exist
     * Returns the number of movies actually saved
     */
    @Transactional
    public int saveMoviesIfNotExists(List<Movie> movies) {
        if (movies == null || movies.isEmpty()) {
            return 0;
        }

        // Get existing movie IDs from database
        Set<Integer> existingIds = movies.stream()
                .map(Movie::getId)
                .collect(Collectors.toSet());

        Set<Integer> dbExistingIds = movieRepository.findExistingIds(existingIds);

        // Filter out movies that already exist
        List<Movie> newMovies = movies.stream()
                .filter(movie -> !dbExistingIds.contains(movie.getId()))
                .collect(Collectors.toList());

        if (newMovies.isEmpty()) {
            return 0;
        }

        // Set sync timestamp
        LocalDateTime now = LocalDateTime.now();
        newMovies.forEach(movie -> movie.setSyncedAt(now));

        // Save in batches to avoid memory issues
        int saved = 0;
        for (int i = 0; i < newMovies.size(); i += batchSize) {
            int end = Math.min(i + batchSize, newMovies.size());
            List<Movie> batch = newMovies.subList(i, end);

            try {
                movieRepository.saveAll(batch);
                saved += batch.size();
            } catch (Exception e) {
                logger.error("Error saving batch of movies: {}", e.getMessage());
                // Try to save individually
                for (Movie movie : batch) {
                    try {
                        movieRepository.save(movie);
                        saved++;
                    } catch (Exception ex) {
                        logger.error("Error saving movie {}: {}", movie.getTitle(), ex.getMessage());
                    }
                }
            }
        }

        return saved;
    }

    /**
     * Manual trigger for full resync (useful for testing or admin operations)
     */
    public void triggerFullResync() {
        if (!syncInProgress.compareAndSet(false, true)) {
            throw new IllegalStateException("Sync already in progress");
        }

        try {
            logger.info("Starting manual full resync...");
            currentPage = 1;
            currentCategoryIndex = 0;
            performInitialSync();
        } finally {
            syncInProgress.set(false);
        }
    }

    /**
     * Get sync status information
     */
    public SyncStatus getSyncStatus() {
        return new SyncStatus(
                syncInProgress.get(),
                categories[currentCategoryIndex],
                currentPage,
                movieRepository.count(),
                syncEnabled
        );
    }

    /**
     * Stop/start sync service
     */
    public void setSyncEnabled(boolean enabled) {
        this.syncEnabled = enabled;
        logger.info("Movie sync service {}", enabled ? "enabled" : "disabled");
    }

    public static class SyncStatus {
        private final boolean inProgress;
        private final String currentCategory;
        private final int currentPage;
        private final long totalMovies;
        private final boolean enabled;

        public SyncStatus(boolean inProgress, String currentCategory, int currentPage,
                          long totalMovies, boolean enabled) {
            this.inProgress = inProgress;
            this.currentCategory = currentCategory;
            this.currentPage = currentPage;
            this.totalMovies = totalMovies;
            this.enabled = enabled;
        }

        // Getters
        public boolean isInProgress() { return inProgress; }
        public String getCurrentCategory() { return currentCategory; }
        public int getCurrentPage() { return currentPage; }
        public long getTotalMovies() { return totalMovies; }
        public boolean isEnabled() { return enabled; }

        @Override
        public String toString() {
            return String.format("SyncStatus{enabled=%s, inProgress=%s, category='%s', page=%d, totalMovies=%d}",
                    enabled, inProgress, currentCategory, currentPage, totalMovies);
        }
    }
}