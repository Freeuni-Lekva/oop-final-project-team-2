package com.moviemood.servlets;

import com.moviemood.bean.FriendRequest;
import com.moviemood.dao.FriendRequestDao;
import com.moviemood.dao.FriendshipDao;
import com.moviemood.dao.UserDao;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/accept-friend-request")
public class AcceptFriendRequestServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String username = request.getSession().getAttribute("username").toString();

        FriendRequestDao friendRequestDao =  (FriendRequestDao) getServletContext().getAttribute("friendRequestDao");
        FriendshipDao friendshipDao = (FriendshipDao) getServletContext().getAttribute("friendshipDao");

        try {
            int requestId = Integer.parseInt(request.getParameter("requestId"));
            int senderId = Integer.parseInt(request.getParameter("senderId"));
            int receiverId = Integer.parseInt(request.getParameter("receiverId"));

            friendRequestDao.updateRequestStatus(requestId, "accepted");

            int user1 = Math.min(senderId, receiverId);
            int user2 = Math.max(senderId, receiverId);
            friendshipDao.createFriendship(user1, user2);
            //friendRequestDao.deleteRequest(requestId); could delete this from reqeusts.
            response.setStatus(HttpServletResponse.SC_OK);

        }catch (Exception e){
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            throw new ServletException("Failed to accept friend request", e);
        }

    }
}
