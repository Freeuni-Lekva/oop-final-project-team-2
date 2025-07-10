<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.moviemood.bean.Movie" %>
<%@ page import="java.util.List" %>

<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Choose Your Movies - MovieMood</title>
  <link rel="stylesheet" href="assets/css/movie-preferences.css?v=2">
</head>
<body>
<div class="container">
  <h1>Choose Your Favorite Movies</h1>
  <p class="subtitle">Select 3-4 movies that match your taste to get personalized recommendations</p>

  <form id="preferencesForm" method="post" action="/movie-preferences">
    <div class="movies-grid">
      <%
        List<Movie> movies = (List<Movie>) request.getAttribute("movies");
        String posterBaseUrl = (String) request.getAttribute("POSTER_BASE");
        if (movies != null) {
          for (Movie movie : movies) {
      %>
      <label class="selectable-movie-card" for="movie_<%= movie.getId() %>">
        <input type="checkbox"
               id="movie_<%= movie.getId() %>"
               name="selectedMovies"
               value="<%= movie.getId() %>"
               class="movie-checkbox">

        <div class="movie-card">
          <div class="movie-poster">
            <% if (movie.getPosterPath() != null && !movie.getPosterPath().isEmpty()) { %>
            <img src="<%= posterBaseUrl + movie.getPosterPath() %>" alt="<%= movie.getTitle() %>" />
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
              <% if (movie.getReleaseDate() != null) { %>
              <span class="year"><%= movie.getReleaseDate().toString().substring(0, 4) %></span>
              <% } %>
            </div>
          </div>
        </div>
      </label>
      <%
          }
        }
      %>
    </div>

    <div class="selection-info">
      <div class="selection-count" id="selectionCount">Selected: 0 movies</div>
      <div class="selection-hint">Choose between 3-4 movies to continue</div>
    </div>

    <div class="submit-container">
      <button type="submit" id="submitBtn" class="submit-btn" disabled>
        Save My Preferences
      </button>
    </div>
  </form>
</div>

<script src="assets/js/movie-preferences.js?v=1"></script>
</body>
</html>