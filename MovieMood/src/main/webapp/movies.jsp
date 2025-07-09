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
    <link rel="stylesheet" href="assets/css/navbar.css">
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

        /* Slider for recommended movies */
        .container {
            max-width: 1200px;
            margin: 0 auto;
            padding: 0 20px;
        }

        .slider-container {
            position: relative;
            margin-bottom: 50px;
        }

        .slider-wrapper {
            overflow: hidden;
            position: relative;
        }

        .movies-slider {
            display: flex;
            white-space: nowrap;
            transition: transform 0.5s ease;
            gap: 20px;
            will-change: transform;
        }

        .movie-card {
            background: rgba(255, 255, 255, 0.1);
            border-radius: 15px;
            overflow: hidden;
            transition: transform 0.3s, box-shadow 0.3s;
            backdrop-filter: blur(10px);
            border: 1px solid rgba(255, 255, 255, 0.2);
            flex: 0 0 calc(20% - 16px);
            min-width: 200px;
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

        /* Slider Arrow Buttons */
        .slider-arrow {
            position: absolute;
            top: 50%;
            transform: translateY(-50%);
            background: rgba(243, 156, 18, 0.9);
            color: white;
            border: none;
            width: 50px;
            height: 50px;
            border-radius: 50%;
            cursor: pointer;
            font-size: 18px;
            z-index: 10;
            transition: all 0.3s ease;
            display: flex;
            align-items: center;
            justify-content: center;
            box-shadow: 0 4px 15px rgba(0, 0, 0, 0.3);
        }

        .slider-arrow:hover {
            background: rgba(243, 156, 18, 1);
            transform: translateY(-50%) scale(1.1);
            box-shadow: 0 6px 20px rgba(243, 156, 18, 0.4);
        }

        .slider-arrow.prev {
            left: -25px;
        }

        .slider-arrow.next {
            right: -25px;
        }

        .slider-arrow.prev::after {
            content: '';
            width: 0;
            height: 0;
            border-right: 12px solid white;
            border-top: 8px solid transparent;
            border-bottom: 8px solid transparent;
            margin-right: 2px;
        }

        .slider-arrow.next::after {
            content: '';
            width: 0;
            height: 0;
            border-left: 12px solid white;
            border-top: 8px solid transparent;
            border-bottom: 8px solid transparent;
            margin-left: 2px;
        }

        /* Standard grid for other sections */
        .movies-grid {
            display: grid;
            grid-template-columns: repeat(5, 1fr);
            gap: 20px;
            margin-bottom: 50px;
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

        .filter-input {
            background: rgba(255, 255, 255, 0.1);
            border: 1px solid rgba(255, 255, 255, 0.3);
            border-radius: 25px;
            padding: 12px 20px;
            color: white;
            font-size: 14px;
            min-width: 180px;
            backdrop-filter: blur(10px);
            outline: none;
            box-shadow: 0 4px 15px rgba(0, 0, 0, 0.2);
            transition: all 0.3s ease;
        }

        .filter-input::placeholder {
            color: #ccc;
        }

        .filter-input:hover,
        .filter-input:focus {
            background: rgba(255, 255, 255, 0.2);
            border-color: #f39c12;
            box-shadow: 0 0 0 3px rgba(243, 156, 18, 0.2);
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

            .movie-card {
                flex: 0 0 calc(25% - 15px);
            }
        }

        @media (max-width: 768px) {
            .hero-title {
                font-size: 36px;
            }

            .movies-grid {
                grid-template-columns: repeat(2, 1fr);
            }

            .movie-card {
                flex: 0 0 calc(50% - 10px);
            }

            .filter-row {
                flex-direction: column;
                align-items: flex-start;
            }

            .slider-arrow {
                width: 40px;
                height: 40px;
                font-size: 16px;
            }

            .slider-arrow.prev {
                left: -15px;
            }

            .slider-arrow.next {
                right: -15px;
            }
        }

        @media (max-width: 480px) {
            .movies-grid {
                grid-template-columns: 1fr;
            }

            .movie-card {
                flex: 0 0 calc(100% - 10px);
            }
        }

        /* Pagination Styles */
        .pagination-section {
            margin-top: 40px;
            padding: 20px 0;
            display: flex;
            flex-direction: column;
            align-items: center;
            gap: 15px;
        }

        .pagination-container {
            display: flex;
            align-items: center;
            gap: 8px;
            background: rgba(255, 255, 255, 0.1);
            padding: 10px 15px;
            border-radius: 12px;
            backdrop-filter: blur(10px);
            border: 1px solid rgba(255, 255, 255, 0.2);
        }

        .pagination-btn {
            display: flex;
            align-items: center;
            justify-content: center;
            width: 40px;
            height: 40px;
            border: none;
            background: rgba(255, 255, 255, 0.1);
            color: #fff;
            border-radius: 8px;
            cursor: pointer;
            transition: all 0.3s ease;
            font-size: 16px;
            font-weight: bold;
        }

        .pagination-btn:hover:not(:disabled) {
            background: rgba(255, 255, 255, 0.2);
            transform: translateY(-2px);
        }

        .pagination-btn:disabled {
            opacity: 0.5;
            cursor: not-allowed;
        }

        .pagination-numbers {
            display: flex;
            align-items: center;
            gap: 4px;
        }

        .page-number {
            display: flex;
            align-items: center;
            justify-content: center;
            min-width: 40px;
            height: 40px;
            padding: 0 8px;
            border: none;
            background: rgba(255, 255, 255, 0.1);
            color: #fff;
            border-radius: 8px;
            cursor: pointer;
            transition: all 0.3s ease;
            font-size: 14px;
            font-weight: 500;
        }

        .page-number:hover {
            background: rgba(255, 255, 255, 0.2);
            transform: translateY(-2px);
        }

        .page-number.active {
            background: linear-gradient(45deg, #667eea 0%, #764ba2 100%);
            color: #fff;
            box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4);
        }

        .page-number.ellipsis {
            cursor: default;
            background: none;
            pointer-events: none;
        }

        .page-number.ellipsis:hover {
            background: none;
            transform: none;
        }

        .pagination-info {
            color: rgba(255, 255, 255, 0.7);
            font-size: 14px;
            text-align: center;
        }

        /* Responsive Design */
        @media (max-width: 768px) {
            .pagination-container {
                padding: 8px 10px;
                gap: 4px;
            }

            .pagination-btn,
            .page-number {
                width: 35px;
                height: 35px;
                min-width: 35px;
                font-size: 12px;
            }

            .pagination-numbers {
                gap: 2px;
            }
        }

        @media (max-width: 480px) {
            .pagination-container {
                padding: 6px 8px;
                gap: 2px;
            }

            .pagination-btn,
            .page-number {
                width: 30px;
                height: 30px;
                min-width: 30px;
                font-size: 11px;
            }
        }

    </style>
</head>
<body data-current-page="<%= request.getAttribute("currentPage") %>" data-total-pages="<%= request.getAttribute("totalPages") %>">
<!-- Include Navigation Bar -->
<jsp:include page="WEB-INF/includes/navbar.jsp" />

<main class="main-content">
    <script src="assets/js/slider.js"></script>
    <script src="assets/js/pagination.js"></script>
    <div class="container">
        <h1 class="hero-title">Find Your Perfect <span class="highlight">Movie Mood</span></h1>

        <section class="suggested-section">
            <h2 class="section-title">Suggested For You</h2>
            <div class="slider-container">
                <button class="slider-arrow prev" onclick="slideMovies(-1)"></button>
                <div class="slider-wrapper">
                    <div class="movies-slider" id="recommendedSlider">
                        <%
                            List<Movie> recomededMovies = (List<Movie>) request.getAttribute("recomededMovies");
                            String posterBaseUrl = (String) request.getAttribute("POSTER_BASE");
                            if (recomededMovies != null) {
                                for (int i = 0; i < recomededMovies.size(); i++) {
                                    Movie movie = recomededMovies.get(i);
                        %>
                        <a href="/movie/details?id=<%= movie.getId() %>" style="text-decoration: none; color: inherit;">
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
                                        <span class="year"><%= movie.getReleaseDate().toString().substring(0, 4) %></span>
                                    </div>
                                </div>
                            </div>
                        </a>
                        <%
                                }
                            }
                        %>
                    </div>
                </div>
                <button class="slider-arrow next" onclick="slideMovies(1)"></button>
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
                <select class="filter-select" name="genre">
                    <option value="">Genre</option>
                    <option value="12">Adventure</option>
                    <option value="16">Animation</option>
                    <option value="80">Crime</option>
                    <option value="99">Documentary</option>
                    <option value="10751">Family</option>
                    <option value="14">Fantasy</option>
                    <option value="36">History</option>
                    <option value="27">Horror</option>
                    <option value="10402">Music</option>
                    <option value="9648">Mystery</option>
                    <option value="10749">Romance</option>
                    <option value="878">Science Fiction</option>
                    <option value="10770">TV Movie</option>
                    <option value="53">Thriller</option>
                    <option value="10752">War</option>
                    <option value="37">Western</option>
                </select>
                <select class="filter-select">
                    <option>Runtime</option>
                    <option>Short ( <90 mins)</option>
                    <option>Medium (90–120 mins)</option>
                    <option>Long (120+ mins)</option>
                </select>
                <select class="filter-select">
                    <option>Sort By</option>
                    <option>Popularity</option>
                    <option>Rating</option>
                    <option>Release Date</option>
                </select>
                <input type="text" class="filter-input" placeholder="Search by title...">
                <button class="reset-btn">Search</button>
            </div>
        </section>

        <section class="popular-section">
            <h2 class="section-title">Popular Movies</h2>
            <div class="movies-grid">
                <%
                    List<Movie> movies = (List<Movie>) request.getAttribute("movies");
                    if (movies != null) {
                        for (int i = 0; i < movies.size(); i++) {
                            Movie movie = movies.get(i);
                %>
                <a href="/movie/details?id=<%= movie.getId() %>" style="text-decoration: none; color: inherit;">
                    <div class="movie-card">
                        <div class="movie-poster">
                            <% if (movie.getPosterPath() != null && !movie.getPosterPath().isEmpty()) { %>
                            <img src="<%= posterBaseUrl + movie.getPosterPath() %>" alt="<%= movie.getTitle() %>" />
                            <% } else { %>
                            <div class="movie-poster-fallback"></div>
                            <% } %>
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
                </a>
                <%
                        }
                    }
                %>
            </div>
        </section>
    </div>
    <section class="pagination-section">
        <div class="pagination-container">
            <button class="pagination-btn prev-btn" id="prevBtn" onclick="changePage(-1)">
                <span>&lt;</span>
            </button>

            <div class="pagination-numbers" id="paginationNumbers">
                <!-- Page numbers will be generated by JavaScript -->
            </div>

            <button class="pagination-btn next-btn" id="nextBtn" onclick="changePage(1)">
                <span>&gt;</span>
            </button>
        </div>

        <div class="pagination-info">
            <span id="pageInfo">Page 1 of 500</span>
        </div>
    </section>
</main>

</body>
</html>
<%--old--%>