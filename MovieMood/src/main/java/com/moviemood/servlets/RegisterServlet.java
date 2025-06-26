package com.moviemood.servlets;

import com.moviemood.bean.User;
import com.moviemood.dao.UserDao;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.CompletableFuture;

import com.moviemood.exceptions.UserAlreadyExistsException;
import com.moviemood.services.EmailService;
import org.mindrot.jbcrypt.BCrypt;

/**
 * Handles register.
 */
@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        UserDao userDao = (UserDao) getServletContext().getAttribute("userDao");
        String username = request.getParameter("username");
        String email = request.getParameter("email");
        String rawPassword = request.getParameter("password");
        String confirmRawPassword = request.getParameter("confirmPassword");
        String error = null;

        // Check if passwords match
        if (!rawPassword.equals(confirmRawPassword)) {
            error = "Passwords do not match.";
            request.setAttribute("error", error);
            request.setAttribute("keepUsername", username);
            request.setAttribute("keepEmail", email);
            request.getRequestDispatcher("register.jsp").forward(request, response);
            return;
        }

        String hashedPassword = BCrypt.hashpw(rawPassword, BCrypt.gensalt());

        // Generate verification code and calculate expiry time
        EmailService emailService = new EmailService();
        String verificationCode = String.valueOf(emailService.generateVerificationCode());

        LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(10);
        Timestamp expiry = Timestamp.valueOf(expiryTime);

        try {

            userDao.insertUser(username, email, hashedPassword, verificationCode, expiry);
            request.getSession().setAttribute("waitingToVerifyEmail", email);

            // sending email asynchronously in background
            CompletableFuture.runAsync(() -> {
                EmailService asyncEmailService = new EmailService();
                asyncEmailService.sendVerificationEmail(email, verificationCode, username);
            });

            response.sendRedirect("verify-email.jsp");
            return;

        } catch (UserAlreadyExistsException e) {
            error = e.getMessage();
        }  catch (Exception e) {
            error = "Something went wrong. Please try again.";
        }

        request.setAttribute("error", error);
        request.setAttribute("keepUsername", username);
        request.setAttribute("keepEmail", email);
        request.getRequestDispatcher("register.jsp").forward(request, response);

    }



    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("register.jsp").forward(request, response);
    }

}
