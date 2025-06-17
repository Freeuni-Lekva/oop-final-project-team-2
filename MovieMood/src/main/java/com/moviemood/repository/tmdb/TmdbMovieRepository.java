package com.moviemood.repository.tmdb;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.moviemood.Enums.MovieCategory;
import com.moviemood.bean.Movie;
import com.moviemood.config.Config;
import com.moviemood.repository.api.MovieRepository;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class TmdbMovieRepository implements MovieRepository {

    private final String BASE_URL;
    private final String apiKey;
    private final OkHttpClient httpClient;
    private final Gson gson;

    public TmdbMovieRepository() {
        BASE_URL= Config.get("tmdbBaseUrl");
        apiKey= Config.get("apiKey");
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
        this.gson = new GsonBuilder().create();
    }

    @Override
    public List<Movie> fetchByCategory(MovieCategory category, int page) {
        List<Movie> movies = new ArrayList<>();
        String url=String.format("%s/movie/"+category.getEndpoint()+"?api_key=%s&page=%d",BASE_URL,apiKey,page);
        return null;
    }

    @Override
    public List<Movie> search(String query, int page) {
        return List.of();
    }

    @Override
    public Optional<Movie> findById(int tmdbId) {
        return Optional.empty();
    }

    @Override
    public Optional<Movie> findByImdbId(String imdbId) {
        return Optional.empty();
    }

    @Override
    public List<Movie> fetchByGenre(int genreId, int page) {
        return List.of();
    }

    @Override
    public List<Movie> fetchByYear(int year, int page) {
        return List.of();
    }

    @Override
    public List<Movie> discover(String sortBy, int page) {
        return List.of();
    }

    @Override
    public List<Movie> fetchSimilar(int tmdbId) {
        return List.of();
    }

    @Override
    public List<Movie> fetchRecommendations(int tmdbId) {
        return List.of();
    }

    @Override
    public List<Movie> fetchRecentlyReleased() {
        return List.of();
    }

    @Override
    public Optional<Movie> fetchRandomPopular() {
        return Optional.empty();
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

}
