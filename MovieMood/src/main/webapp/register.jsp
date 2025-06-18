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
</head>
<body>
<h1>Register</h1>

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
    String keepEmail = (String) request.getAttribute("keepEmail");
    String keepPassword = (String) request.getAttribute("keepPassword");
%>

<form method="post" action="register">
    <label for="username">Username:</label><br/>
    <input type="text" id="username" name="username" value="<%= keepUsername != null ? keepUsername : "" %>" required><br/>

    <label for="email">Email:</label><br/>
    <input type="email" id="email" name="email" value="<%= keepEmail != null ? keepEmail : "" %>" required><br/>

    <label for="password">Password:</label><br/>
    <input type="password" id="password" name="password" value="<%= keepPassword != null ? keepPassword : "" %>" required><br/>

    <input type="submit" value="Register">
</form>

<p>Already have an account? <a href="login.jsp">Log in here</a>.</p>
</body>
</html>