package com.moviemood.servlets;

import com.moviemood.bean.FriendRequest;
import com.moviemood.dao.FriendRequestDao;
import com.moviemood.dao.UserDao;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/friend-requests")
public class FriendRequestsServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getSession().getAttribute("username").toString();

        try {
            FriendRequestDao friendRequestDao =  (FriendRequestDao) getServletContext().getAttribute("friendRequestDao");
            UserDao userDao = (UserDao) getServletContext().getAttribute("userDao");

            int userId = userDao.getUserByUsername(username).getId();

            List<FriendRequest> incomingRequests = friendRequestDao.getIncomingRequests(userId);
            List<FriendRequest> sentRequests = friendRequestDao.getSentRequests(userId);

            request.setAttribute("incomingRequests", incomingRequests);
            request.setAttribute("sentRequests", sentRequests);
            request.getRequestDispatcher("friend-requests.jsp").forward(request, response);

        }catch(Exception e) {
            e.printStackTrace();
            throw new ServletException("Failed to load friend requests");
        }
    }
}
