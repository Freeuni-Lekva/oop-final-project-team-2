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
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

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
            grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
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
            height: 350px;
            background: linear-gradient(135deg, #2c3e50, #3498db);
            display: flex;
            align-items: center;
            justify-content: center;
            position: relative;
        }

        .play-btn {
            width: 60px;
            height: 60px;
            background: rgba(243, 156, 18, 0.9);
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            cursor: pointer;
            transition: background 0.3s;
        }

        .play-btn:hover {
            background: rgba(243, 156, 18, 1);
        }

        .play-btn::after {
            content: '';
            width: 0;
            height: 0;
            border-left: 20px solid white;
            border-top: 12px solid transparent;
            border-bottom: 12px solid transparent;
            margin-left: 4px;
        }

        .movie-info {
            padding: 20px;
        }

        .movie-title {
            font-size: 18px;
            font-weight: 600;
            margin-bottom: 10px;
            color: white;
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
            border: 1px solid rgba(255, 255, 255, 0.2);
            border-radius: 8px;
            padding: 8px 15px;
            color: white;
            font-size: 14px;
            min-width: 120px;
        }

        .filter-select option {
            background: #2c3e50;
            color: white;
        }

        .reset-btn {
            background: #3498db;
            color: white;
            padding: 8px 20px;
            border: none;
            border-radius: 8px;
            cursor: pointer;
            font-weight: 600;
            transition: background 0.3s;
        }

        .reset-btn:hover {
            background: #2980b9;
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
        @media (max-width: 768px) {
            .hero-title {
                font-size: 36px;
            }

            .nav-links {
                display: none;
            }

            .movies-grid {
                grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            }

            .filter-row {
                flex-direction: column;
                align-items: flex-start;
            }
        }
    </style>
</head>
<body>
<header class="header">
    <div class="container">
        <nav class="nav">
            <div class="logo">
                <img src="WEB-INF/Images/logo.png" alt="MovieMood Logo">
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
                <c:forEach items="${recomededMovies}" var="movie" varStatus="status">
                    <c:if test="${status.index < 4}">
                        <div class="movie-card">
                            <div class="movie-poster">
                                <div class="play-btn"></div>
                            </div>
                            <div class="movie-info">
                                <h3 class="movie-title">${movie.title}</h3>
                                <div class="movie-meta">
                                    <div class="rating">
                                        <span class="stars">★★★★★</span>
                                    </div>
                                    <span class="year">${movie.releaseDate.substring(0, 4)}</span>
                                </div>
                            </div>
                        </div>
                    </c:if>
                </c:forEach>
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
                <c:forEach items="${movies}" var="movie" varStatus="status">
                    <c:if test="${status.index < 8}">
                        <div class="movie-card">
                            <div class="movie-poster">
                                <div class="play-btn"></div>
                                <c:if test="${status.index == 0}">
                                    <div style="position: absolute; top: 10px; left: 10px; background: #f39c12; color: white; padding: 4px 8px; border-radius: 4px; font-size: 12px; font-weight: bold;">NEW</div>
                                </c:if>
                            </div>
                            <div class="movie-info">
                                <h3 class="movie-title">${movie.title}</h3>
                                <div class="movie-meta">
                                    <div class="rating">
                                        <span class="stars">★★★★☆</span>
                                    </div>
                                    <span class="year">${movie.releaseDate.substring(0, 4)}</span>
                                </div>
                            </div>
                        </div>
                    </c:if>
                </c:forEach>
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