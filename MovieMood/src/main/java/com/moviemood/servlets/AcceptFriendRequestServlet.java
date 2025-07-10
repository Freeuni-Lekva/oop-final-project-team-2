package com.moviemood.servlets;

import com.moviemood.bean.User;
import com.moviemood.dao.FriendRequestDao;
import com.moviemood.dao.FriendshipDao;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/accept-friend-request")
public class AcceptFriendRequestServlet extends HttpServlet {
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        FriendRequestDao friendRequestDao = (FriendRequestDao) getServletContext().getAttribute("friendRequestDao");
        FriendshipDao friendshipDao = (FriendshipDao) getServletContext().getAttribute("friendshipDao");

        try {
            int requestId = Integer.parseInt(request.getParameter("requestId"));
            int senderId = Integer.parseInt(request.getParameter("senderId"));
            int receiverId = Integer.parseInt(request.getParameter("receiverId"));

//            if (user.getId() != receiverId) {
//                response.sendError(HttpServletResponse.SC_FORBIDDEN, "You can only accept requests sent to you");
//                return;
//            }

            friendRequestDao.updateRequestStatus(requestId, "accepted");

            friendshipDao.createFriendship(senderId, receiverId);

            friendRequestDao.deleteRequest(requestId);

            response.sendRedirect("friend-requests?tab=incoming");

        } catch (NumberFormatException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid request parameters");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to accept friend request");
        }
    }
}
