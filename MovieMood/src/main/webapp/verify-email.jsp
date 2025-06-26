<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
  <title>Verify Email - MovieMood</title>
  <style>
    * {
      margin: 0;
      padding: 0;
      box-sizing: border-box;
    }

    body {
      font-family: Arial, sans-serif;
      background-color: #f5f5f5;
      min-height: 100vh;
      display: flex;
      justify-content: center;
      align-items: center;
    }

    .container {
      background-color: white;
      padding: 40px;
      border-radius: 10px;
      box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
      width: 100%;
      max-width: 400px;
    }

    h1 {
      margin-bottom: 20px;
      color: #333;
      text-align: center;
    }

    .info-message {
      margin-bottom: 30px;
      padding: 15px;
      background-color: #e3f2fd;
      border: 1px solid #2196f3;
      border-radius: 5px;
      color: #1976d2;
      text-align: center;
    }

    .error-message {
      color: red;
      margin-bottom: 20px;
      padding: 10px;
      background-color: #ffe6e6;
      border: 1px solid #ffcccc;
      border-radius: 5px;
      text-align: center;
    }

    .success-message {
      color: green;
      margin-bottom: 20px;
      padding: 10px;
      background-color: #e8f5e8;
      border: 1px solid #4caf50;
      border-radius: 5px;
      text-align: center;
    }

    form {
      text-align: left;
    }

    label {
      display: block;
      margin-bottom: 5px;
      font-weight: bold;
      color: #555;
    }

    input[type="text"] {
      width: 100%;
      padding: 12px;
      margin-bottom: 20px;
      border: 1px solid #ddd;
      border-radius: 5px;
      font-size: 16px;
      text-align: center;
      letter-spacing: 2px;
    }

    input[type="text"]:focus {
      outline: none;
      border-color: #007bff;
      box-shadow: 0 0 5px rgba(0, 123, 255, 0.3);
    }

    input[type="submit"] {
      width: 100%;
      padding: 12px;
      background-color: #007bff;
      color: white;
      border: none;
      border-radius: 5px;
      font-size: 16px;
      cursor: pointer;
      transition: background-color 0.3s;
      margin-bottom: 20px;
    }

    input[type="submit"]:hover {
      background-color: #0056b3;
    }

    .resend-link {
      text-align: center;
    }

    .resend-link a {
      color: #007bff;
    }

    .resend-link a:hover {
      text-decoration: underline;
    }
  </style>
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