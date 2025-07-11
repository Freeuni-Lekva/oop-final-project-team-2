package com.moviemood.servlets;

import com.moviemood.bean.User;
import com.moviemood.dao.FriendRequestDao;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/cancel-sent-request")
public class CancelSentRequestServlet extends HttpServlet {
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            response.sendRedirect("login.jsp");
            return;
        }
        
        String receiverUsername = request.getParameter("receiverUsername");
        if (receiverUsername == null || receiverUsername.trim().isEmpty()) {
            response.sendRedirect("profile?error=Invalid user specified");
            return;
        }
        
        try {
            FriendRequestDao friendRequestDao = (FriendRequestDao) getServletContext().getAttribute("friendRequestDao");
            
            boolean success = friendRequestDao.cancelSentRequest(user.getId(), receiverUsername);
            
            if (success) {
                response.sendRedirect("profile?user=" + receiverUsername + "&message=Friend request cancelled");
            } else {
                response.sendRedirect("profile?user=" + receiverUsername + "&error=Failed to cancel friend request");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("profile?user=" + receiverUsername + "&error=Failed to cancel friend request");
        }
    }
}
