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
    <link rel="stylesheet" type="text/css" href="assets/css/mainpage.css">
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
            <form method="get" action="/Home">
                <div class="filter-row">
                    <span class="filter-label">Filters:</span>
                    <select class="filter-select" name="year">
                        <option value="">Year</option>
                        <option value="2023">2023</option>
                        <option value="2022">2022</option>
                        <option value="2021">2021</option>
                    </select>

                    <select class="filter-select" name="genre">
                        <option value="">Genre</option>
                        <option value="12">Adventure</option>
                        <option value="16">Animation</option>
                        <!-- ... -->
                    </select>

                    <select class="filter-select" name="runtime">
                        <option value="">Runtime</option>
                        <option value="short">Short</option>
                        <option value="medium">Medium</option>
                        <option value="long">Long</option>
                    </select>

                    <select class="filter-select" name="sort">
                        <option value="">Sort By</option>
                        <option value="popularity.desc">Popularity</option>
                        <option value="vote_average.desc">Rating</option>
                        <option value="primary_release_date.desc">Release Date</option>
                    </select>

                    <input type="text" class="filter-input" name="title" placeholder="Search by title...">

                    <button class="reset-btn" type="submit">Search</button>
                </div>
            </form>
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