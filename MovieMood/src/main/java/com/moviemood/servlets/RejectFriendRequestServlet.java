package com.moviemood.servlets;

import com.moviemood.bean.User;
import com.moviemood.dao.FriendRequestDao;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/reject-friend-request")
public class RejectFriendRequestServlet extends HttpServlet {

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

            friendRequestDao.updateRequestStatus(requestId, "rejected");

            friendRequestDao.deleteRequest(requestId);

            String redirectTo = request.getParameter("redirectTo");
            if ("profile".equals(redirectTo)) {
                String redirectUser = request.getParameter("redirectUser");
                if (redirectUser != null) {
                    response.sendRedirect("profile?user=" + redirectUser + "&message=Friend request rejected");
                } else {
                    response.sendRedirect("profile?message=Friend request rejected");
                }
            } else {
                response.sendRedirect("friend-requests?tab=incoming");
            }

        } catch (NumberFormatException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid request ID");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to reject friend request");
        }
    }
}
