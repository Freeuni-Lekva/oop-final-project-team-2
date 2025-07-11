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

            friendRequestDao.updateRequestStatus(requestId, "accepted");

            friendshipDao.createFriendship(senderId, receiverId);

            friendRequestDao.deleteRequest(requestId);

            String redirectTo = request.getParameter("redirectTo");
            if ("profile".equals(redirectTo)) {
                String redirectUser = request.getParameter("redirectUser");
                if (redirectUser != null) {
                    response.sendRedirect("profile?user=" + redirectUser + "&message=Friend request accepted");
                } else {
                    response.sendRedirect("profile?message=Friend request accepted");
                }
            } else {
                response.sendRedirect("friend-requests?tab=incoming");
            }

        } catch (NumberFormatException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid request parameters");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to accept friend request");
        }
    }
}
