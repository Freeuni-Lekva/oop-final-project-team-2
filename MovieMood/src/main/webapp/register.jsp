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
    <style>
        /* Common styles for all pages */
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        body {
            font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
            background: linear-gradient(135deg, #1a1a2e 0%, #16213e 50%, #0f172a 100%);
            min-height: 100vh;
            display: flex;
            justify-content: center;
            align-items: center;
            color: #ffffff;
        }

        .container {
            background: rgba(30, 41, 59, 0.95);
            backdrop-filter: blur(10px);
            border: 1px solid rgba(255, 255, 255, 0.1);
            padding: 40px;
            border-radius: 16px;
            box-shadow: 0 20px 40px rgba(0, 0, 0, 0.3);
            width: 100%;
            max-width: 450px;
            text-align: center;
        }

        /* Header styles */
        h1, h2 {
            margin-bottom: 30px;
            color: #ffffff;
            font-weight: 600;
            font-size: 2.2em;
        }

        .logo {
            display: flex;
            align-items: center;
            justify-content: center;
            margin-bottom: 30px;
            font-size: 1.8em;
            font-weight: bold;
            color: #ffffff;
        }

        .logo::before {
            content: "🎬";
            margin-right: 10px;
        }

        /* Welcome message styles */
        .welcome-message {
            margin-bottom: 25px;
            font-size: 1.2em;
            color: #e2e8f0;
        }

        .welcome-message strong {
            color: #f59e0b;
        }

        /* Form styles */
        form {
            text-align: left;
        }

        label {
            display: block;
            margin-bottom: 8px;
            font-weight: 500;
            color: #e2e8f0;
            font-size: 14px;
        }

        input[type="text"],
        input[type="email"],
        input[type="password"] {
            width: 100%;
            padding: 14px 16px;
            margin-bottom: 20px;
            background: rgba(15, 23, 42, 0.8);
            border: 1px solid rgba(255, 255, 255, 0.2);
            border-radius: 8px;
            font-size: 16px;
            color: #ffffff;
            transition: all 0.3s ease;
        }

        input[type="text"]:focus,
        input[type="email"]:focus,
        input[type="password"]:focus {
            outline: none;
            border-color: #f59e0b;
            box-shadow: 0 0 0 3px rgba(245, 158, 11, 0.1);
            background: rgba(15, 23, 42, 0.9);
        }

        input[type="text"]::placeholder,
        input[type="email"]::placeholder,
        input[type="password"]::placeholder {
            color: #94a3b8;
        }

        /* Button styles */
        .btn,
        input[type="submit"] {
            display: inline-block;
            padding: 14px 28px;
            margin: 10px;
            background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%);
            color: #ffffff;
            text-decoration: none;
            border: none;
            border-radius: 8px;
            font-size: 16px;
            font-weight: 600;
            cursor: pointer;
            transition: all 0.3s ease;
            text-align: center;
            box-shadow: 0 4px 15px rgba(245, 158, 11, 0.2);
        }

        .btn:hover,
        input[type="submit"]:hover {
            background: linear-gradient(135deg, #d97706 0%, #b45309 100%);
            transform: translateY(-2px);
            box-shadow: 0 6px 20px rgba(245, 158, 11, 0.3);
        }

        input[type="submit"] {
            width: 100%;
            margin: 20px 0;
        }

        /* Secondary button styles */
        .btn-secondary {
            background: linear-gradient(135deg, #475569 0%, #334155 100%);
            box-shadow: 0 4px 15px rgba(71, 85, 105, 0.2);
        }

        .btn-secondary:hover {
            background: linear-gradient(135deg, #334155 0%, #1e293b 100%);
            box-shadow: 0 6px 20px rgba(71, 85, 105, 0.3);
        }

        /* Danger button styles */
        .btn-danger {
            background: linear-gradient(135deg, #dc2626 0%, #b91c1c 100%);
            box-shadow: 0 4px 15px rgba(220, 38, 38, 0.2);
        }

        .btn-danger:hover {
            background: linear-gradient(135deg, #b91c1c 0%, #991b1b 100%);
            box-shadow: 0 6px 20px rgba(220, 38, 38, 0.3);
        }

        /* Success button styles */
        .btn-success {
            background: linear-gradient(135deg, #059669 0%, #047857 100%);
            box-shadow: 0 4px 15px rgba(5, 150, 105, 0.2);
        }

        .btn-success:hover {
            background: linear-gradient(135deg, #047857 0%, #065f46 100%);
            box-shadow: 0 6px 20px rgba(5, 150, 105, 0.3);
        }

        /* Action buttons container */
        .action-buttons {
            margin: 30px 0;
        }

        /* Remember me checkbox */
        .remember-me {
            display: flex;
            align-items: center;
            margin-bottom: 20px;
        }

        .remember-me input[type="checkbox"] {
            margin-right: 10px;
            width: auto;
            accent-color: #f59e0b;
        }

        .remember-me label {
            margin-bottom: 0;
            font-weight: normal;
            cursor: pointer;
            color: #e2e8f0;
            font-size: 14px;
        }

        /* Error message styles */
        .error-message {
            color: #fecaca;
            margin-bottom: 20px;
            padding: 12px 16px;
            background: rgba(220, 38, 38, 0.1);
            border: 1px solid rgba(220, 38, 38, 0.3);
            border-radius: 8px;
            font-size: 14px;
        }

        /* Link styles */
        .signup-link,
        .login-link {
            text-align: center;
            margin-top: 20px;
        }

        .signup-link p,
        .login-link p {
            color: #94a3b8;
            font-size: 14px;
        }

        .signup-link a,
        .login-link a {
            color: #f59e0b;
            text-decoration: none;
            font-weight: 500;
            transition: color 0.3s ease;
        }

        .signup-link a:hover,
        .login-link a:hover {
            color: #d97706;
            text-decoration: underline;
        }

        /* Guest section */
        .guest-section {
            margin-top: 20px;
        }

        .guest-section p {
            margin-bottom: 15px;
            color: #e2e8f0;
            font-size: 1.1em;
        }

        /* Divider */
        .divider {
            margin: 30px 0;
            border-bottom: 1px solid rgba(255, 255, 255, 0.1);
        }

        /* Responsive design */
        @media (max-width: 600px) {
            .container {
                padding: 30px 20px;
                margin: 20px;
            }

            .btn {
                display: block;
                margin: 10px 0;
                width: 100%;
            }

            h1, h2 {
                font-size: 1.8em;
            }
        }
    </style>
</head>
<body>
    <div class="container">
        <h1>Register</h1>

        <%-- Show error message if exists --%>
        <%
            String error = (String) request.getAttribute("error");
            if (error != null) {
        %>
        <div class="error-message"><%= error %></div>
        <%
            }
        %>

        <%
            String keepUsername = (String) request.getAttribute("keepUsername");
            String keepEmail = (String) request.getAttribute("keepEmail");
        %>

        <form method="post" action="register">
            <label for="username">Username:</label>
            <input type="text" id="username" name="username" value="<%= keepUsername != null ? keepUsername : "" %>" required><br/>

            <label for="email">Email:</label>
            <input type="email" id="email" name="email" value="<%= keepEmail != null ? keepEmail : "" %>" required><br/>

            <label for="password">Password:</label>
            <input type="password" id="password" name="password" required><br/>

            <label for="confirmPassword">Confirm Password:</label>
            <input type="password" id="confirmPassword" name="confirmPassword" required><br/>

            <input type="submit" value="Register">
        </form>

        <div class="login-link">
            <p>Already have an account? <a href="login.jsp">Log in here</a>.</p>
        </div>
    </div>
</body>
</html>