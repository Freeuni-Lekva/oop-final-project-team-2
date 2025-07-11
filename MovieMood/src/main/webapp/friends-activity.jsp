<%@ page import="com.moviemood.bean.FriendActivity" %>
<%@ page import="java.util.List" %>
<%@ page import="com.moviemood.bean.User" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Friends' Activity - MovieMood</title>
    <link rel="stylesheet" href="assets/css/navbar.css?v=<%= System.currentTimeMillis() %>">
    <link rel="stylesheet" href="assets/css/friend-requests.css?v=<%= System.currentTimeMillis() %>">
    <link rel="stylesheet" href="assets/css/friends-activity.css?v=<%= System.currentTimeMillis() %>">
</head>
<body>
    <jsp:include page="/WEB-INF/includes/navbar.jsp" />

    <div class="activity-feed-container">
        <a href="friend-requests" class="back-btn">← Back to Friends</a>
        
        <h1><span class="highlight">Friends'</span> Activity</h1>
        <p>See what your friends have been up to recently</p>

        <%
            String error = (String) request.getAttribute("error");
            if (error != null) {
        %>
                         <div class="error-message">
                 <%= error %>
             </div>
        <%
            }
        %>

        <%
            List<FriendActivity> activities = (List<FriendActivity>) request.getAttribute("activities");
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("MMM d, yyyy 'at' h:mm a");
            
            if (activities != null && !activities.isEmpty()) {
        %>
            <div class="activity-feed">
                <%
                    for (FriendActivity activity : activities) {
                %>
                    <div class="activity-item">
                        <div class="activity-header">
                            <div class="activity-avatar">
                                <% if (activity.getProfilePicture() != null && !activity.getProfilePicture().isEmpty()) { %>
                                    <img src="/profile-picture/<%= activity.getProfilePicture() %>" alt="<%= activity.getUsername() %>'s Profile Picture">
                                <% } else { %>
                                    <div class="default-avatar"><%= activity.getUsername().substring(0, 1).toUpperCase() %></div>
                                <% } %>
                            </div>
                            <div class="activity-info">
                                <div class="activity-username"><%= activity.getUsername() %></div>
                                <div class="activity-time"><%= activity.getTimestamp().format(timeFormatter) %></div>
                            </div>
                        </div>
                        
                        <div class="activity-content">
                            <span class="activity-action"><%= activity.getActivityDescription() %></span>
                            
                            <% if (activity.getMovieId() > 0 && activity.getMovieTitle() != null) { %>
                                <a href="/movie/details?id=<%= activity.getMovieId() %>" class="movie-link">
                                    <%= activity.getMovieTitle() %>
                                </a>
                            <% } %>
                            
                            <% if ("created_list".equals(activity.getActivityType()) && activity.getListName() != null) { %>
                                <span class="list-name">"<%= activity.getListName() %>"</span>
                                <% if (activity.getAdditionalInfo() != null && !activity.getAdditionalInfo().trim().isEmpty()) { %>
                                    <div class="review-preview">
                                        <%= activity.getAdditionalInfo() %>
                                    </div>
                                <% } %>
                            <% } %>
                            
                            <% if ("reviewed".equals(activity.getActivityType()) && activity.getAdditionalInfo() != null) { %>
                                <div class="review-preview">
                                    "<%= activity.getAdditionalInfo() %>..."
                                </div>
                            <% } %>
                        </div>
                    </div>
                <%
                    }
                %>
            </div>
        <%
            } else {
        %>
            <div class="empty-feed">
                <h3>No Recent Activity</h3>
                <p>Your friends haven't been active recently, or you don't have any friends yet.</p>
                <p>Start connecting with people to see their movie activities here!</p>
                                 <a href="friend-requests?tab=suggestions" class="back-btn">Find Friends</a>
            </div>
        <%
            }
        %>
    </div>
</body>
</html> 