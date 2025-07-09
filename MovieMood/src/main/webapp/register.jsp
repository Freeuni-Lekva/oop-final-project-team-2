<%--
  Created by IntelliJ IDEA.
  User: giorgidzagania
  Date: 6/18/25
--%>

<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Register - MovieMood</title>
    <link rel="stylesheet" type="text/css" href="assets/css/register.css">
</head>
<body>
    <div class="container">
        <h1>Register</h1>

        <%-- Show error message if exists --%>
        <%
            String error = (String) request.getAttribute("error");
            if (error != null) {
        %>
        <div class="error-message"><%= error %></div>
        <%
            }
        %>

        <%
            String keepUsername = (String) request.getAttribute("keepUsername");
            String keepEmail = (String) request.getAttribute("keepEmail");
        %>

        <form method="post" action="register">
            <label for="username">Username:</label>
            <input type="text" id="username" name="username" value="<%= keepUsername != null ? keepUsername : "" %>" required><br/>

            <label for="email">Email:</label>
            <input type="email" id="email" name="email" value="<%= keepEmail != null ? keepEmail : "" %>" required><br/>

            <label for="password">Password:</label>
            <input type="password" id="password" name="password" required><br/>

            <label for="confirmPassword">Confirm Password:</label>
            <input type="password" id="confirmPassword" name="confirmPassword" required><br/>

            <input type="submit" value="Register">
        </form>

        <div class="login-link">
            <p>Already have an account? <a href="login.jsp">Log in here</a>.</p>
        </div>
    </div>
</body>
</html>