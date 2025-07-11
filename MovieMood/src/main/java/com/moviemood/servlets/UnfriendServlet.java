package com.moviemood.servlets;

import com.moviemood.bean.User;
import com.moviemood.dao.FriendshipDao;
import com.moviemood.dao.UserDao;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/unfriend")
public class UnfriendServlet extends HttpServlet {
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute("user");

        if (user == null) {
            response.sendRedirect("login.jsp");
            return;
        }
        
        String friendUsername = request.getParameter("friendUsername");
        if (friendUsername == null || friendUsername.trim().isEmpty()) {
            response.sendRedirect("friend-requests?tab=your_friends&error=Invalid friend specified");
            return;
        }
        
        try {
            FriendshipDao friendshipDao = (FriendshipDao) getServletContext().getAttribute("friendshipDao");
            UserDao userDao = (UserDao) getServletContext().getAttribute("userDao");
            User friendUser = userDao.getUserByUsername(friendUsername);

            if (friendUser == null) {
                response.sendRedirect("friend-requests?tab=your_friends&error=Friend not found");
                return;
            }
            friendshipDao.deleteFriendship(user.getId(), friendUser.getId());
            
            response.sendRedirect("friend-requests?tab=your_friends&message=Successfully unfriended " + friendUsername);
            
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("friend-requests?tab=your_friends&error=Failed to unfriend user");
        }
    }
} 