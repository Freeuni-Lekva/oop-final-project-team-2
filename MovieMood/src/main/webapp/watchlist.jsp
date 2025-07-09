<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.moviemood.bean.User" %>
<%@ page import="com.moviemood.bean.Movie" %>
<%@ page import="java.util.List" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>My Watchlist - MovieMood</title>
    <link rel="stylesheet" href="assets/css/navbar.css">
    <link rel="stylesheet" type="text/css" href="assets/css/mainpage.css">
    <link rel="stylesheet" type="text/css" href="assets/css/watchlist.css">
</head>
<body data-current-page="1" data-total-pages="1">
<!-- Include Navigation Bar -->
<jsp:include page="WEB-INF/includes/navbar.jsp" />

<main class="main-content">
    <div class="container">
        <h1 class="hero-title">My <span class="highlight">Watchlist</span></h1>
        
        <%
            Boolean isDemoWatchlist = (Boolean) request.getAttribute("isDemoWatchlist");
            if (isDemoWatchlist != null && isDemoWatchlist) {
        %>
        <div class="demo-banner">
            [DEMO] Sample watchlist movies. Sign in to see your actual watchlist.
        </div>
        <%
            }
        %>

        <section class="popular-section">
            <h2 class="section-title">Your Saved Movies</h2>
            <div class="movies-grid">
                <%
                    List<Movie> watchlistMovies = (List<Movie>) request.getAttribute("watchlistMovies");
                    String posterBaseUrl = (String) request.getAttribute("POSTER_BASE");
                    if (watchlistMovies != null && !watchlistMovies.isEmpty()) {
                        for (Movie movie : watchlistMovies) {
                %>
                <a href="/movie/details?id=<%= movie.getId() %>" style="text-decoration: none; color: inherit;">
                    <div class="movie-card">
                        <div class="movie-poster">
                            <% if (movie.getPosterPath() != null && !movie.getPosterPath().isEmpty()) { %>
                                <% if (movie.getPosterPath().startsWith("assets/")) { %>
                                <img src="<%= movie.getPosterPath() %>" alt="<%= movie.getTitle() %>" />
                                <% } else { %>
                                <img src="<%= posterBaseUrl + movie.getPosterPath() %>" alt="<%= movie.getTitle() %>" />
                                <% } %>
                            <% } else { %>
                            <div class="movie-poster-fallback"></div>
                            <% } %>
                        </div>
                        <div class="movie-info">
                            <h3 class="movie-title"><%= movie.getTitle() %></h3>
                            <div class="movie-meta">
                                <div class="rating">
                                    <span class="stars">★★★★★</span>
                                </div>
                                <span class="year"><%= movie.getReleaseDate().toString().substring(0, 4) %></span>
                            </div>
                        </div>
                    </div>
                </a>
                <%
                        }
                    } else {
                %>
                <div class="empty-watchlist">
                    <h3>Your watchlist is empty</h3>
                    <p>Start adding movies to keep track of what you want to watch!</p>
                </div>
                <%
                    }
                %>
            </div>
        </section>
    </div>
</main>

</body>
</html> 