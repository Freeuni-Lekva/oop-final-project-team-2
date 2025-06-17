-- Create movies table
CREATE TABLE movies (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        tmdb_id INT UNIQUE NOT NULL,
                        title VARCHAR(500) NOT NULL,
                        overview TEXT,
                        release_date VARCHAR(10),
                        poster_path VARCHAR(200),
                        backdrop_path VARCHAR(200),
                        vote_average DECIMAL(3,1),
                        vote_count INT,
                        is_adult BOOLEAN DEFAULT FALSE,
                        language VARCHAR(10),
                        popularity DECIMAL(10,3),
                        imdb_id VARCHAR(20),
                        runtime INT,
                        category VARCHAR(50),
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                        synced_at TIMESTAMP NULL DEFAULT NULL,
                        detailed_synced_at TIMESTAMP NULL DEFAULT NULL
);

-- Indexes
CREATE INDEX idx_movies_tmdb_id ON movies(tmdb_id);
CREATE INDEX idx_movies_title ON movies(title);
CREATE INDEX idx_movies_category ON movies(category);
CREATE INDEX idx_movies_release_date ON movies(release_date);
CREATE INDEX idx_movies_vote_average ON movies(vote_average);
CREATE INDEX idx_movies_synced_at ON movies(synced_at);

-- Create movie_genre_ids table
CREATE TABLE movie_genre_ids (
                                 id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                 movie_id BIGINT NOT NULL,
                                 genre_id INT NOT NULL,
                                 FOREIGN KEY (movie_id) REFERENCES movies(id) ON DELETE CASCADE
);

CREATE INDEX idx_movie_genre_ids_movie_id ON movie_genre_ids(movie_id);
CREATE INDEX idx_movie_genre_ids_genre_id ON movie_genre_ids(genre_id);

-- Create movie_genres table
CREATE TABLE movie_genres (
                              id BIGINT AUTO_INCREMENT PRIMARY KEY,
                              movie_id BIGINT NOT NULL,
                              genre_id INT NOT NULL,
                              genre_name VARCHAR(100) NOT NULL,
                              FOREIGN KEY (movie_id) REFERENCES movies(id) ON DELETE CASCADE
);

CREATE INDEX idx_movie_genres_movie_id ON movie_genres(movie_id);
CREATE INDEX idx_movie_genres_genre_id ON movie_genres(genre_id);

-- Create sync_metadata table
CREATE TABLE sync_metadata (
                               id BIGINT AUTO_INCREMENT PRIMARY KEY,
                               `key` VARCHAR(100) UNIQUE NOT NULL,
                               value TEXT,
                               created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                               updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Insert initial sync status
INSERT INTO sync_metadata (`key`, value) VALUES ('initial_sync_completed', 'false');
INSERT INTO sync_metadata (`key`, value) VALUES ('last_sync_category', 'popular');
INSERT INTO sync_metadata (`key`, value) VALUES ('last_sync_page', '1');

-- Create movie_stats view
CREATE VIEW movie_stats AS
SELECT
    category,
    COUNT(*) AS movie_count,
    AVG(vote_average) AS avg_rating,
    MAX(synced_at) AS last_synced,
    COUNT(CASE WHEN detailed_synced_at IS NOT NULL THEN 1 END) AS detailed_count
FROM movies
WHERE category IS NOT NULL
GROUP BY category;

-- Add table comments (MySQL-style)
ALTER TABLE movies COMMENT = 'Main table storing movie information from TMDb API';
