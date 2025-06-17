package com.moviemood.bean;

import com.google.gson.annotations.SerializedName;
import com.moviemood.config.Config;

import java.time.LocalDate;
import java.util.List;

public class Movie {

    private int id;
    private String title;
    private String overview;
    private String language;
    private boolean adult;
    private double popularity;
    private int runtime;

    @SerializedName("imdb_id")
    private String imdbId;

    @SerializedName("release_date")
    private LocalDate releaseDate;

    @SerializedName("poster_path")
    private String posterPath;

    @SerializedName("backdrop_path")
    private String backdropPath;

    private List<Genre> genres;

    public Movie() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public boolean isAdult() {
        return adult;
    }

    public void setAdult(boolean adult) {
        this.adult = adult;
    }

    public double getPopularity() {
        return popularity;
    }

    public void setPopularity(double popularity) {
        this.popularity = popularity;
    }

    public String getImdbId() {
        return imdbId;
    }

    public void setImdbId(String imdbId) {
        this.imdbId = imdbId;
    }

    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public void setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
    }

    public int getRuntime() {
        return runtime;
    }

    public void setRuntime(int runtime) {
        this.runtime = runtime;
    }

    public List<Genre> getGenres() {
        return genres;
    }

    public void setGenres(List<Genre> genres) {
        this.genres = genres;
    }

    //Helper functions

    public String getFullPosterUrl() {
        return posterPath != null ? Config.get("posterPathBase") + posterPath : null;
    }

    public String getFullBackdropUrl() {
        return backdropPath != null ? Config.get("backDropPathBase") + backdropPath : null;
    }

    public String getImdbUrl() {
        return imdbId != null ? Config.get("ImdbBase") + imdbId : null;
    }

    @Override
    public String toString() {
        return "Movie{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", overview='" + overview + '\'' +
                ", language='" + language + '\'' +
                ", adult=" + adult +
                ", popularity=" + popularity +
                ", imdbId='" + imdbId + '\'' +
                ", releaseDate=" + releaseDate +
                ", posterPath='" + posterPath + '\'' +
                ", backdropPath='" + backdropPath + '\'' +
                '}';
    }

}
