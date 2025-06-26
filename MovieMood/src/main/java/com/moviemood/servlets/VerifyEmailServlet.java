package com.moviemood.servlets;

import com.moviemood.bean.User;
import com.moviemood.dao.UserDao;
import com.moviemood.exceptions.UserAlreadyExistsException;
import com.moviemood.services.EmailService;
import org.mindrot.jbcrypt.BCrypt;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

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
                request.getRequestDispatcher("login.jsp").forward(request, response);
            }
        } else {
            String error = "Verification code is not correct. Try again.";
            request.setAttribute("error", error);
            request.getRequestDispatcher("verify-email.jsp").forward(request, response);
        }

    }



    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("login.jsp").forward(request, response);
    }
}
