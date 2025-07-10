package com.moviemood.servlets;

import com.moviemood.bean.FriendRequest;
import com.moviemood.bean.Friendship;
import com.moviemood.bean.User;
import com.moviemood.bean.FriendSuggestion;
import com.moviemood.dao.FriendRequestDao;
import com.moviemood.dao.FriendshipDao;
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
        // im using this for testing
        String hardcodedTestUser = "alice";
        
        UserDao userDao = (UserDao) getServletContext().getAttribute("userDao");
        User user = userDao.getUserByUsername(hardcodedTestUser);
        
        if (user != null) {
            request.getSession().setAttribute("user", user); // Set in session for consistency
        } else {
            throw new ServletException("Test user '" + hardcodedTestUser + "' not found in database");
        }
        

        // User user = (User) request.getSession().getAttribute("user");
        // if (user == null) {
        //     throw new ServletException("No user found in session. Please login first.");
        // }

        try {
            FriendRequestDao friendRequestDao =  (FriendRequestDao) getServletContext().getAttribute("friendRequestDao");
            FriendshipDao friendshipDao = (FriendshipDao) getServletContext().getAttribute("friendshipDao");

            int userId = user.getId();

            List<FriendRequest> incomingRequests = friendRequestDao.getIncomingRequests(userId);
            List<FriendRequest> sentRequests = friendRequestDao.getSentRequests(userId);
            List<User> allFriends = friendshipDao.getFriendsByUserId(userId);
            List<FriendSuggestion> friendSuggestions = friendshipDao.getFriendSuggestions(userId, 10); // Get top 10 suggestions

            request.setAttribute("incomingRequests", incomingRequests);
            request.setAttribute("sentRequests", sentRequests);
            request.setAttribute("allFriends", allFriends);
            request.setAttribute("friendSuggestions", friendSuggestions);
            request.getRequestDispatcher("friend-requests.jsp").forward(request, response);

        }catch(Exception e) {
            e.printStackTrace();
            throw new ServletException("Failed to load friend requests");
        }
    }
}
