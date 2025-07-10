<%@ page import="com.moviemood.bean.User" %>

<%
    User profileUser = (User) request.getAttribute("profileUser");
    User currentUser = (User) request.getAttribute("currentUser");
    boolean isOwnProfile = (Boolean) request.getAttribute("isOwnProfile");
    
    // Get real statistics
    Integer watchlistCount = (Integer) request.getAttribute("watchlistCount");
    Integer reviewsCount = (Integer) request.getAttribute("reviewsCount");
    Integer favoritesCount = (Integer) request.getAttribute("favoritesCount");
    
    // Default to 0 if null
    if (watchlistCount == null) watchlistCount = 0;
    if (reviewsCount == null) reviewsCount = 0;
    if (favoritesCount == null) favoritesCount = 0;
%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><%= profileUser.getUsername() %>'s Profile - MovieMood</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        body {
            font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
            background-color: #1e1e1e;
            color: #fff;
            line-height: 1.6;
            min-height: 100vh;
        }

        .container {
            max-width: 1200px;
            margin: 0 auto;
            padding: 40px 20px;
        }

        .profile-header {
            display: flex;
            gap: 40px;
            margin-bottom: 50px;
            align-items: flex-start;
        }

        .profile-avatar {
            width: 150px;
            height: 150px;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 48px;
            font-weight: bold;
            color: white;
            flex-shrink: 0;
        }

        .profile-info {
            flex: 1;
        }

        .profile-name {
            font-size: 2.5rem;
            font-weight: 700;
            margin-bottom: 10px;
            color: #fff;
        }

        .profile-email {
            font-size: 1rem;
            color: #00d4ff;
            margin-bottom: 20px;
        }

        .profile-stats {
            display: flex;
            gap: 30px;
            margin: 20px 0;
        }

        .stat-item {
            text-align: center;
            padding: 15px 20px;
            background: rgba(255, 255, 255, 0.05);
            border-radius: 8px;
            min-width: 120px;
        }

        .stat-item.clickable {
            text-decoration: none;
            color: inherit;
            cursor: pointer;
            transition: all 0.3s ease;
        }

        .stat-item.clickable:hover {
            background: rgba(255, 255, 255, 0.1);
            transform: translateY(-2px);
            box-shadow: 0 4px 15px rgba(0, 212, 255, 0.2);
        }

        .stat-item.disabled {
            opacity: 0.6;
            cursor: not-allowed;
        }

        .stat-number {
            display: block;
            font-size: 1.8rem;
            font-weight: bold;
            color: #00d4ff;
        }

        .stat-label {
            font-size: 0.9rem;
            color: #ccc;
            margin-top: 5px;
        }

        .profile-actions {
            margin-top: 20px;
        }

        .btn {
            display: inline-block;
            padding: 12px 24px;
            background: #00d4ff;
            color: #1e1e1e;
            text-decoration: none;
            border-radius: 6px;
            font-weight: 600;
            margin-right: 10px;
            transition: all 0.3s ease;
        }

        .btn:hover {
            background: #00b8e6;
            transform: translateY(-2px);
        }

        .btn-secondary {
            background: rgba(255, 255, 255, 0.1);
            color: #fff;
        }

        .btn-secondary:hover {
            background: rgba(255, 255, 255, 0.2);
        }

        .navigation {
            text-align: center;
            margin-bottom: 30px;
        }

        .nav-link {
            color: #00d4ff;
            text-decoration: none;
            font-size: 1.1rem;
        }

        .nav-link:hover {
            text-decoration: underline;
        }

        .welcome-message {
            text-align: center;
            padding: 40px 20px;
            background: rgba(255, 255, 255, 0.05);
            border-radius: 10px;
            margin-top: 30px;
        }

        .welcome-message h3 {
            font-size: 1.5rem;
            margin-bottom: 10px;
            color: #00d4ff;
        }

        @media (max-width: 768px) {
            .profile-header {
                flex-direction: column;
                text-align: center;
                gap: 20px;
            }

            .profile-stats {
                justify-content: center;
                flex-wrap: wrap;
            }

            .stat-item {
                min-width: 100px;
            }
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="navigation">
            <a href="Home" class="nav-link">&lt; Back to Movies</a>
        </div>

        <div class="profile-header">
            <div class="profile-avatar">
                <% if (profileUser.getProfilePicture() != null && !profileUser.getProfilePicture().isEmpty()) { %>
                    <img src="<%= profileUser.getProfilePicture() %>" alt="<%= profileUser.getUsername() %>'s Profile Picture" style="width: 100%; height: 100%; border-radius: 50%; object-fit: cover;">
                <% } else { %>
                    <%= profileUser.getUsername().substring(0, 1).toUpperCase() %>
                <% } %>
            </div>
            
            <div class="profile-info">
                <h1 class="profile-name"><%= profileUser.getUsername() %></h1>
                <% if (isOwnProfile) { %>
                    <div class="profile-email"><%= profileUser.getEmail() %></div>
                <% } %>
                
                <div class="profile-stats">
                    <a href="watchlist" class="stat-item clickable">
                        <span class="stat-number"><%= watchlistCount %></span>
                        <div class="stat-label">Watchlist</div>
                    </a>
                    <a href="favorites" class="stat-item clickable">
                        <span class="stat-number"><%= favoritesCount %></span>
                        <div class="stat-label">Favorites</div>
                    </a>
                    <a href="reviews" class="stat-item clickable">
                        <span class="stat-number"><%= reviewsCount %></span>
                        <div class="stat-label">Reviews</div>
                    </a>
                    <div class="stat-item disabled">
                        <span class="stat-number">0</span>
                        <div class="stat-label">Lists</div>
                    </div>
                </div>

                <% if (isOwnProfile) { %>
                <div class="profile-actions">
                    <a href="Home" class="btn">Browse Movies</a>
                    <a href="settings" class="btn btn-secondary">Settings</a>
                    <a href="logout" class="btn btn-secondary">Logout</a>
                </div>
                <% } %>
            </div>
        </div>

        <div class="welcome-message">
            <% if (isOwnProfile) { %>
                <h3>Profile Created!</h3>
                <p>Welcome to your MovieMood profile, <%= profileUser.getUsername() %>!</p>
                <p>Start browsing movies to build your watchlist and favorites.</p>
            <% } else { %>
                <h3>User Profile</h3>
                <p>This is <%= profileUser.getUsername() %>'s profile.</p>
                <p>Their movie activity will appear here as they use MovieMood.</p>
            <% } %>
        </div>
    </div>
</body>
</html> 