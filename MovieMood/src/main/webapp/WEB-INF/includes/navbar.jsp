<%--
  Navigation Bar Component
  Created by IntelliJ IDEA.
  User: Nikoloz
  Date: 7/7/2025
  Time: 12:00 PM
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.moviemood.bean.User" %>

<header class="header">
    <div class="container">
        <nav class="nav">
            <div class="logo">
                <a href="/Home">
                    <img src=<%=request.getContextPath()%>"/Images/logo.png" alt="MovieMood Logo">
                    <span class="logo-text">MovieMood</span>
                </a>
            </div>
            <div class="nav-links">
                <%
                    User currentUser = (User) session.getAttribute("user");
                    if (currentUser != null) {
                %>
                    <a href="/friend-requests">Friends</a>
                    <a href="/friends-activity">Activity</a>
                    <a href="/profile" class="create-account-btn">Profile</a>
                <%
                    } else {
                %>
                    <a href="/login" class="create-account-btn">Login</a>
                <%
                    }
                %>
            </div>
        </nav>
    </div>
</header>