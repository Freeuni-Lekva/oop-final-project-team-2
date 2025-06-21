package com.moviemood.servlets;

import com.moviemood.bean.User;
import com.moviemood.dao.UserDao;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate(); // clear session
        }

        // Now this will remove users remmeber_token cookie from browser
        Cookie[] cookies = request.getCookies();
        if (cookies != null){
            for (Cookie cookie : cookies) {
                if ("remember_token".equals(cookie.getName())) {
                    ServletContext context = request.getServletContext();
                    UserDao userDao = (UserDao) context.getAttribute("userDao");
                    User user = userDao.getUserByToken(cookie.getValue());
                    if (user != null) {
                        // delete cookie
                        Cookie deleteCookie = new Cookie("remember_token", "");
                        deleteCookie.setMaxAge(0);
                        deleteCookie.setHttpOnly(true);
                        response.addCookie(deleteCookie);

                        // delete token from database
                        userDao.updateRememberToken(user.getUsername(), null);
                        break;
                    }
                }
            }
        }

        response.sendRedirect("login.jsp");

    }
}
