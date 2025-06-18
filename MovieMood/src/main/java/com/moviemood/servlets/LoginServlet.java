package com.moviemood.servlets;

import com.moviemood.bean.User;
import com.moviemood.dao.UserDao;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.mindrot.jbcrypt.BCrypt;


/**
 * Not implemented yet!!!
 */
@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

//        UserDao userDao = (UserDao) getServletContext().getAttribute("userDao");
//        String username = request.getParameter("username");
//        String hashedPassword = request.getParameter("password");
//
//        String error = null;


        request.getRequestDispatcher("login.jsp").forward(request, response);
    }


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {


        request.getRequestDispatcher("login.jsp").forward(request, response);
    }

}
