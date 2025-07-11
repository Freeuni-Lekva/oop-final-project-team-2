<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.moviemood.bean.User" %>
<%@ page import="com.moviemood.bean.UserList" %>
<%@ page import="com.moviemood.bean.Movie" %>
<%@ page import="java.util.List" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>
        <%
            UserList list = (UserList) request.getAttribute("list");
            String listName = list != null ? list.getName() : "List";
        %>
        <%= listName %> - MovieMood
    </title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/css/navbar.css">
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        body {
            font-family: 'Arial', sans-serif;
            background: linear-gradient(135deg, #1a1a1a 0%, #2d2d2d 100%);
            color: #e0e0e0;
            min-height: 100vh;
        }

        .main-content {
            margin-top: 70px;
            padding: 20px;
            min-height: calc(100vh - 70px);
        }

        .container {
            max-width: 1200px;
            margin: 0 auto;
        }

        .list-header {
            margin-bottom: 30px;
        }

        .list-title {
            font-size: 1.8rem;
            font-weight: 600;
            color: #f39c12;
            margin-bottom: 20px;
        }

        .back-link {
            display: inline-flex;
            align-items: center;
            gap: 8px;
            color: #f39c12;
            text-decoration: none;
            font-weight: 600;
            margin-bottom: 20px;
            transition: color 0.3s ease;
        }

        .back-link:hover {
            color: #e67e22;
        }

        .movies-grid {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
            gap: 20px;
            margin-top: 30px;
        }

        .movie-card {
            background: rgba(0, 0, 0, 0.4);
            border-radius: 15px;
            overflow: hidden;
            transition: all 0.3s ease;
            cursor: pointer;
            position: relative;
        }

        .movie-card:hover {
            transform: translateY(-5px);
            box-shadow: 0 15px 30px rgba(243, 156, 18, 0.2);
        }

        .movie-poster {
            width: 100%;
            height: 300px;
            object-fit: cover;
            border-radius: 15px 15px 0 0;
        }

        .movie-info {
            padding: 15px;
        }

        .movie-title {
            font-size: 1rem;
            font-weight: 600;
            margin-bottom: 5px;
            color: #f39c12;
            line-height: 1.3;
        }

        .movie-year {
            color: #95a5a6;
            font-size: 0.9rem;
        }

        .remove-btn {
            position: absolute;
            top: 10px;
            right: 10px;
            background: rgba(220, 53, 69, 0.9);
            color: white;
            border: none;
            border-radius: 50%;
            width: 32px;
            height: 32px;
            font-size: 14px;
            cursor: pointer;
            display: flex;
            align-items: center;
            justify-content: center;
            opacity: 0;
            transition: all 0.3s ease;
        }

        .movie-card:hover .remove-btn {
            opacity: 1;
        }

        .remove-btn:hover {
            background: rgba(220, 53, 69, 1);
            transform: scale(1.1);
        }

        .empty-list {
            text-align: center;
            padding: 60px 20px;
            color: #bdc3c7;
        }

        .empty-list h3 {
            font-size: 24px;
            margin-bottom: 10px;
            color: #95a5a6;
        }

        .empty-list p {
            font-size: 16px;
            margin-bottom: 30px;
        }

        .browse-movies-btn {
            background: linear-gradient(135deg, #f39c12, #e67e22);
            color: white;
            text-decoration: none;
            padding: 12px 25px;
            border-radius: 25px;
            font-weight: 600;
            transition: all 0.3s ease;
            display: inline-block;
        }

        .browse-movies-btn:hover {
            transform: translateY(-2px);
            box-shadow: 0 6px 20px rgba(243, 156, 18, 0.4);
            color: white;
            text-decoration: none;
        }

        @media (max-width: 768px) {
            .list-title {
                font-size: 1.5rem;
            }

            .movies-grid {
                grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
                gap: 15px;
            }
        }
    </style>
</head>
<body>

<!-- Include Navigation Bar -->
<jsp:include page="WEB-INF/includes/navbar.jsp" />

<main class="main-content">
    <div class="container">
        <!-- Back to Lists Link -->
        <a href="<%= request.getContextPath() %>/lists" class="back-link">
            ← Back to My Lists
        </a>

        <%
            List<Movie> movies = (List<Movie>) request.getAttribute("movies");
            String posterBase = (String) request.getAttribute("POSTER_BASE");
        %>

        <!-- List Header -->
        <div class="list-header">
            <h1 class="list-title"><%= list.getName() %></h1>
        </div>

        <!-- Movies Grid -->
        <% if (movies.isEmpty()) { %>
            <div class="empty-list">
                <h3>No movies yet</h3>
                <p>Start adding movies to "<%= list.getName() %>" to see them here!</p>
                <a href="<%= request.getContextPath() %>/movies" class="browse-movies-btn">Browse Movies</a>
            </div>
        <% } else { %>
            <div class="movies-grid">
                <% for (Movie movie : movies) { %>
                    <div class="movie-card" onclick="location.href='<%= request.getContextPath() %>/movie/details?id=<%= movie.getId() %>'">
                        <img src="<%= posterBase %><%= movie.getPosterPath() %>" 
                             alt="<%= movie.getTitle() %>" 
                             class="movie-poster"
                             onerror="this.src='<%= request.getContextPath() %>/Images/logo.png'">
                        
                        <div class="movie-info">
                            <div class="movie-title" title="<%= movie.getTitle() %>">
                                <%= movie.getTitle() %>
                            </div>
                            <div class="movie-year">
                                <%= movie.getReleaseDate() != null ? 
                                    movie.getReleaseDate().toString().substring(0, 4) : "Unknown" %>
                            </div>
                        </div>
                        
                        <button class="remove-btn" 
                                title="Remove from list" 
                                onclick="event.stopPropagation(); removeFromList(<%= movie.getId() %>)">
                            ✕
                        </button>
                    </div>
                <% } %>
            </div>
        <% } %>
    </div>
</main>

<!-- Hidden Remove Form -->
<form id="removeForm" method="post" action="<%= request.getContextPath() %>/list/view" style="display: none;">
    <input type="hidden" name="action" value="remove">
    <input type="hidden" name="listId" value="<%= list.getId() %>">
    <input type="hidden" name="movieId" id="removeMovieId">
</form>

<script>
    function removeFromList(movieId) {
        if (confirm('Remove this movie from "<%= list.getName() %>"?')) {
            document.getElementById('removeMovieId').value = movieId;
            document.getElementById('removeForm').submit();
        }
    }
</script>

</body>
</html> 