<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.moviemood.bean.User" %>
<%@ page import="com.moviemood.bean.Movie" %>
<%@ page import="com.moviemood.bean.MovieReview" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.time.format.DateTimeFormatter" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>My Reviews - MovieMood</title>
    <link rel="stylesheet" href="assets/css/navbar.css">
    <link rel="stylesheet" type="text/css" href="assets/css/mainpage.css">
    <style>
        .review-card {
            background: rgba(255, 255, 255, 0.1);
            border-radius: 15px;
            overflow: hidden;
            transition: transform 0.3s, box-shadow 0.3s;
            backdrop-filter: blur(10px);
            border: 1px solid rgba(255, 255, 255, 0.2);
            margin-bottom: 30px;
            display: flex;
            min-height: 200px;
        }

        .review-card:hover {
            transform: translateY(-5px);
            box-shadow: 0 10px 30px rgba(0, 0, 0, 0.3);
        }

        .review-poster {
            width: 150px;
            height: 200px;
            flex-shrink: 0;
            background: linear-gradient(135deg, #2c3e50, #3498db);
            display: flex;
            align-items: center;
            justify-content: center;
            position: relative;
            overflow: hidden;
        }

        .review-poster img {
            width: 100%;
            height: 100%;
            object-fit: cover;
        }

        .review-content {
            flex: 1;
            padding: 20px;
            display: flex;
            flex-direction: column;
        }

        .review-movie-title {
            font-size: 1.5rem;
            font-weight: 700;
            color: #f39c12;
            margin-bottom: 10px;
            text-decoration: none;
        }

        .review-movie-title:hover {
            color: #e67e22;
        }

        .review-meta {
            display: flex;
            gap: 15px;
            margin-bottom: 15px;
            color: #bdc3c7;
            font-size: 0.9rem;
        }

        .review-text {
            color: #ecf0f1;
            line-height: 1.6;
            margin-bottom: 15px;
            flex: 1;
        }

        .review-date {
            color: #95a5a6;
            font-size: 0.85rem;
            margin-top: auto;
        }

        .no-reviews {
            text-align: center;
            padding: 60px 20px;
            color: #bdc3c7;
        }

        .no-reviews h3 {
            font-size: 1.5rem;
            margin-bottom: 15px;
            color: #f39c12;
        }

        .no-reviews p {
            font-size: 1.1rem;
            margin-bottom: 20px;
        }

        .browse-btn {
            display: inline-block;
            padding: 12px 24px;
            background: linear-gradient(135deg, #f39c12, #e67e22);
            color: white;
            text-decoration: none;
            border-radius: 25px;
            font-weight: 600;
            transition: all 0.3s ease;
            box-shadow: 0 4px 15px rgba(243, 156, 18, 0.3);
        }

        .browse-btn:hover {
            transform: translateY(-2px);
            box-shadow: 0 6px 20px rgba(243, 156, 18, 0.4);
        }

        @media (max-width: 768px) {
            .review-card {
                flex-direction: column;
                min-height: auto;
            }

            .review-poster {
                width: 100%;
                height: 250px;
            }
        }
    </style>
</head>
<body>
<!-- Include Navigation Bar -->
<jsp:include page="WEB-INF/includes/navbar.jsp" />

<%
    User currentUser = (User) request.getAttribute("currentUser");
    List<MovieReview> userReviews = (List<MovieReview>) request.getAttribute("userReviews");
    Map<Integer, Movie> movieMap = (Map<Integer, Movie>) request.getAttribute("movieMap");
    String posterBaseUrl = (String) request.getAttribute("POSTER_BASE");
%>

<main class="main-content">
    <div class="container">
        <h1 class="hero-title">My <span class="highlight">Reviews</span></h1>

        <section class="popular-section">
            <h2 class="section-title">Your Movie Reviews</h2>
            
            <%
                if (userReviews != null && !userReviews.isEmpty()) {
            %>
                <div class="reviews-container">
                    <%
                        for (MovieReview review : userReviews) {
                            Movie movie = movieMap.get(review.getMovieId());
                            if (movie != null) {
                    %>
                    <a href="/movie/details?id=<%= movie.getId() %>" style="text-decoration: none; color: inherit;">
                    <div class="review-card">
                        <div class="review-poster">
                            <% if (movie.getPosterPath() != null && !movie.getPosterPath().isEmpty()) { %>
                                <img src="<%= posterBaseUrl + movie.getPosterPath() %>" alt="<%= movie.getTitle() %>" />
                            <% } else { %>
                                <span style="font-size: 24px; color: white;">🎬</span>
                            <% } %>
                        </div>
                        <div class="review-content">
                            <div class="review-movie-title">
                                <%= movie.getTitle() %>
                            </div>
                            <div class="review-meta">
                                <% if (movie.getReleaseDate() != null) { %>
                                    <span>Released: <%= movie.getReleaseDate().toString().substring(0, 4) %></span>
                                <% } %>
                                <% if (movie.getRuntime() > 0) { %>
                                    <span>Runtime: <%= movie.getRuntime() %> min</span>
                                <% } %>
                            </div>
                            <div class="review-text">
                                <%= review.getReviewText() %>
                            </div>
                            <div class="review-date">
                                Reviewed on <%= review.getCreatedAt().toLocalDateTime().format(DateTimeFormatter.ofPattern("MMMM d, yyyy")) %>
                            </div>
                        </div>
                    </div>
                    </a>
                    <%
                            }
                        }
                    %>
                </div>
            <%
                } else {
            %>
                <div class="no-reviews">
                    <h3>No Reviews Yet</h3>
                </div>
            <%
                }
            %>
        </section>
    </div>
</main>
</body>
</html> 