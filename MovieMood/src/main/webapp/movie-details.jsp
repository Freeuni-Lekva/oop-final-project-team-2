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
<%
    Movie movie = (Movie) request.getAttribute("movie");
    String backDropBaseURL = (String) request.getAttribute("backDropPathBaseURL");
    String posterBaseUrl = (String) request.getAttribute("POSTER_BASE");
    User currentUser = (User) request.getAttribute("currentUser");
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
                    <%
                        }
                    %>

                    <% if (currentUser != null) { %>
                        <button id="watchlistBtn" class="btn btn-secondary" data-movie-id="<%= movie.getId() %>" data-in-watchlist="<%= isInWatchlist %>">
                            <%= isInWatchlist ? "✓ In Watchlist" : "+ Add to Watchlist" %>
                        </button>
                        <button id="favoritesBtn" class="btn btn-secondary" data-movie-id="<%= movie.getId() %>" data-in-favorites="<%= isInFavorites %>" title="<%= isInFavorites ? "Remove from Favorites" : "Add to Favorites" %>">
                            <%= isInFavorites ? "♥" : "♡" %>
                        </button>
                    <% } else { %>
                        <button id="watchlistBtn" class="btn btn-secondary" title="Please log in to add to watchlist">+ Add to Watchlist</button>
                        <button id="favoritesBtn" class="btn btn-secondary" title="Please log in to add to favorites">♡ Add to Favorites</button>
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

<script>
    // Rating System
    let currentRating = 0;
    const stars = document.querySelectorAll('.star');
    const ratingText = document.getElementById('ratingText');
    const submitRatingBtn = document.getElementById('submitRating');

    stars.forEach((star, index) => {
        star.addEventListener('click', () => {
            currentRating = index + 1;
            updateStars();
            updateRatingText();
        });

        star.addEventListener('mouseenter', () => {
            highlightStars(index + 1);
        });
    });

    document.getElementById('ratingStars').addEventListener('mouseleave', () => {
        updateStars();
    });

    function updateStars() {
        stars.forEach((star, index) => {
            star.classList.toggle('active', index < currentRating);
        });
    }

    function highlightStars(rating) {
        stars.forEach((star, index) => {
            star.classList.toggle('active', index < rating);
        });
    }

    function updateRatingText() {
        const ratingTexts = ['', 'Poor', 'Fair', 'Good', 'Very Good', 'Excellent'];
        ratingText.textContent = ratingTexts[currentRating] || 'Click a star to rate';
    }

    submitRatingBtn.addEventListener('click', () => {
        if (currentRating > 0) {
            alert(`Thank you for rating this movie ${currentRating} stars!`);
            // Here you would typically send the rating to your server
        } else {
            alert('Please select a rating first!');
        }
    });

    // Review Form
    document.getElementById('reviewForm').addEventListener('submit', (e) => {
        e.preventDefault();
        const name = document.getElementById('reviewerName').value;
        const review = document.getElementById('reviewText').value;

        if (name && review) {
            addReview(name, review);
            document.getElementById('reviewForm').reset();
        }
    });

    function addReview(name, reviewText) {
        const reviewsList = document.getElementById('reviewsList');
        const reviewItem = document.createElement('div');
        reviewItem.className = 'review-item';

        const initials = name.split(' ').map(n => n[0]).join('').toUpperCase();
        const currentDate = new Date().toLocaleDateString('en-US', {
            year: 'numeric',
            month: 'long',
            day: 'numeric'
        });

        reviewItem.innerHTML = `
                <div class="review-header">
                    <div class="reviewer-info">
                        <div class="reviewer-avatar">${initials}</div>
                        <div>
                            <div class="reviewer-name">${name}</div>
                            <div class="review-date">${currentDate}</div>
                        </div>
                    </div>
                    <div class="review-rating">★★★★☆</div>
                </div>
                <div class="review-text">${reviewText}</div>
            `;

        reviewsList.insertBefore(reviewItem, reviewsList.firstChild);

        // Add animation
        reviewItem.style.opacity = '0';
        reviewItem.style.transform = 'translateY(20px)';
        setTimeout(() => {
            reviewItem.style.transition = 'all 0.3s ease';
            reviewItem.style.opacity = '1';
            reviewItem.style.transform = 'translateY(0)';
        }, 100);
    }

    // Smooth scrolling for navigation
    document.querySelectorAll('a[href^="#"]').forEach(anchor => {
        anchor.addEventListener('click', function (e) {
            e.preventDefault();
            document.querySelector(this.getAttribute('href')).scrollIntoView({
                behavior: 'smooth'
            });
        });
    });

    // Watchlist and Favorites buttons
    <% if (currentUser != null) { %>
    
    // Set initial favorited class based on current state
    const favBtn = document.getElementById('favoritesBtn');
    console.log('Initial favorites state:', favBtn.getAttribute('data-in-favorites'));
    if (favBtn.getAttribute('data-in-favorites') === 'true') {
        favBtn.classList.add('favorited');
        // Force red color with inline styles to override everything
        favBtn.style.background = 'linear-gradient(135deg, #e74c3c, #c0392b)';
        favBtn.style.border = '2px solid #e74c3c';
        favBtn.style.color = 'white';
        favBtn.style.boxShadow = '0 6px 20px rgba(231, 76, 60, 0.4)';
        console.log('Applied red styling to favorites button');
    }
    
    // Watchlist button functionality
    document.getElementById('watchlistBtn').addEventListener('click', function() {
        const btn = this;
        const movieId = btn.getAttribute('data-movie-id');
        const isInWatchlist = btn.getAttribute('data-in-watchlist') === 'true';
        const action = isInWatchlist ? 'remove' : 'add';
        
        fetch('/watchlist/action', {
            method: 'POST',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            body: 'action=' + action + '&movieId=' + movieId
        })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                // Update button state
                const newIsInWatchlist = action === 'add';
                btn.setAttribute('data-in-watchlist', newIsInWatchlist);
                btn.textContent = newIsInWatchlist ? '✓ In Watchlist' : '+ Add to Watchlist';
            }
        });
    });

    // Favorites button functionality
    document.getElementById('favoritesBtn').addEventListener('click', function() {
        const btn = this;
        const movieId = btn.getAttribute('data-movie-id');
        const isInFavorites = btn.getAttribute('data-in-favorites') === 'true';
        const action = isInFavorites ? 'remove' : 'add';
        
        fetch('/favorites/action', {
            method: 'POST',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            body: 'action=' + action + '&movieId=' + movieId
        })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                // Update button state
                const newIsInFavorites = action === 'add';
                btn.setAttribute('data-in-favorites', newIsInFavorites);
                btn.textContent = newIsInFavorites ? '♥' : '♡';
                btn.title = newIsInFavorites ? 'Remove from Favorites' : 'Add to Favorites';
                
                // Add/remove favorited class for red styling
                console.log('Setting favorites state to:', newIsInFavorites);
                if (newIsInFavorites) {
                    btn.classList.add('favorited');
                    // Force red color with inline styles
                    btn.style.background = 'linear-gradient(135deg, #e74c3c, #c0392b)';
                    btn.style.border = '2px solid #e74c3c';
                    btn.style.color = 'white';
                    btn.style.boxShadow = '0 6px 20px rgba(231, 76, 60, 0.4)';
                    console.log('Applied red styling to button');
                } else {
                    btn.classList.remove('favorited');
                    // Reset to default styling
                    btn.style.background = 'rgba(255, 255, 255, 0.05)';
                    btn.style.border = '2px solid rgba(255, 255, 255, 0.3)';
                    btn.style.color = 'rgba(255, 255, 255, 0.8)';
                    btn.style.boxShadow = 'none';
                    console.log('Reset styling to default');
                }
            }
        });
    });
    
    <% } else { %>
    
    // Not logged in - redirect to login
    document.getElementById('watchlistBtn').addEventListener('click', function() {
        window.location.href = '/login.jsp';
    });

    document.getElementById('favoritesBtn').addEventListener('click', function() {
        window.location.href = '/login.jsp';
    });
    
    <% } %>
</script>
</body>
</html>