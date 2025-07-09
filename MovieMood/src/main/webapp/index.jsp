<%@ page import="com.moviemood.bean.User" %>

<%
    User user = (User) session.getAttribute("user");
%>

<html>
<head>
    <title>MovieMood - Home</title>
    <link rel="stylesheet" type="text/css" href="assets/css/index.css">
</head>
<body>
<div class="container">
    <h2>Hello</h2>

    <% if (user != null) { %>
    <div class="welcome-message">
        <p>Welcome back, <strong><%= user.getUsername() %></strong>!</p>
    </div>

    <div class="action-buttons">
        <a href="/Home" class="btn btn-success">Browse Movies</a>
    </div>

    <div class="divider"></div>

    <a href="logout" class="btn btn-danger">Logout</a>

    <% } else { %>
    <div class="guest-section">
        <p>Welcome to MovieMood!</p>
        <p>Please log in to discover your perfect movie match.</p>

        <div class="action-buttons">
            <a href="login.jsp" class="btn">Login</a>
            <a href="register.jsp" class="btn btn-secondary">Create Account</a>
        </div>
    </div>
    <% } %>
</div>
</body>
</html>