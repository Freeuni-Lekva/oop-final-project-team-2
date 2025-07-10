<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.moviemood.bean.Movie" %>
<%@ page import="java.util.List" %>

<!DOCTYPE html>
<html>
<head>
  <title>Choose Your Movies</title>
  <link rel="stylesheet" type="text/css" href="assets/css/mainpage.css">
  <style>
    .selectable-movie-card {
      position: relative;
      cursor: pointer;
      transition: all 0.3s ease;
    }

    .selectable-movie-card.selected {
      opacity: 0.7;
      transform: scale(0.95);
    }

    .selectable-movie-card.selected .movie-card {
      background: rgba(243, 156, 18, 0.3);
      border: 2px solid #f39c12;
    }

    .selectable-movie-card.selected::after {
      content: '✓';
      position: absolute;
      top: 10px;
      right: 10px;
      background: #f39c12;
      color: white;
      width: 30px;
      height: 30px;
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      font-weight: bold;
      z-index: 10;
    }

    .movie-checkbox {
      display: none;
    }
  </style>
</head>
<body>
<div class="container">
  <h1>Choose Your Favorite Movies</h1>

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

    <div id="selectionCount" style="color: white; font-size: 18px; margin: 20px 0;">Selected: 0 movies</div>

    <div style="text-align: center; margin: 30px 0;">
      <button type="submit" id="submitBtn" style="
        background: linear-gradient(135deg, #f39c12, #e67e22);
        color: white;
        padding: 15px 30px;
        border: none;
        border-radius: 25px;
        font-size: 16px;
        font-weight: 600;
        cursor: pointer;
        opacity: 0.5;
    " disabled>
        Save My Preferences
      </button>
    </div>

  </form>

</div>
<script>
  function updateSelection() {
    // Count checked boxes manually
    const allCheckboxes = document.querySelectorAll('.movie-checkbox');
    let count = 0;

    allCheckboxes.forEach(checkbox => {
      if (checkbox.checked) {
        count++;
      }
    });

    // Update counter
    const counterElement = document.getElementById('selectionCount');
    if (counterElement) {
      counterElement.textContent = 'Selected: ' + count + ' movies';
    }

    // Visual feedback
    document.querySelectorAll('.selectable-movie-card').forEach(card => {
      const checkbox = card.querySelector('.movie-checkbox');
      if (checkbox.checked) {
        card.classList.add('selected');
      } else {
        card.classList.remove('selected');
      }
    });

    const submitBtn = document.getElementById('submitBtn');
    if (count >= 3 && count <= 4) {
      submitBtn.disabled = false;
      submitBtn.style.opacity = '1';
    } else {
      submitBtn.disabled = true;
      submitBtn.style.opacity = '0.5';
    }
  }

  document.addEventListener('DOMContentLoaded', function() {
    document.querySelectorAll('.movie-checkbox').forEach(checkbox => {
      checkbox.addEventListener('change', function() {
        const selectedCount = document.querySelectorAll('.movie-checkbox:checked').length;

        if (selectedCount > 4) {
          this.checked = false;
          alert('You can only select up to 4 movies!');
          return;
        }

        updateSelection();
      });
    });
  });
</script>
</body>
</html>