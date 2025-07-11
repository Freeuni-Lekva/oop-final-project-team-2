<%@ page import="com.moviemood.bean.User" %>

<%
    User profileUser = (User) request.getAttribute("profileUser");
    User currentUser = (User) request.getAttribute("currentUser");
    boolean isOwnProfile = (Boolean) request.getAttribute("isOwnProfile");

    // Get real statistics
    Integer watchlistCount = (Integer) request.getAttribute("watchlistCount");
    Integer reviewsCount = (Integer) request.getAttribute("reviewsCount");
    Integer favoritesCount = (Integer) request.getAttribute("favoritesCount");
    Integer friendsCount = (Integer) request.getAttribute("friendsCount");
    Integer listsCount = (Integer) request.getAttribute("listsCount");

    // Default to 0 if null
    if (watchlistCount == null) watchlistCount = 0;
    if (reviewsCount == null) reviewsCount = 0;
    if (favoritesCount == null) favoritesCount = 0;
    if (friendsCount == null) friendsCount = 0;
    if (listsCount == null) listsCount = 0;
%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><%= profileUser.getUsername() %>'s Profile - MovieMood</title>
    <link rel="stylesheet" href="assets/css/navbar.css">
    <link rel="stylesheet" type="text/css" href="assets/css/mainpage.css">
    <link rel="stylesheet" type="text/css" href="assets/css/profile.css">
</head>
<body>
<!-- Include Navigation Bar -->
<jsp:include page="WEB-INF/includes/navbar.jsp" />

<main class="main-content">
    <div class="container">
        <!-- Navigation Button -->
        <a href="Home" class="nav-btn">Back to Movies</a>

        <!-- First Row: Photo and Name -->
        <div class="profile-header">
            <div class="profile-avatar">
                <% if (profileUser.getProfilePicture() != null && !profileUser.getProfilePicture().isEmpty()) { %>
                <img src="/profile-picture/<%= profileUser.getProfilePicture() %>" alt="<%= profileUser.getUsername() %>'s Profile Picture" style="width: 100%; height: 100%; border-radius: 50%; object-fit: cover;">
                <% } else { %>
                <%= profileUser.getUsername().substring(0, 1).toUpperCase() %>
                <% } %>
            </div>
            <div class="profile-name">
                <%= profileUser.getUsername() %>
            </div>
        </div>

        <!-- Second Row: Statistics -->
        <div class="profile-stats">
            <a href="watchlist" class="stat-card">
                <span class="stat-number"><%= watchlistCount %></span>
                <div class="stat-label">Watchlist</div>
            </a>
            <a href="favorites" class="stat-card">
                <span class="stat-number"><%= favoritesCount %></span>
                <div class="stat-label">Favorites</div>
            </a>
            <a href="reviews" class="stat-card">
                <span class="stat-number"><%= reviewsCount %></span>
                <div class="stat-label">Reviews</div>
            </a>
            <a href="friend-requests" class="stat-card">
                <span class="stat-number"><%= friendsCount %></span>
                <div class="stat-label">Friends</div>
            </a>
            <a href="lists" class="stat-card">
                <span class="stat-number"><%= listsCount %></span>
                <div class="stat-label">Lists</div>
            </a>
        </div>

        <!-- Third Row: Action Buttons -->
        <% if (isOwnProfile) { %>
        <div class="profile-actions">
            <a href="settings" class="action-btn secondary">Settings</a>
            <a href="logout" class="action-btn secondary">Logout</a>
        </div>
        <% } %>
    </div>
</main>
</body>
</html>
