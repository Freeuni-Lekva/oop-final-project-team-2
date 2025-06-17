package com.example.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MovieResponse {
    private int page;
    private List<Movie> results;

    @SerializedName("total_pages")
    private int totalPages;

    @SerializedName("total_results")
    private int totalResults;

    // Constructors
    public MovieResponse() {}

    // Getters and Setters
    public int getPage() { return page; }
    public void setPage(int page) { this.page = page; }

    public List<Movie> getResults() { return results; }
    public void setResults(List<Movie> results) { this.results = results; }

    public int getTotalPages() { return totalPages; }
    public void setTotalPages(int totalPages) { this.totalPages = totalPages; }

    public int getTotalResults() { return totalResults; }
    public void setTotalResults(int totalResults) { this.totalResults = totalResults; }

    @Override
    public String toString() {
        return "MovieResponse{" +
                "page=" + page +
                ", resultsCount=" + (results != null ? results.size() : 0) +
                ", totalPages=" + totalPages +
                ", totalResults=" + totalResults +
                '}';
    }
}