
<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Register - MovieMood</title>
    <link rel="stylesheet" href="assets/css/register.css?v=2">
    <script src="https://accounts.google.com/gsi/client" async defer></script>
    <script src="assets/js/register.js"></script>
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

    <!-- Google Sign-In Button -->
    <div class="google-signin-container">
        <div id="g_id_onload"
             data-client_id="632596920546-o05uh5426a18fv57pd73vhodlp5jon7n.apps.googleusercontent.com"
             data-callback="handleCredentialResponse"
             data-auto_prompt="false">
        </div>
        <div class="g_id_signin"
             data-type="standard"
             data-size="large"
             data-theme="filled_blue"
             data-text="signup_with"
             data-shape="rectangular"
             data-width="320">
        </div>
    </div>

    <div class="divider">
        <span>or</span>
    </div>

    <h1>Register</h1>

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

        <div class="checkbox-group">
            <input type="checkbox" id="rememberMe" name="rememberMe" value="true">
            <label for="rememberMe">Remember me</label>
        </div>

        <input type="submit" value="Register">
    </form>

    <div class="login-link">
        <p>Already have an account? <a href="login.jsp">Log in here</a>.</p>
    </div>
</div>
</body>
</html>