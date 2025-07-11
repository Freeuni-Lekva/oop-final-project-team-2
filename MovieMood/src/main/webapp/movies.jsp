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
<%@ page import="com.moviemood.dao.MovieRatingsDao" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>MovieMood - Find Your Perfect Movie</title>
    <link rel="stylesheet" href="assets/css/navbar.css?v=<%= System.currentTimeMillis() %>">
    <link rel="stylesheet" type="text/css" href="assets/css/mainpage.css?v=<%= System.currentTimeMillis() %>">
    <link rel="stylesheet" href="assets/css/friends-activity.css?v=<%= System.currentTimeMillis() %>">
</head>
<body data-current-page="<%= request.getAttribute("currentPage") %>" data-total-pages="<%= request.getAttribute("totalPages") %>">
<!-- Include Navigation Bar -->
<jsp:include page="WEB-INF/includes/navbar.jsp" />

<main class="main-content">
    <script src="assets/js/slider.js"></script>
    <script src="assets/js/pagination.js"></script>
    <div class="container">
        <h1 class="hero-title">Find Your Perfect <span class="highlight">Movie Mood</span></h1>

        <%
            MovieRatingsDao ratings=(MovieRatingsDao)request.getServletContext().getAttribute("movieRatingsDao");
            User user = (User) request.getSession().getAttribute("user");
            List<Movie> recomededMovies = (List<Movie>) request.getAttribute("recomededMovies");
            String posterBaseUrl = (String) request.getAttribute("POSTER_BASE");
            if (user != null && recomededMovies != null && !recomededMovies.isEmpty()) {
        %>
        <section class="suggested-section">
            <h2 class="section-title">Suggested For You</h2>
            <div class="slider-container">
                <button class="slider-arrow prev" onclick="slideMovies(-1)"></button>
                <div class="slider-wrapper">
                    <div class="movies-slider" id="recommendedSlider">
                        <%
                            for (Movie movie : recomededMovies) {
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
                                            <%
                                                double avgRating = ratings.getAverageMovieRating(movie.getId());
                                                if (avgRating != -1) {
                                                    int fullStars = (int)(avgRating / 2); // Convert 10-scale to 5
                                                    boolean halfStar = (avgRating / 2 - fullStars) >= 0.5;
                                                    int emptyStars = 5 - fullStars - (halfStar ? 1 : 0);

                                                    StringBuilder starsBuilder = new StringBuilder();
                                                    for (int i = 0; i < fullStars; i++) starsBuilder.append("★");
                                                    if (halfStar) starsBuilder.append("☆");
                                                    for (int i = 0; i < emptyStars; i++) starsBuilder.append("✩");
                                            %>
                                            <div class="rating">
                                                <span class="stars"><%= starsBuilder.toString() %></span>
                                            </div>
                                            <%
                                            } else {
                                            %>
                                            <div class="rating">
                                                <span class="stars" style="color: gray;">Not Rated</span>
                                            </div>
                                            <%
                                                }
                                            %>

                                        </div>
                                        <span class="year"><%= movie.getReleaseDate().toString().substring(0, 4) %></span>
                                    </div>
                                </div>
                            </div>
                        </a>
                        <%
                            }
                        %>
                    </div>
                </div>
                <button class="slider-arrow next" onclick="slideMovies(1)"></button>
            </div>
        </section>
        <% } %>

        <section class="filters">
            <form method="get" action="/Home">
                <div class="filter-row">
                    <span class="filter-label">Filters:</span>
                    <select class="filter-select" name="year">
                        <option value="">Year</option>
                        <option value="2023" <%= "2023".equals(request.getParameter("year")) ? "selected" : "" %>>2023</option>
                        <option value="2022" <%= "2022".equals(request.getParameter("year")) ? "selected" : "" %>>2022</option>
                        <option value="2021" <%= "2021".equals(request.getParameter("year")) ? "selected" : "" %>>2021</option>
                    </select>

                    <select class="filter-select" name="genre">
                        <option value="">Genre</option>
                        <option value="12" <%= "12".equals(request.getParameter("genre")) ? "selected" : "" %>>Adventure</option>
                        <option value="16" <%= "16".equals(request.getParameter("genre")) ? "selected" : "" %>>Animation</option>
                        <!-- ... -->
                    </select>

                    <select class="filter-select" name="runtime">
                        <option value="">Runtime</option>
                        <option value="short" <%= "short".equals(request.getParameter("runtime")) ? "selected" : "" %>>Short</option>
                        <option value="medium" <%= "medium".equals(request.getParameter("runtime")) ? "selected" : "" %>>Medium</option>
                        <option value="long" <%= "long".equals(request.getParameter("runtime")) ? "selected" : "" %>>Long</option>
                    </select>

                    <select class="filter-select" name="sort">
                        <option value="">Category</option>
                        <option value="popular" <%= "popular".equals(request.getParameter("sort")) ? "selected" : "" %>>Popular</option>
                        <option value="top_rated" <%= "top_rated".equals(request.getParameter("sort")) ? "selected" : "" %>>Top Rated</option>
                        <option value="upcoming" <%= "upcoming".equals(request.getParameter("sort")) ? "selected" : "" %>>Upcoming</option>
                        <option value="now_playing" <%= "now_playing".equals(request.getParameter("sort")) ? "selected" : "" %>>Now Playing</option>
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
                        </div>
                        <div class="movie-info">
                            <h3 class="movie-title"><%= movie.getTitle() %></h3>
                            <div class="movie-meta">
                                <div class="rating">
                                    <div class="movie-meta">
                                        <div class="rating">
                                            <%
                                                double avgRating = ratings.getAverageMovieRating(movie.getId()); // 0–10
                                                if (avgRating != -1) {
                                                    int fullStars = (int)(avgRating / 2); // Convert to 0–5 scale
                                                    boolean halfStar = (avgRating / 2 - fullStars) >= 0.5;
                                                    int emptyStars = 5 - fullStars - (halfStar ? 1 : 0);

                                                    StringBuilder starsBuilder = new StringBuilder();
                                                    for (int j = 0; j < fullStars; j++) starsBuilder.append("★");
                                                    if (halfStar) starsBuilder.append("☆");
                                                    for (int j = 0; j < emptyStars; j++) starsBuilder.append("✩");
                                            %>
                                            <span class="stars"><%= starsBuilder.toString() %></span>
                                            <span class="rating-score"><%= String.format("%.1f", avgRating) %></span>
                                            <%
                                            } else {
                                            %>
                                            <span class="stars" style="color: gray;">Not Rated</span>
                                            <%
                                                }
                                            %>
                                        </div>
                                        <% if (movie.getReleaseDate() != null) { %>
                                        <span class="year"><%= movie.getReleaseDate().toString().substring(0, 4) %></span>
                                        <% } %>
                                    </div>

                                </div>
                                <% if (movie.getReleaseDate() != null) { %>
                                <span class="year"><%= movie.getReleaseDate().toString().substring(0, 4) %></span>
                                <% } %>
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