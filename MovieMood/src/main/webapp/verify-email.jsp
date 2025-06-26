<%--
  Created by IntelliJ IDEA.
  User: giorgidzagania
  Date: 6/26/25
  Time: 13:09
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Verify Email - MovieMood</title>
</head>
<body>

  <%
    String pendingEmail = (String) session.getAttribute("waitingToVerifyEmail");
    if (pendingEmail == null) {
      response.sendRedirect("register.jsp");
      return;
    }
  %>

  <%-- Show error message if exists --%>
  <%
    String error = (String) request.getAttribute("error");
    if (error != null) {
  %>
  <p style="color:red;"><%= error %></p>
  <%
    }
  %>

  <p>Code was sent to: <<%= pendingEmail %></p><br/>

  <form method="post" action="verify-email">
    <label>Write code here:</label>
    <input type="text" name="verification-code" maxlength="6" placeholder="000000" required><br/>

    <input type = "submit" value="submit"><br/>
  </form>

</body>
</html>
