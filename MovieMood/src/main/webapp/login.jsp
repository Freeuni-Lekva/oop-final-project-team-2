<%--
  Created by IntelliJ IDEA.
  User: giorgidzagania
  Date: 6/18/25
--%>

<%-- Not implemented yet!! --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Login - MovieMood</title>
</head>
<body>
    <%-- Show error message if exists --%>
    <%
        String error = (String) request.getAttribute("error");
        if (error != null) {
    %>
    <p style="color:red;"><%= error %></p>
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
        <input type="checkbox" name="rememberMe" value="true"> Remember me

    </form>

    <p>Don't have an account? <a href="register.jsp">Sign Up</a> </p>
</body>
</html>
