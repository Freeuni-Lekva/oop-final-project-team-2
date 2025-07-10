<%--
  Created by IntelliJ IDEA.
  User: Nikoloz
  Date: 7/6/2025
  Time: 9:38 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.moviemood.bean.Movie" %>
<%@ page import="com.moviemood.bean.User" %>
<%@ page import="java.util.List" %>
<%@ page import="com.moviemood.bean.Genre" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>MovieMood - <%= ((Movie) request.getAttribute("movie")).getTitle() %></title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/css/navbar.css">
    <link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/assets/css/moviedetails.css">
</head>
<body>
<script src="<%= request.getContextPath() %>/assets/js/movie-details.js"></script>
<%
    Movie movie = (Movie) request.getAttribute("movie");
    String backDropBaseURL = (String) request.getAttribute("backDropPathBaseURL");
    String posterBaseUrl = (String) request.getAttribute("POSTER_BASE");
%>

<!-- Include Navigation Bar -->
<jsp:include page="WEB-INF/includes/navbar.jsp" />

<section class="hero-section">
    <div class="backdrop-container">
        <% if (movie.getBackdropPath() != null && !movie.getBackdropPath().isEmpty()) { %>
        <img src="<%= backDropBaseURL + movie.getBackdropPath() %>" alt="<%= movie.getTitle() %>" class="backdrop-image">
        <% } %>
        <div class="backdrop-overlay"></div>
    </div>
    <div class="container">
        <div class="hero-content">
            <div class="movie-poster-large">
                <% if (movie.getPosterPath() != null && !movie.getPosterPath().isEmpty()) { %>
                <img src="<%= posterBaseUrl + movie.getPosterPath() %>" alt="<%= movie.getTitle() %>">
                <% } else { %>
                <div style="width: 100%; height: 100%; background: linear-gradient(135deg, #2c3e50, #3498db); display: flex; align-items: center; justify-content: center;">
                    <span style="font-size: 24px; color: white;">No Poster</span>
                </div>
                <% } %>
            </div>
            <div class="movie-info-main">
                <h1 class="movie-title-main"><%= movie.getTitle() %></h1>
<%--                <% if (movie.getTagline() != null && !movie.getTagline().isEmpty()) { %>--%>
<%--                <p class="movie-tagline">"<%= movie.getTagline() %>"</p>--%>
<%--                <% } %>--%>

                <div class="movie-meta-main">
                    <div class="meta-item">
                        <span class="meta-label">Release Date:</span>
                        <span class="meta-value"><%= movie.getReleaseDate() %></span>
                    </div>
                    <div class="meta-item">
                        <span class="meta-label">Runtime:</span>
                        <span class="meta-value"><%= movie.getRuntime() %> minutes</span>
                    </div>
                    <div class="meta-item rating-display">
                        <span class="stars-display">★★★★☆</span>
                        <span class="rating-score">8.5</span>
                    </div>
                </div>

                <div class="genres">
                    <% for(int i=0; i<movie.getGenres().size(); i++){ %>
                        <span class="genre-tag"><%= movie.getGenres().get(i).getName()%></span>
                    <%}%>
                </div>

                <div class="overview">
                    <p><%= movie.getOverview() %></p>
                </div>

                <div class="action-buttons">
                    <%
                        String trailerKey = (String) request.getAttribute("trailerKey");
                        if (trailerKey != null) {
                    %>
                    <a class="btn btn-primary" href="https://www.youtube.com/watch?v=<%= trailerKey %>" target="_blank">
                        ▶ Watch Trailer
                    </a>
                    <%
                    } else {
                    %>
                    <button class="btn btn-secondary" disabled>Trailer Unavailable</button>
                    <%
                        }
                    %>

                    <button class="btn btn-secondary">+ Add to Watchlist</button>
                    <button class="btn btn-secondary">♡ Add to Favorites</button>
                </div>
            </div>
        </div>
    </div>
</section>

<main class="container">
    <section class="details-section">
        <h2 class="section-title">Movie Details</h2>
        <div class="details-grid">
<%--            <div class="detail-card">--%>
<%--                <h3>Production Information</h3>--%>
<%--                <p><strong>Budget:</strong> $<%= movie.getBudget() %></p>--%>
<%--                <p><strong>Revenue:</strong> $<%= movie.getRevenue() %></p>--%>
<%--                <p><strong>Status:</strong> <%= movie.getStatus() %></p>--%>
<%--                <p><strong>Original Language:</strong> <%= movie.getOriginalLanguage() %></p>--%>
<%--            </div>--%>
            <div class="detail-card">
                <h3>Ratings & Popularity</h3>
<%--                <p><strong>TMDB Rating:</strong> <%= movie.getVoteAverage() %>/10</p>--%>
<%--                <p><strong>Vote Count:</strong> <%= movie.getVoteCount() %></p>--%>
                <p><strong>Popularity Score:</strong> <%= movie.getPopularity() %></p>
            </div>
            <div class="detail-card">
                <h3>Additional Information</h3>
                <p><strong>Original Title:</strong> <%= movie.getTitle() %></p>
                <p><strong>Adult Content:</strong> <%= movie.isAdult() ? "Yes" : "No" %></p>
<%--                <p><strong>Video Available:</strong> <%= movie.isVideo() ? "Yes" : "No" %></p>--%>
            </div>
        </div>
    </section>

    <section class="rating-section">
        <h2 class="section-title">Rate This Movie</h2>
        <div class="rating-container">
            <div class="rating-stars" id="ratingStars">
                <span class="star" data-rating="1">★</span>
                <span class="star" data-rating="2">★</span>
                <span class="star" data-rating="3">★</span>
                <span class="star" data-rating="4">★</span>
                <span class="star" data-rating="5">★</span>
            </div>
            <span class="rating-text" id="ratingText">Click a star to rate</span>
            <button class="submit-rating" id="submitRating">Submit Rating</button>
        </div>
    </section>

    <section class="reviews-section">
        <h2 class="section-title">Reviews</h2>

        <div class="review-form">
            <h3 style="margin-bottom: 20px; color: #f39c12;">Write a Review</h3>
            <form id="reviewForm">
                <div class="form-group">
                    <textarea id="reviewText" name="reviewText" placeholder="Share your thoughts about this movie..." required></textarea>
                </div>
                <button type="submit" class="btn btn-primary">Submit Review</button>
            </form>
        </div>

        <div class="reviews-list" id="reviewsList">
            <!-- Sample Reviews -->
            <div class="review-item">
                <div class="review-header">
                    <div class="reviewer-info">
                        <div class="reviewer-avatar">JD</div>
                        <div>
                            <div class="reviewer-name">John Doe</div>
                            <div class="review-date">July 5, 2025</div>
                        </div>
                    </div>
                    <div class="review-rating">★★★★☆</div>
                </div>
                <div class="review-text">
                    An absolutely captivating film that keeps you on the edge of your seat from start to finish. The cinematography is breathtaking and the performances are outstanding. Highly recommend this movie to anyone looking for a great cinematic experience.
                </div>
            </div>

            <div class="review-item">
                <div class="review-header">
                    <div class="reviewer-info">
                        <div class="reviewer-avatar">MS</div>
                        <div>
                            <div class="reviewer-name">Movie Enthusiast</div>
                            <div class="review-date">July 4, 2025</div>
                        </div>
                    </div>
                    <div class="review-rating">★★★★★</div>
                </div>
                <div class="review-text">
                    This movie exceeded all my expectations! The plot is engaging, the characters are well-developed, and the ending is satisfying. A must-watch for fans of the genre.
                </div>
            </div>
        </div>
    </section>
</main>
</body>
</html>