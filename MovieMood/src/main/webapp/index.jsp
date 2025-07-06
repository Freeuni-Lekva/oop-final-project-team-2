<%@ page import="com.moviemood.bean.User" %>

<%
    User user = (User) session.getAttribute("user");
%>

<html>
<head>
    <title>MovieMood - Home</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        body {
            font-family: Arial, sans-serif;
            background-color: #f5f5f5;
            min-height: 100vh;
            display: flex;
            justify-content: center;
            align-items: center;
        }

        .container {
            background-color: white;
            padding: 40px;
            border-radius: 10px;
            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
            width: 100%;
            max-width: 500px;
            text-align: center;
        }

        h2 {
            margin-bottom: 30px;
            color: #333;
            font-size: 2.5em;
        }

        .welcome-message {
            margin-bottom: 20px;
            font-size: 1.2em;
            color: #555;
        }

        .welcome-message strong {
            color: #007bff;
        }

        .action-buttons {
            margin: 30px 0;
        }

        .btn {
            display: inline-block;
            padding: 12px 24px;
            margin: 10px;
            background-color: #007bff;
            color: white;
            text-decoration: none;
            border-radius: 5px;
            font-size: 16px;
            font-weight: bold;
            transition: background-color 0.3s, transform 0.2s;
        }

        .btn:hover {
            background-color: #0056b3;
            transform: translateY(-2px);
        }

        .btn-secondary {
            background-color: #6c757d;
        }

        .btn-secondary:hover {
            background-color: #545b62;
        }

        .btn-danger {
            background-color: #dc3545;
        }

        .btn-danger:hover {
            background-color: #c82333;
        }

        .btn-success {
            background-color: #28a745;
        }

        .btn-success:hover {
            background-color: #218838;
        }

        .guest-section {
            margin-top: 20px;
        }

        .guest-section p {
            margin-bottom: 15px;
            color: #666;
            font-size: 1.1em;
        }

        .divider {
            margin: 20px 0;
            border-bottom: 1px solid #eee;
        }

        @media (max-width: 600px) {
            .btn {
                display: block;
                margin: 10px 0;
                width: 100%;
            }
        }
    </style>
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