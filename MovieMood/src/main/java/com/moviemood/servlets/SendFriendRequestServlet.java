package com.moviemood.servlets;

import com.moviemood.dao.FriendRequestDao;
import com.moviemood.dao.UserDao;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Handles sending friendRequest to another user.
 */

@WebServlet("/send-friend-request")
public class SendFriendRequestServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //need to change attribute name here based on giorgi's login implementation
        String username = (String) request.getSession().getAttribute("user");
        if(username == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        int receiverId = Integer.parseInt(request.getParameter("receiverId"));

        FriendRequestDao friendRequestDAO = (FriendRequestDao) getServletContext().getAttribute("friendRequestDao");
        UserDao userDao = (UserDao) getServletContext().getAttribute("userDao");

        try {
            int senderId = userDao.getUserByUsername(username).getId();
            friendRequestDAO.sendRequest(senderId, receiverId);
            response.setStatus(HttpServletResponse.SC_OK);
        }catch(Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            throw new ServletException("Failed to send friend request", e);
        }

    }
}
