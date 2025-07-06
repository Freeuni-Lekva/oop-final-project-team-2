<%--
  Created by IntelliJ IDEA.
  User: giorgidzagania
  Date: 6/18/25
  Time: 17:47
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.moviemood.bean.User" %>
<%@ page import="com.moviemood.bean.Movie" %>
<%@ page import="java.util.List" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>MovieMood - Find Your Perfect Movie</title>
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

        /* Main Content */
        .main-content {
            padding: 40px 0;
        }

        .hero-title {
            font-size: 48px;
            font-weight: 700;
            margin-bottom: 40px;
            text-align: left;
        }

        .hero-title .highlight {
            color: #f39c12;
        }

        .section-title {
            font-size: 24px;
            font-weight: 600;
            margin-bottom: 30px;
            color: white;
        }

        /* Movie Cards */
        .movies-grid {
            display: grid;
            grid-template-columns: repeat(5, 1fr);
            gap: 20px;
            margin-bottom: 50px;
        }

        .movie-card {
            background: rgba(255, 255, 255, 0.1);
            border-radius: 15px;
            overflow: hidden;
            transition: transform 0.3s, box-shadow 0.3s;
            backdrop-filter: blur(10px);
            border: 1px solid rgba(255, 255, 255, 0.2);
        }

        .movie-card:hover {
            transform: translateY(-5px);
            box-shadow: 0 10px 30px rgba(0, 0, 0, 0.3);
        }

        .movie-poster {
            width: 100%;
            height: 250px;
            background: linear-gradient(135deg, #2c3e50, #3498db);
            display: flex;
            align-items: center;
            justify-content: center;
            position: relative;
            overflow: hidden;
        }

        .movie-poster img {
            width: 100%;
            height: 100%;
            object-fit: cover;
        }

        .movie-poster-fallback {
            width: 100%;
            height: 250px;
            background: linear-gradient(135deg, #2c3e50, #3498db);
            display: flex;
            align-items: center;
            justify-content: center;
            position: relative;
        }

        .play-btn {
            width: 50px;
            height: 50px;
            background: rgba(243, 156, 18, 0.9);
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            cursor: pointer;
            transition: background 0.3s;
            position: absolute;
            z-index: 2;
        }

        .play-btn:hover {
            background: rgba(243, 156, 18, 1);
        }

        .play-btn::after {
            content: '';
            width: 0;
            height: 0;
            border-left: 16px solid white;
            border-top: 10px solid transparent;
            border-bottom: 10px solid transparent;
            margin-left: 3px;
        }

        .movie-info {
            padding: 15px;
        }

        .movie-title {
            font-size: 16px;
            font-weight: 600;
            margin-bottom: 8px;
            color: white;
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
        }

        .movie-meta {
            display: flex;
            align-items: center;
            gap: 10px;
            font-size: 14px;
            color: #bdc3c7;
        }

        .rating {
            display: flex;
            align-items: center;
            gap: 5px;
        }

        .stars {
            color: #f39c12;
        }

        .year {
            color: #95a5a6;
        }

        /* Filters */
        .filters {
            margin-bottom: 40px;
        }

        .filter-row {
            display: flex;
            align-items: center;
            gap: 20px;
            margin-bottom: 20px;
            flex-wrap: wrap;
        }

        .filter-label {
            font-weight: 600;
            color: white;
        }

        .filter-select {
            background: rgba(255, 255, 255, 0.1);
            border: 1px solid rgba(255, 255, 255, 0.3);
            border-radius: 25px;
            padding: 12px 20px;
            color: white;
            font-size: 14px;
            min-width: 140px;
            backdrop-filter: blur(10px);
            cursor: pointer;
            transition: all 0.3s ease;
            outline: none;
            box-shadow: 0 4px 15px rgba(0, 0, 0, 0.2);
        }

        .filter-select:hover {
            background: rgba(255, 255, 255, 0.15);
            border-color: #f39c12;
            transform: translateY(-2px);
            box-shadow: 0 6px 20px rgba(243, 156, 18, 0.3);
        }

        .filter-select:focus {
            background: rgba(255, 255, 255, 0.2);
            border-color: #f39c12;
            box-shadow: 0 0 0 3px rgba(243, 156, 18, 0.2);
        }

        .filter-select option {
            background: #2c3e50;
            color: white;
            padding: 10px;
            border-radius: 5px;
        }

        .reset-btn {
            background: linear-gradient(135deg, #3498db, #2980b9);
            color: white;
            padding: 12px 24px;
            border: none;
            border-radius: 25px;
            cursor: pointer;
            font-weight: 600;
            font-size: 14px;
            transition: all 0.3s ease;
            box-shadow: 0 4px 15px rgba(52, 152, 219, 0.3);
            outline: none;
        }

        .reset-btn:hover {
            background: linear-gradient(135deg, #2980b9, #1f618d);
            transform: translateY(-2px);
            box-shadow: 0 6px 20px rgba(52, 152, 219, 0.4);
        }

        .reset-btn:active {
            transform: translateY(0);
        }

        .filter-tags {
            display: flex;
            gap: 10px;
            flex-wrap: wrap;
        }

        .filter-tag {
            background: rgba(255, 255, 255, 0.1);
            border: 1px solid rgba(255, 255, 255, 0.2);
            padding: 8px 15px;
            border-radius: 20px;
            font-size: 14px;
            cursor: pointer;
            transition: all 0.3s;
        }

        .filter-tag.active {
            background: #f39c12;
            border-color: #f39c12;
            color: white;
        }

        .filter-tag:hover {
            background: rgba(255, 255, 255, 0.2);
        }

        /* Responsive */
        @media (max-width: 1200px) {
            .movies-grid {
                grid-template-columns: repeat(4, 1fr);
            }
        }

        @media (max-width: 768px) {
            .hero-title {
                font-size: 36px;
            }

            .nav-links {
                display: none;
            }

            .movies-grid {
                grid-template-columns: repeat(2, 1fr);
            }

            .filter-row {
                flex-direction: column;
                align-items: flex-start;
            }
        }

        @media (max-width: 480px) {
            .movies-grid {
                grid-template-columns: 1fr;
            }
        }
    </style>
</head>
<body>
<header class="header">
    <div class="container">
        <nav class="nav">
            <div class="logo">
                <img src="Images/logo.png" alt="MovieMood Logo">
                <span class="logo-text">MovieMood</span>
            </div>
            <div class="nav-links">
                <a href="#">Films</a>
                <a href="#">Popular Lists</a>
                <a href="#" class="create-account-btn">Create Account</a>
            </div>
        </nav>
    </div>
</header>

<main class="main-content">
    <div class="container">
        <h1 class="hero-title">Find Your Perfect <span class="highlight">Movie Mood</span></h1>

        <section class="suggested-section">
            <h2 class="section-title">Suggested For You</h2>
            <div class="movies-grid">
                <%
                    List<Movie> recomededMovies = (List<Movie>) request.getAttribute("recomededMovies");
                    String posterBaseUrl = (String) request.getAttribute("POSTER_BASE");
                    if (recomededMovies != null) {
                        for (int i = 0; i < Math.min(5, recomededMovies.size()); i++) {
                            Movie movie = recomededMovies.get(i);
                %>
                <div class="movie-card">
                    <div class="movie-poster">
                        <% if (movie.getPosterPath() != null && !movie.getPosterPath().isEmpty()) { %>
                        <img src="<%= posterBaseUrl + movie.getPosterPath() %>" alt="<%= movie.getTitle() %>" />
                        <% } else { %>
                        <div class="movie-poster-fallback"></div>
                        <% } %>
                        <div class="play-btn"></div>
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
                <%
                        }
                    }
                %>
            </div>
        </section>

        <section class="filters">
            <div class="filter-row">
                <span class="filter-label">Filters:</span>
                <select class="filter-select">
                    <option>Year</option>
                    <option>2023</option>
                    <option>2022</option>
                    <option>2021</option>
                </select>
                <select class="filter-select">
                    <option>Genre</option>
                    <option>Action</option>
                    <option>Comedy</option>
                    <option>Drama</option>
                </select>
                <select class="filter-select">
                    <option>Rating</option>
                    <option>5 Stars</option>
                    <option>4+ Stars</option>
                    <option>3+ Stars</option>
                </select>
                <select class="filter-select">
                    <option>Sort By</option>
                    <option>Popularity</option>
                    <option>Rating</option>
                    <option>Release Date</option>
                </select>
                <button class="reset-btn">Reset Filters</button>
            </div>
            <div class="filter-tags">
                <span class="filter-tag">Action</span>
                <span class="filter-tag">Comedy</span>
                <span class="filter-tag active">Sci-Fi</span>
                <span class="filter-tag">Drama</span>
                <span class="filter-tag">Thriller</span>
                <span class="filter-tag">2023</span>
                <span class="filter-tag">4+ Stars</span>
            </div>
        </section>

        <section class="popular-section">
            <h2 class="section-title">Popular Movies</h2>
            <div class="movies-grid">
                <%
                    List<Movie> movies = (List<Movie>) request.getAttribute("movies");
                    if (movies != null) {
                        for (int i = 0; i < Math.min(10, movies.size()); i++) {
                            Movie movie = movies.get(i);
                %>
                <div class="movie-card">
                    <div class="movie-poster">
                        <% if (movie.getPosterPath() != null && !movie.getPosterPath().isEmpty()) { %>
                        <img src="<%= posterBaseUrl + movie.getPosterPath() %>" alt="<%= movie.getTitle() %>" />
                        <% } else { %>
                        <div class="movie-poster-fallback"></div>
                        <% } %>
                        <div class="play-btn"></div>
                        <% if (i == 0) { %>
                        <div style="position: absolute; top: 10px; left: 10px; background: #f39c12; color: white; padding: 4px 8px; border-radius: 4px; font-size: 12px; font-weight: bold; z-index: 2;">NEW</div>
                        <% } %>
                    </div>
                    <div class="movie-info">
                        <h3 class="movie-title"><%= movie.getTitle() %></h3>
                        <div class="movie-meta">
                            <div class="rating">
                                <span class="stars">★★★★☆</span>
                            </div>
                            <span class="year"><%= movie.getReleaseDate().toString().substring(0, 4) %></span>
                        </div>
                    </div>
                </div>
                <%
                        }
                    }
                %>
            </div>
        </section>
    </div>
</main>

<script>
    // Add interactivity for filter tags
    document.querySelectorAll('.filter-tag').forEach(tag => {
        tag.addEventListener('click', function() {
            this.classList.toggle('active');
        });
    });

    // Reset filters functionality
    document.querySelector('.reset-btn').addEventListener('click', function() {
        document.querySelectorAll('.filter-tag').forEach(tag => {
            tag.classList.remove('active');
        });
        document.querySelectorAll('.filter-select').forEach(select => {
            select.selectedIndex = 0;
        });
    });
</script>
</body>
</html>