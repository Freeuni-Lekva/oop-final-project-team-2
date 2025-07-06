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
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background: linear-gradient(135deg, #1a1a2e 0%, #16213e 50%, #0f3460 100%);
            color: white;
            min-height: 100vh;
        }

        .container {
            max-width: 1200px;
            margin: 0 auto;
            padding: 0 20px;
        }

        /* Header */
        .header {
            padding: 20px 0;
            border-bottom: 1px solid rgba(255, 255, 255, 0.1);
            position: relative;
            z-index: 10;
        }

        .nav {
            display: flex;
            justify-content: space-between;
            align-items: center;
        }

        .logo {
            display: flex;
            align-items: center;
            gap: 10px;
        }

        .logo img {
            width: 40px;
            height: 40px;
            border-radius: 50%;
        }

        .logo-text {
            font-size: 24px;
            font-weight: bold;
            color: white;
        }

        .nav-links {
            display: flex;
            gap: 30px;
            align-items: center;
        }

        .nav-links a {
            color: white;
            text-decoration: none;
            font-weight: 500;
            transition: color 0.3s;
        }

        .nav-links a:hover {
            color: #f39c12;
        }

        .create-account-btn {
            background: #f39c12;
            color: white;
            padding: 10px 20px;
            border-radius: 25px;
            text-decoration: none;
            font-weight: 600;
            transition: background 0.3s;
        }

        .create-account-btn:hover {
            background: #e67e22;
        }


        /* Hero Section with Backdrop */
        .hero-section {
            position: relative;
            height: 60vh;
            overflow: hidden;
            display: flex;
            align-items: center;
            margin-bottom: 50px;
        }

        .backdrop-container {
            position: absolute;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            z-index: 1;
        }

        .backdrop-image {
            width: 100%;
            height: 100%;
            object-fit: cover;
            opacity: 0.3;
        }

        .backdrop-overlay {
            position: absolute;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            background: linear-gradient(45deg, rgba(26, 26, 46, 0.9) 0%, rgba(15, 52, 96, 0.7) 100%);
            z-index: 2;
        }

        .hero-content {
            position: relative;
            z-index: 3;
            display: flex;
            gap: 40px;
            align-items: center;
            width: 100%;
        }

        .movie-poster-large {
            width: 300px;
            height: 450px;
            border-radius: 15px;
            overflow: hidden;
            box-shadow: 0 20px 40px rgba(0, 0, 0, 0.5);
            flex-shrink: 0;
        }

        .movie-poster-large img {
            width: 100%;
            height: 100%;
            object-fit: cover;
        }

        .movie-info-main {
            flex: 1;
        }

        .movie-title-main {
            font-size: 48px;
            font-weight: 700;
            margin-bottom: 15px;
            color: white;
            text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.5);
        }

        .movie-tagline {
            font-size: 18px;
            color: #f39c12;
            margin-bottom: 20px;
            font-style: italic;
        }

        .movie-meta-main {
            display: flex;
            gap: 30px;
            margin-bottom: 30px;
            flex-wrap: wrap;
        }

        .meta-item {
            display: flex;
            align-items: center;
            gap: 8px;
        }

        .meta-label {
            font-weight: 600;
            color: #bdc3c7;
        }

        .meta-value {
            color: white;
        }

        .rating-display {
            display: flex;
            align-items: center;
            gap: 10px;
        }

        .stars-display {
            color: #f39c12;
            font-size: 20px;
        }

        .rating-score {
            font-size: 18px;
            font-weight: bold;
            color: #f39c12;
        }

        .genres {
            display: flex;
            gap: 10px;
            flex-wrap: wrap;
        }

        .genre-tag {
            background: rgba(243, 156, 18, 0.2);
            color: #f39c12;
            padding: 6px 12px;
            border-radius: 20px;
            font-size: 14px;
            border: 1px solid rgba(243, 156, 18, 0.3);
        }

        .overview {
            font-size: 16px;
            line-height: 1.6;
            color: #ecf0f1;
            margin-bottom: 30px;
        }

        .action-buttons {
            display: flex;
            gap: 15px;
            flex-wrap: wrap;
        }

        .btn {
            padding: 12px 24px;
            border-radius: 25px;
            font-weight: 600;
            text-decoration: none;
            transition: all 0.3s;
            border: none;
            cursor: pointer;
            font-size: 14px;
        }

        .btn-primary {
            background: linear-gradient(135deg, #f39c12, #e67e22);
            color: white;
            box-shadow: 0 4px 15px rgba(243, 156, 18, 0.3);
        }

        .btn-primary:hover {
            transform: translateY(-2px);
            box-shadow: 0 6px 20px rgba(243, 156, 18, 0.4);
        }

        .btn-secondary {
            background: rgba(255, 255, 255, 0.1);
            color: white;
            border: 1px solid rgba(255, 255, 255, 0.2);
            backdrop-filter: blur(10px);
        }

        .btn-secondary:hover {
            background: rgba(255, 255, 255, 0.2);
            transform: translateY(-2px);
        }

        /* Details Section */
        .details-section {
            margin-bottom: 50px;
        }

        .section-title {
            font-size: 32px;
            font-weight: 700;
            margin-bottom: 30px;
            color: white;
            position: relative;
        }

        .section-title::after {
            content: '';
            position: absolute;
            bottom: -10px;
            left: 0;
            width: 60px;
            height: 4px;
            background: linear-gradient(135deg, #f39c12, #e67e22);
            border-radius: 2px;
        }

        .details-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
            gap: 30px;
            margin-bottom: 50px;
        }

        .detail-card {
            background: rgba(255, 255, 255, 0.1);
            border-radius: 15px;
            padding: 25px;
            backdrop-filter: blur(10px);
            border: 1px solid rgba(255, 255, 255, 0.2);
            transition: transform 0.3s;
        }

        .detail-card:hover {
            transform: translateY(-5px);
        }

        .detail-card h3 {
            color: #f39c12;
            margin-bottom: 15px;
            font-size: 20px;
        }

        .detail-card p {
            color: #ecf0f1;
            line-height: 1.6;
        }

        /* Rating Section */
        .rating-section {
            background: rgba(255, 255, 255, 0.1);
            border-radius: 15px;
            padding: 30px;
            margin-bottom: 50px;
            backdrop-filter: blur(10px);
            border: 1px solid rgba(255, 255, 255, 0.2);
        }

        .rating-container {
            display: flex;
            align-items: center;
            gap: 20px;
            margin-bottom: 20px;
        }

        .rating-stars {
            display: flex;
            gap: 5px;
        }

        .star {
            font-size: 30px;
            color: #555;
            cursor: pointer;
            transition: color 0.3s;
        }

        .star:hover,
        .star.active {
            color: #f39c12;
        }

        .rating-text {
            font-size: 18px;
            font-weight: 600;
        }

        .submit-rating {
            background: linear-gradient(135deg, #f39c12, #e67e22);
            color: white;
            padding: 10px 20px;
            border: none;
            border-radius: 25px;
            cursor: pointer;
            font-weight: 600;
            transition: all 0.3s;
        }

        .submit-rating:hover {
            transform: translateY(-2px);
            box-shadow: 0 6px 20px rgba(243, 156, 18, 0.4);
        }

        /* Reviews Section */
        .reviews-section {
            margin-bottom: 50px;
        }

        .review-form {
            background: rgba(255, 255, 255, 0.1);
            border-radius: 15px;
            padding: 30px;
            margin-bottom: 30px;
            backdrop-filter: blur(10px);
            border: 1px solid rgba(255, 255, 255, 0.2);
        }

        .form-group {
            margin-bottom: 20px;
        }

        .form-group label {
            display: block;
            margin-bottom: 8px;
            font-weight: 600;
            color: white;
        }

        .form-group input,
        .form-group textarea {
            width: 100%;
            padding: 12px;
            border-radius: 8px;
            border: 1px solid rgba(255, 255, 255, 0.3);
            background: rgba(255, 255, 255, 0.1);
            color: white;
            font-size: 16px;
            backdrop-filter: blur(10px);
        }

        .form-group input::placeholder,
        .form-group textarea::placeholder {
            color: rgba(255, 255, 255, 0.6);
        }

        .form-group textarea {
            resize: vertical;
            min-height: 120px;
        }

        .reviews-list {
            display: flex;
            flex-direction: column;
            gap: 20px;
        }

        .review-item {
            background: rgba(255, 255, 255, 0.1);
            border-radius: 15px;
            padding: 25px;
            backdrop-filter: blur(10px);
            border: 1px solid rgba(255, 255, 255, 0.2);
        }

        .review-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 15px;
        }

        .reviewer-info {
            display: flex;
            align-items: center;
            gap: 10px;
        }

        .reviewer-avatar {
            width: 40px;
            height: 40px;
            border-radius: 50%;
            background: linear-gradient(135deg, #f39c12, #e67e22);
            display: flex;
            align-items: center;
            justify-content: center;
            font-weight: bold;
            color: white;
        }

        .reviewer-name {
            font-weight: 600;
            color: white;
        }

        .review-date {
            color: #bdc3c7;
            font-size: 14px;
        }

        .review-rating {
            color: #f39c12;
        }

        .review-text {
            color: #ecf0f1;
            line-height: 1.6;
        }

        /* Responsive Design */
        @media (max-width: 768px) {
            .hero-content {
                flex-direction: column;
                text-align: center;
            }

            .movie-poster-large {
                width: 250px;
                height: 375px;
            }

            .movie-title-main {
                font-size: 36px;
            }

            .movie-meta-main {
                justify-content: center;
            }

            .details-grid {
                grid-template-columns: 1fr;
            }

            .rating-container {
                flex-direction: column;
                align-items: flex-start;
            }
        }

        @media (max-width: 480px) {
            .hero-section {
                height: auto;
                padding: 40px 0;
            }

            .movie-poster-large {
                width: 200px;
                height: 300px;
            }

            .movie-title-main {
                font-size: 28px;
            }

            .section-title {
                font-size: 24px;
            }
        }
    </style>
</head>
<body>
<%
    Movie movie = (Movie) request.getAttribute("movie");
    String backDropBaseURL = (String) request.getAttribute("backDropPathBaseURL");
    String posterBaseUrl = (String) request.getAttribute("POSTER_BASE");
%>

<header class="header">
    <div class="container">
        <nav class="nav">
            <div class="logo">
                <img src="Images/logo.png" alt="MovieMood Logo">
                <span class="logo-text">MovieMood</span>
            </div>
            <div class="nav-links">
                <a href="/films">Films</a>
                <a href="#">Popular Lists</a>
                <a href="/login" class="create-account-btn">Create Account</a>
            </div>
        </nav>
    </div>
</header>

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
                    <button class="btn btn-primary">▶ Watch Trailer</button>
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
</script>
</body>
</html>