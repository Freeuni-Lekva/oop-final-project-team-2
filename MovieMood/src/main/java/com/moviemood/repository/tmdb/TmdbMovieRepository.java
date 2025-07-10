package com.moviemood.repository.tmdb;

import com.google.gson.*;
import com.moviemood.Enums.MovieCategory;
import com.moviemood.bean.Movie;
import com.moviemood.config.Config;
import com.moviemood.config.LocalDateAdapter;
import com.moviemood.repository.api.MovieRepository;
import com.moviemood.repository.api.model.MovieResponse;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class TmdbMovieRepository implements MovieRepository {

    private static TmdbMovieRepository instance;
    private final String BASE_URL;
    private final String apiKey;
    private final OkHttpClient httpClient;
    private final Gson gson;

    private TmdbMovieRepository() {
        BASE_URL= Config.get("tmdbBaseUrl");
        apiKey= Config.get("apiKey");
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
        this.gson = new GsonBuilder().registerTypeAdapter(LocalDate.class,new LocalDateAdapter())
                .create();
    }

    public static synchronized TmdbMovieRepository getInstance() {
        if (instance == null) {
            instance = new TmdbMovieRepository();
        }
        return instance;
    }

    @Override
    public List<Movie> fetchAll(int page) throws IOException {
        String url = String.format("%s/discover/movie?api_key=%s&page=%d", BASE_URL, apiKey, page);
        MovieResponse response = executeRequest(url, MovieResponse.class);
        return response.getResults();
    }

    @Override
    public List<Movie> fetchByCategory(MovieCategory category, int page) throws IOException {
        String url=String.format("%s/movie/"+category.getEndpoint()+"?api_key=%s&page=%d",BASE_URL,apiKey,page);
        MovieResponse response=executeRequest(url,MovieResponse.class);
        return response.getResults();
    }

    @Override
    public List<Movie> search(String query, int page) throws IOException {
        String url = String.format("%s/search/movie?api_key=%s&query=%s&page=%d",
                BASE_URL, apiKey, URLEncoder.encode(query, StandardCharsets.UTF_8), page);
        MovieResponse response = executeRequest(url, MovieResponse.class);
        return response.getResults();
    }

    @Override
    public List<Movie> discoverWithFilters(String genre, String year, String runtime, int page) throws IOException {
        StringBuilder url = new StringBuilder(BASE_URL + "/discover/movie?api_key=" + apiKey + "&page=" + page);

        if (genre != null && !genre.isEmpty()) {
            url.append("&with_genres=").append(genre);
        }

        if (year != null && !year.isEmpty()) {
            url.append("&primary_release_year=").append(year);
        }

        if (runtime != null && !runtime.isEmpty()) {
            switch (runtime) {
                case "short":
                    url.append("&with_runtime.lte=89");
                    break;
                case "medium":
                    url.append("&with_runtime.gte=90&with_runtime.lte=120");
                    break;
                case "long":
                    url.append("&with_runtime.gte=121");
                    break;
            }
        }

        MovieResponse response = executeRequest(url.toString(), MovieResponse.class);
        return response.getResults();
    }

    @Override
    public Optional<Movie> findById(int tmdbId) throws IOException {
        String url = String.format("%s/movie/%d?api_key=%s", BASE_URL, tmdbId, apiKey);
        Movie movie = executeRequest(url, Movie.class);
        return Optional.ofNullable(movie);
    }

    @Override
    public List<Movie> fetchByGenre(int genreId, int page) throws IOException {
        String url = String.format("%s/discover/movie?api_key=%s&with_genres=%d&page=%d",
                BASE_URL, apiKey, genreId, page);
        MovieResponse response = executeRequest(url, MovieResponse.class);
        return response.getResults();
    }

    @Override
    public List<Movie> fetchByYear(int year, int page) throws IOException {
        String url = String.format("%s/discover/movie?api_key=%s&primary_release_year=%d&page=%d",
                BASE_URL, apiKey, year, page);
        MovieResponse response = executeRequest(url, MovieResponse.class);
        return response.getResults();
    }

    @Override
    public List<Movie> discover(String sortBy, int page) throws IOException {
        String url = String.format("%s/discover/movie?api_key=%s&sort_by=%s&page=%d", BASE_URL, apiKey, sortBy, page);
        MovieResponse response = executeRequest(url, MovieResponse.class);
        return response.getResults();
    }


    @Override
    public List<Movie> fetchSimilar(int tmdbId) throws IOException {
        String url = String.format("%s/movie/%d/similar?api_key=%s&page=1", BASE_URL, tmdbId, apiKey);
        MovieResponse response = executeRequest(url, MovieResponse.class);
        return response.getResults();
    }


    @Override
    public List<Movie> fetchRecommendations(int tmdbId) throws IOException {
        String url = String.format("%s/movie/%d/recommendations?api_key=%s&page=1", BASE_URL, tmdbId, apiKey);
        MovieResponse response = executeRequest(url, MovieResponse.class);
        return response.getResults();
    }

    @Override
    public Optional<Movie> fetchRandomPopular() {
        return Optional.empty();
    }

    @Override
    public Optional<String> fetchYoutubeTrailerKey(int movieId) throws IOException {
        String url = BASE_URL + "/movie/" + movieId + "/videos?api_key=" + apiKey;

        Request request = new Request.Builder().url(url).build();
        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) return Optional.empty();

            String responseBody = response.body().string();
            JsonObject json = JsonParser.parseString(responseBody).getAsJsonObject();
            JsonArray results = json.getAsJsonArray("results");

            for (JsonElement element : results) {
                JsonObject video = element.getAsJsonObject();
                String site = video.get("site").getAsString();
                String type = video.get("type").getAsString();
                if ("YouTube".equalsIgnoreCase(site) && "Trailer".equalsIgnoreCase(type)) {
                    return Optional.of(video.get("key").getAsString());
                }
            }
        }
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
