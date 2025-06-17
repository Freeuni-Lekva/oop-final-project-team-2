package com.moviemood.servlets;


import com.moviemood.dao.UserDao;
import com.moviemood.bean.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebListener;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;


/**
 * Temporary test for existing features (This test is about User, UserDao).
 * will be deleted soon.
 */
@WebServlet("/test-user")
public class TestServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        UserDao userDao = (UserDao) getServletContext().getAttribute("userDao");

        response.setContentType("text/plain");
        PrintWriter out = response.getWriter();

        try {
            // Insert test
            userDao.insertUser("tester", "tester@example.com", "hashedpass");

            // Retrieve test
            User user = userDao.getUserByEmail("tester@example.com");

            if (user != null) {
                out.println("User found:");
                out.println("Username: " + user.getUsername());
                out.println("Email: " + user.getEmail());
            } else {
                out.println("User not found.");
            }

        } catch (Exception e) {
            e.printStackTrace(out);
        }
    }
}