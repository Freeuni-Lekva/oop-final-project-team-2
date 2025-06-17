package com.example.repository;

import com.example.model.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {

    /**
     * Find movie by TMDb ID (not the database ID)
     */
    Optional<Movie> findByTmdbId(Integer tmdbId);

    /**
     * Check if movie exists by TMDb ID
     */
    boolean existsByTmdbId(Integer tmdbId);

    /**
     * Find existing TMDb IDs from a given set
     * This is crucial for preventing duplicates
     */
    @Query("SELECT m.tmdbId FROM Movie m WHERE m.tmdbId IN :ids")
    Set<Integer> findExistingIds(@Param("ids") Set<Integer> ids);

    /**
     * Find movies by category (useful for organizing data)
     */
    List<Movie> findByCategory(String category);

    /**
     * Find movies by title (case insensitive)
     */
    @Query("SELECT m FROM Movie m WHERE LOWER(m.title) LIKE LOWER(CONCAT('%', :title, '%'))")
    List<Movie> findByTitleContainingIgnoreCase(@Param("title") String title);

    /**
     * Find movies with rating above threshold
     */
    @Query("SELECT m FROM Movie m WHERE m.voteAverage >= :minRating ORDER BY m.voteAverage DESC")
    List<Movie> findByMinRating(@Param("minRating") double minRating);

    /**
     * Find recent movies (by release date)
     */
    @Query("SELECT m FROM Movie m WHERE m.releaseDate >= :fromDate ORDER BY m.releaseDate DESC")
    List<Movie> findRecentMovies(@Param("fromDate") String fromDate);

    /**
     * Count movies by category
     */
    @Query("SELECT COUNT(m) FROM Movie m WHERE m.category = :category")
    long countByCategory(@Param("category") String category);

    /**
     * Get movies that need detailed information (missing IMDB ID)
     */
    @Query("SELECT m FROM Movie m WHERE m.imdbId IS NULL OR m.imdbId = '' ORDER BY m.tmdbId ASC")
    List<Movie> findMoviesNeedingDetails();
}