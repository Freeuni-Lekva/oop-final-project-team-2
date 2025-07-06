package com.moviemood.servlets;

import com.moviemood.bean.User;
import com.moviemood.dao.UserDao;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

import org.mindrot.jbcrypt.BCrypt;


/**
 * Handels login
 */
@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        UserDao userDao = (UserDao) getServletContext().getAttribute("userDao");
        String username = request.getParameter("username");
        String rawPassword = request.getParameter("password");

        User user = userDao.getUserByUsername(username);
        if (user != null && BCrypt.checkpw(rawPassword, user.getHashedPassword())) {
            // Check if user is verified
            if (!user.isVerified()) {
                request.getSession().setAttribute("waitingToVerifyEmail", user.getEmail());
                response.sendRedirect("verify-email.jsp");
                return;
            }
            request.getSession().setAttribute("user", user);
            if ("true".equals(request.getParameter("rememberMe"))) {
                String token = UUID.randomUUID().toString(); // generate random token
                userDao.updateRememberToken(username, token);

                Cookie cookie = new Cookie("remember_token", token);
                cookie.setMaxAge(60 * 60 * 24 * 30); // 30 days
                cookie.setHttpOnly(true);
                response.addCookie(cookie);
            }

            response.sendRedirect("/Home");
            return;
        }
        request.setAttribute("error", "Invalid username or password");
        request.setAttribute("keepUsername", username);
        //request.setAttribute("keepPassword", rawPassword);
        request.getRequestDispatcher("login.jsp").forward(request, response);


    }


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("login.jsp").forward(request, response);
    }

}
