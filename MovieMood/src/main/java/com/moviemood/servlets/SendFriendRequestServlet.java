package com.moviemood.servlets;

import com.moviemood.bean.User;
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
        User user = (User) request.getSession().getAttribute("user");
        if(user == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String receiverIdParam = request.getParameter("receiverId");
        String receiverUsernameParam = request.getParameter("receiverUsername");
        int receiverId;
        
        FriendRequestDao friendRequestDAO = (FriendRequestDao) getServletContext().getAttribute("friendRequestDao");
        UserDao userDao = (UserDao) getServletContext().getAttribute("userDao");

        try {
            if (receiverIdParam != null && !receiverIdParam.isEmpty()) {
                receiverId = Integer.parseInt(receiverIdParam);
            } else if (receiverUsernameParam != null && !receiverUsernameParam.isEmpty()) {
                User receiverUser = userDao.getUserByUsername(receiverUsernameParam);
                if (receiverUser == null) {
                    throw new ServletException("User not found: " + receiverUsernameParam);
                }
                receiverId = receiverUser.getId();
            } else {
                throw new ServletException("Either receiverId or receiverUsername must be provided");
            }
            int senderId = user.getId();
            friendRequestDAO.sendRequest(senderId, receiverId);
            response.sendRedirect("friend-requests?message=Friend request sent successfully");
            
        }catch(Exception e) {
            e.printStackTrace();
            response.sendRedirect("friend-requests?error=Failed to send friend request");
        }

    }
}
