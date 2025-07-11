package com.moviemood.servlets;

import com.moviemood.bean.User;
import com.moviemood.dao.FriendRequestDao;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/cancel-friend-request")
public class CancelSentRequestServlet extends HttpServlet {
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        FriendRequestDao friendRequestDao = (FriendRequestDao) getServletContext().getAttribute("friendRequestDao");

        try {
            int requestId = Integer.parseInt(request.getParameter("requestId"));

            friendRequestDao.updateRequestStatus(requestId, "cancelled");

            response.sendRedirect("friend-requests?tab=sent");

        } catch (NumberFormatException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid request ID");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to cancel friend request");
        }
    }
}
