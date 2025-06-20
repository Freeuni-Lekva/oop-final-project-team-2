package com.moviemood.servlets;

import com.moviemood.bean.User;
import com.moviemood.dao.UserDao;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import com.moviemood.exceptions.UserAlreadyExistsException;
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
        String hashedPassword = BCrypt.hashpw(rawPassword, BCrypt.gensalt());
        String error = null;

        try {
            userDao.insertUser(username, email, hashedPassword);
            response.sendRedirect("login.jsp");
            return;
        } catch (UserAlreadyExistsException e) {
            error = e.getMessage();
//            request.setAttribute("error", error);
//            request.setAttribute("keepUsername", username);
//            request.setAttribute("keepEmail", email);
//            request.setAttribute("keepPassword", rawPassword);
//            request.getRequestDispatcher("register.jsp").forward(request, response);
        }  catch (Exception e) {
            error = "Something went wrong. Please try again.";
//            request.setAttribute("error", error);
//            request.setAttribute("keepUsername", username);
//            request.setAttribute("keepEmail", email);
//            request.setAttribute("keepPassword", rawPassword);
//            request.getRequestDispatcher("register.jsp").forward(request, response);
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
