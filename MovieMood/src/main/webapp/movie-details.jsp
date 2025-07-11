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
<%@ page import="com.moviemood.bean.UserList" %>
<%@ page import="com.moviemood.dao.UserDao" %>
<%@ page import="com.moviemood.bean.MovieReview" %>
<%@ page import="com.moviemood.dao.MovieReviewsDao" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>MovieMood - <%= ((Movie) request.getAttribute("movie")).getTitle() %></title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/css/navbar.css">
    <link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/assets/css/moviedetails.css">
    <style>
        /* Add to List dropdown styles */
        .add-to-list-container {
            position: relative;
            display: inline-block;
        }

        .list-dropdown {
            position: fixed;
            background: rgba(0, 0, 0, 0.95);
            border: 2px solid rgba(255, 255, 255, 0.3);
            border-radius: 8px;
            min-width: 200px;
            max-height: 300px;
            overflow-y: auto;
            z-index: 9999;
            box-shadow: 0 8px 32px rgba(0, 0, 0, 0.6);
            margin-top: 5px;
        }

        .list-option {
            padding: 12px 16px;
            cursor: pointer;
            border-bottom: 1px solid rgba(255, 255, 255, 0.1);
            display: flex;
            justify-content: space-between;
            align-items: center;
            transition: all 0.3s ease;
        }

        .list-option:hover {
            background: rgba(243, 156, 18, 0.2);
        }

        .list-option:last-child {
            border-bottom: none;
        }

        .list-name {
            color: rgba(255, 255, 255, 0.9);
            font-weight: 500;
        }

        .list-status {
            color: #f39c12;
            font-weight: bold;
            font-size: 16px;
        }

        .list-option[data-in-list="true"] .list-status {
            color: #27ae60;
        }

        .no-lists-message {
            padding: 16px;
            text-align: center;
            color: rgba(255, 255, 255, 0.7);
        }

        .create-list-link {
            color: #f39c12;
            text-decoration: none;
            display: block;
            margin-top: 8px;
            font-weight: 500;
        }

        .create-list-link:hover {
            color: #e67e22;
            text-decoration: underline;
        }
    </style>
</head>
<body>
<script src="<%= request.getContextPath() %>/assets/js/movie-details.js"></script>
<%
    Movie movie = (Movie) request.getAttribute("movie");
    int movieId=movie.getId();
    MovieReviewsDao movieReviewsDao=(MovieReviewsDao)request.getServletContext().getAttribute("reviewsDao");
    List<MovieReview> reviews=movieReviewsDao.getMovieReviews(movieId);
    String backDropBaseURL = (String) request.getAttribute("backDropPathBaseURL");
    String posterBaseUrl = (String) request.getAttribute("POSTER_BASE");
    User user=(User) request.getSession().getAttribute("user");
    Integer userId = null;

    if (user != null) {
        userId=user.getId();
    }
    Boolean isInWatchlist = (Boolean) request.getAttribute("isInWatchlist");
    Boolean isInFavorites = (Boolean) request.getAttribute("isInFavorites");

    // Default to false if null
    if (isInWatchlist == null) isInWatchlist = false;
    if (isInFavorites == null) isInFavorites = false;
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
                    <% } %>
                    <% if (user != null) { %>
                        <button id="watchlistBtn" class="btn btn-secondary" data-movie-id="<%= movie.getId() %>" data-in-watchlist="<%= isInWatchlist %>">
                            <%= isInWatchlist ? "✓ In Watchlist" : "+ Add to Watchlist" %>
                        </button>
                        <button id="favoritesBtn" class="btn btn-secondary" data-movie-id="<%= movie.getId() %>" data-in-favorites="<%= isInFavorites %>" title="<%= isInFavorites ? "Remove from Favorites" : "Add to Favorites" %>">
                            <%= isInFavorites ? "♥" : "♡" %>
                        </button>

                        <!-- Add to List Button with Dropdown -->
                        <div class="add-to-list-container" style="position: relative; display: inline-block;">
                            <button id="addToListBtn" class="btn btn-secondary" data-movie-id="<%= movie.getId() %>">
                                📝 Add to List
                            </button>
                            <div id="listDropdown" class="list-dropdown" style="display: none;">
                                <%
                                    List<UserList> userLists = (List<UserList>) request.getAttribute("userLists");
                                    if (userLists != null && !userLists.isEmpty()) {
                                        for (UserList list : userLists) {
                                %>
                                <div class="list-option" data-list-id="<%= list.getId() %>" data-in-list="<%= list.isContainsCurrentMovie() %>">
                                    <span class="list-name"><%= list.getName() %></span>
                                    <span class="list-status"><%= list.isContainsCurrentMovie() ? "✓" : "+" %></span>
                                </div>
                                <%
                                        }
                                    } else {
                                %>
                                <div class="no-lists-message">
                                    <span>No lists found</span>
                                    <a href="/lists" class="create-list-link">Create your first list</a>
                                </div>
                                <%
                                    }
                                %>
                            </div>
                        </div>
                    <% } %>
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

    <% if (user != null) { %>
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
    <% } %>


    <section class="reviews-section">
        <h2 class="section-title">Reviews</h2>

        <% if (user!=null) { %>
        <div class="review-form">
            <h3 style="margin-bottom: 20px; color: #f39c12;">Write a Review</h3>
            <form id="reviewForm" action="<%= request.getContextPath() %>/add-review" method="POST">
            <input type="hidden" name="movieId" value="<%=movieId%>" />
                <input type="hidden" name="userId" value="<%=userId%>" />

                <div class="form-group">
                    <textarea id="reviewText" name="reviewText" placeholder="Share your thoughts about this movie..." required></textarea>
                </div>

                <button type="submit" class="btn btn-primary">Submit Review</button>
            </form>

        </div>
        <% } %>

        <div class="reviews-list" id="reviewsList">
            <% if (reviews == null || reviews.isEmpty()) { %>
            <p style="color: gray; font-style: italic;">No reviews yet. Be the first to write one!</p>
            <% } else {
                for (MovieReview review : reviews) {
                    UserDao userDao = (UserDao) application.getAttribute("userDao");
                    String reviewerName = userDao.getUserById(review.getUserId()).getUsername();
                    String initials = reviewerName.trim().isEmpty() ? "?" : reviewerName.substring(0, 1).toUpperCase();
            %>
            <div class="review-item">
                <div class="review-header">
                    <div class="reviewer-info">
                        <div class="reviewer-avatar"><%= initials %></div>
                        <div>
                            <div class="reviewer-name"><%= reviewerName %></div>
                            <div class="review-date"><%= review.getFormattedDate() %></div>
                        </div>
                    </div>
                    <div class="review-rating">★★★★☆</div> <!-- You can customize this if you add rating support -->
                </div>
                <div class="review-text"><%= review.getReviewText() %></div>
            </div>
            <%
                    } // end for
                } // end else
            %>
        </div>
    </section>
</main>
</body>
</html>