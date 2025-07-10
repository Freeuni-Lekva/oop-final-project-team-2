<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.moviemood.bean.User" %>
<%@ page import="com.moviemood.bean.Movie" %>
<%@ page import="java.util.List" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>My Favorites - MovieMood</title>
    <link rel="stylesheet" href="assets/css/navbar.css">
    <link rel="stylesheet" type="text/css" href="assets/css/mainpage.css">
    <link rel="stylesheet" type="text/css" href="assets/css/watchlist.css">
    <style>
        .movie-card-container {
            position: relative;
        }
        
        .remove-btn {
            position: absolute;
            top: 10px;
            right: 10px;
            background: rgba(220, 53, 69, 0.9);
            color: white;
            border: none;
            border-radius: 50%;
            width: 30px;
            height: 30px;
            font-size: 16px;
            cursor: pointer;
            opacity: 0;
            transition: opacity 0.3s ease;
            z-index: 10;
            display: flex;
            align-items: center;
            justify-content: center;
        }
        
        .remove-btn:hover {
            background: rgba(220, 53, 69, 1);
        }
        
        .movie-card-container:hover .remove-btn {
            opacity: 1;
        }
    </style>
</head>
<body data-current-page="1" data-total-pages="1">
<!-- Include Navigation Bar -->
<jsp:include page="WEB-INF/includes/navbar.jsp" />

<main class="main-content">
    <div class="container">
        <h1 class="hero-title">My <span class="highlight">Favorites</span></h1>
        
        <%
            Boolean isDemoFavorites = (Boolean) request.getAttribute("isDemoFavorites");
            if (isDemoFavorites != null && isDemoFavorites) {
        %>
        <div class="demo-banner">
            [DEMO] Sample favorite movies. Sign in to see your actual favorites.
        </div>
        <%
            }
        %>

        <section class="popular-section">
            <h2 class="section-title">Your Favorite Movies</h2>
            <div class="movies-grid">
                <%
                    List<Movie> favoritesMovies = (List<Movie>) request.getAttribute("favoritesMovies");
                    String posterBaseUrl = (String) request.getAttribute("POSTER_BASE");
                    if (favoritesMovies != null && !favoritesMovies.isEmpty()) {
                        for (Movie movie : favoritesMovies) {
                %>
                <div class="movie-card-container">
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
                    <button class="remove-btn" data-movie-id="<%= movie.getId() %>" title="Remove from Favorites">♥</button>
                </div>
                <%
                        }
                    } else {
                %>
                <div class="empty-watchlist">
                    <h3>Your favorites is empty</h3>
                    <p>Start adding movies to keep track of your all-time favorites!</p>
                </div>
                <%
                    }
                %>
            </div>
        </section>
    </div>
</main>

<script>
    // Remove button functionality
    document.querySelectorAll('.remove-btn').forEach(btn => {
        btn.addEventListener('click', function(e) {
            e.preventDefault();
            e.stopPropagation();
            
            const movieId = this.getAttribute('data-movie-id');
            const movieContainer = this.closest('.movie-card-container');
            
            fetch('/favorites/action', {
                method: 'POST',
                headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                body: 'action=remove&movieId=' + movieId
            })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    // Remove the movie from the page with animation
                    movieContainer.style.transition = 'opacity 0.3s ease, transform 0.3s ease';
                    movieContainer.style.opacity = '0';
                    movieContainer.style.transform = 'scale(0.8)';
                    
                    setTimeout(() => {
                        movieContainer.remove();
                        
                        // Check if favorites is now empty
                        const remainingMovies = document.querySelectorAll('.movie-card-container');
                        if (remainingMovies.length === 0) {
                            document.querySelector('.movies-grid').innerHTML = 
                                '<div class="empty-watchlist">' +
                                    '<h3>Your favorites is empty</h3>' +
                                    '<p>Start adding movies to keep track of your all-time favorites!</p>' +
                                '</div>';
                        }
                    }, 300);
                }
            });
        });
    });
</script>

</body>
</html> 