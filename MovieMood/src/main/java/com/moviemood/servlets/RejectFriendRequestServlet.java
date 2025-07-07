package com.moviemood.servlets;

import com.moviemood.dao.FriendRequestDao;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/reject-friend-request")
public class RejectFriendRequestServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        try {

            int requestId = Integer.parseInt(request.getParameter("requestId"));

            FriendRequestDao friendRequestDao = (FriendRequestDao) getServletContext().getAttribute("friendRequestDao");
            friendRequestDao.updateRequestStatus(requestId, "rejected");

            response.setStatus(HttpServletResponse.SC_OK);

        }catch(Exception e){
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            throw new ServletException("Failed to reject friend request");
        }
    }

}
