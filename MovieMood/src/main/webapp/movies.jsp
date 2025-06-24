<%--
  Created by IntelliJ IDEA.
  User: giorgidzagania
  Date: 6/18/25
  Time: 17:47
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.moviemood.bean.User" %>

<%
    User user = (User) session.getAttribute("user");
    if (user == null) {
        response.sendRedirect("login.jsp");
        return;
    }
%>
<html>

<head>
    <title>Movies - MovieMood</title>
    <style>
        body {
            text-align: center;
            margin: 0;
            padding: 20px;
        }

        .logout-btn {
            position: absolute;
            top: 20px;
            right: 20px;
        }
    </style>
</head>
<body>
    <a href="logout" class="logout-btn">Logout</a>
    <p>Hello <%= user.getUsername() %></p>
    <p>Movies Page</p>
</body>
</html>
