<%--
  Navigation Bar Component
  Created by IntelliJ IDEA.
  User: Nikoloz
  Date: 7/7/2025
  Time: 12:00 PM
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<header class="header">
    <div class="container">
        <nav class="nav">
            <div class="logo">
                <img src=<%=request.getContextPath()%>"/Images/logo.png" alt="MovieMood Logo">
                <span class="logo-text">MovieMood</span>
            </div>
            <div class="nav-links">
                <a href="/films">Films</a>
                <a href="#">Popular Lists</a>
                <a href="/login" class="create-account-btn">Create Account</a>
                <%--need this tab for testing for now gotta change later --%>
                <a href="/friend-requests">Friends</a>
            </div>
        </nav>
    </div>
</header>