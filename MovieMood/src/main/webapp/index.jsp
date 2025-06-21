<%@ page import="com.moviemood.bean.User" %>

<%
    User user = (User) session.getAttribute("user");
%>

<html>
<body>
<h2>Hello</h2>

<% if (user != null) { %>
<p>You are logged in as <%= user.getUsername() %></p>
<p>Wanna see movies? <a href="movies.jsp">Click here</a></p>
<a href="logout">Logout</a>
<% } else { %>
<p>You are not logged in. <a href="login.jsp">Login here</a></p>
<p>Create account: <a href="register.jsp">Click here:)</a>.</p>
<% } %>

</body>
</html>