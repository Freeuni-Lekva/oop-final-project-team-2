<%--
  Created by IntelliJ IDEA.
  User: giorgidzagania
  Date: 6/18/25
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Login - MovieMood</title>
    <link rel="stylesheet" type="text/css" href="assets/css/login.css">
</head>
<body>
    <div class="container">
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
            String keepPassword = (String) request.getAttribute("keepPassword");
        %>

        <form method="post" action="login">
            <label for="username">Username:</label><br/>
            <input type="text" id="username" name="username" value="<%= keepUsername != null ? keepUsername : "" %>" required><br/>

            <label for="password">Password:</label><br/>
            <input type="password" id="password" name="password" required><br/>

            <input type="submit" value="Log in"><br/>

            <div class="remember-me">
                <input type="checkbox" id="rememberMe" name="rememberMe" value="true">
                <label for="rememberMe">Remember me</label>
            </div>

        </form>

        <div class="signup-link">
            <p>Don't have an account? <a href="register.jsp">Sign Up</a> </p>
        </div>
    </div>
</body>
</html>
