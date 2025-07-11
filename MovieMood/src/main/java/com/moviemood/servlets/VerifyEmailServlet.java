package com.moviemood.servlets;

import com.moviemood.bean.User;
import com.moviemood.dao.UserDao;
import com.moviemood.exceptions.UserAlreadyExistsException;
import com.moviemood.services.EmailService;
import org.mindrot.jbcrypt.BCrypt;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;

@WebServlet("/verify-email")
public class VerifyEmailServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String verificationCode = request.getParameter("verification-code");
        UserDao userDao = (UserDao) getServletContext().getAttribute("userDao");

        String email = (String) request.getSession().getAttribute("waitingToVerifyEmail");
        if (email == null) {
            response.sendRedirect("register.jsp");
            return;
        }

        User user = (User) userDao.getUserByEmail(email);
        if (user == null) {
            response.sendRedirect("register.jsp");
            return;
        }

        // Checking if time is expired.
        Timestamp now = Timestamp.valueOf(LocalDateTime.now());
        Timestamp expiry = user.getVerificationCodeExpiry();

        if (now.after(expiry)) {
            String error = "Verification code has expired. Please request a new one.";
            request.setAttribute("error", error);
            request.getRequestDispatcher("verify-email.jsp").forward(request, response);
            return;
        }

        if (verificationCode.equals(user.getVerificationCode())) {
            boolean success = userDao.verifyUser(user.getId());
            if (success) {
                user.setVerified(true);
                request.getSession().removeAttribute("waitingToVerifyEmail");
                request.getSession().setAttribute("user", user);

                // Handle remember-me
                String rememberMe = (String) request.getSession().getAttribute("rememberMe");
                if ("true".equals(rememberMe)) {
                    String token = UUID.randomUUID().toString(); // generate random token
                    userDao.updateRememberToken(user.getUsername(), token);

                    Cookie cookie = new Cookie("remember_token", token);
                    cookie.setMaxAge(60 * 60 * 24 * 30); // 30 days
                    cookie.setHttpOnly(true);
                    response.addCookie(cookie);

                    // Clean up session
                    request.getSession().removeAttribute("rememberMe");
                }

                response.sendRedirect("/movie-preferences");
            }
        } else {
            String error = "Verification code is not correct. Try again.";
            request.setAttribute("error", error);
            request.getRequestDispatcher("verify-email.jsp").forward(request, response);
        }

    }



    /*
     * If user clicks resend, doGet handles resending,
     * else it just redirects to jsp file.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");

        if ("resend".equals(action)) {
            String email = (String) request.getSession().getAttribute("waitingToVerifyEmail");

            if (email != null) {
                UserDao userDao = (UserDao) getServletContext().getAttribute("userDao");
                User user = userDao.getUserByEmail(email);

                if (user != null) {
                    // generate new code
                    EmailService emailService = new EmailService();
                    String newCode = String.valueOf(emailService.generateVerificationCode());
                    Timestamp newExpiry = Timestamp.valueOf(LocalDateTime.now().plusMinutes(10));

                    userDao.updateVerificationCode(email, newCode, newExpiry);
                    emailService.sendVerificationEmail(email, newCode, user.getUsername());

                    request.setAttribute("message", "New verification code sent!");
                }
            }

            request.getRequestDispatcher("verify-email.jsp").forward(request, response);
        } else {
            request.getRequestDispatcher("verify-email.jsp").forward(request, response);
        }
    }
}
