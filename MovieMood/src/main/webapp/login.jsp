<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Login - MovieMood</title>
    <link rel="stylesheet" type="text/css" href="assets/css/login.css">
    <script src="https://accounts.google.com/gsi/client" async defer></script>
    <script src="assets/js/login.js"></script>
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
             data-text="sign_in_with"
             data-shape="rectangular"
             data-width="320">
        </div>
    </div>

    <div class="divider">
        <span>or</span>
    </div>

    <h1>Log in</h1>

    <%
        String keepUsername = (String) request.getAttribute("keepUsername");
        String keepPassword = (String) request.getAttribute("keepPassword");
    %>

    <form method="post" action="login">
        <label for="username">Username:</label><br/>
        <input type="text" id="username" name="username" value="<%= keepUsername != null ? keepUsername : "" %>" required><br/>

        <label for="password">Password:</label><br/>
        <input type="password" id="password" name="password" required><br/>

        <div class="remember-me">
            <input type="checkbox" id="rememberMe" name="rememberMe" value="true">
            <label for="rememberMe">Remember me</label>
        </div>

        <input type="submit" value="Log in"><br/>
    </form>

    <div class="signup-link">
        <p>Don't have an account? <a href="register.jsp">Sign Up</a> </p>
    </div>

    <div class="home-link">
        <p><a href="index.jsp">← Back to Home</a></p>
    </div>
</div>
</body>
</html>