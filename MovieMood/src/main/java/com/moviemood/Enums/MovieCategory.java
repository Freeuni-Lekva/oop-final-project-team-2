package com.moviemood.Enums;

public enum MovieCategory {
    POPULAR("popular"),
    TOP_RATED("top_rated"),
    UPCOMING("upcoming"),
    NOW_PLAYING("now_playing"),
    LATEST("latest");

    private final String endpoint;

    MovieCategory(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getEndpoint() {
        return endpoint;
    }
}

