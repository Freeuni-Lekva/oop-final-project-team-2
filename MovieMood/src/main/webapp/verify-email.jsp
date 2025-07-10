<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
  <title>Verify Email - MovieMood</title>
  <link rel="stylesheet" type="text/css" href="assets/css/verify-email.css?v=2">
</head>
<body>
<div class="container">
  <h1>Verify Your Email</h1>

  <%
    String email = (String) session.getAttribute("waitingToVerifyEmail");
    if (email == null) {
      response.sendRedirect("register.jsp");
      return;
    }
  %>

  <div class="info-message">
    You have 10 minutes. 6-digit code has been sent to:<br>
    <strong><%= email %></strong>
  </div>

  <%-- Show error message if exists --%>
  <%
    String error = (String) request.getAttribute("error");
    if (error != null) {
  %>
  <div class="error-message"><%= error %></div>
  <%
    }
  %>

  <%-- Show success message if exists --%>
  <%
    String message = (String) request.getAttribute("message");
    if (message != null) {
  %>
  <div class="success-message"><%= message %></div>
  <%
    }
  %>

  <form method="post" action="verify-email">
    <label for="verificationCode">Enter Verification Code:</label>
    <input type="text" id="verificationCode" name="verification-code"
           maxlength="6" placeholder="000000" required>

    <input type="submit" value="Verify Email">
  </form>

  <div class="resend-link">
    <p>Didn't receive the code? <a href="verify-email?action=resend">Resend Code</a></p>
  </div>
</div>
</body>
</html>