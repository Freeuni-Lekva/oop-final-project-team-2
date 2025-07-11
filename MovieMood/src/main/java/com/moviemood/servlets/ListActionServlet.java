package com.moviemood.servlets;

import com.moviemood.bean.User;
import com.moviemood.dao.UserListDao;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/list/action")
public class ListActionServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        
        HttpSession session = request.getSession(false);
        User currentUser = null;
        if (session != null) {
            currentUser = (User) session.getAttribute("user");
        }
        
        if (currentUser == null) {
            out.print("{\"success\": false, \"message\": \"Please log in to manage your lists\"}");
            return;
        }
        
        String action = request.getParameter("action");
        String movieIdStr = request.getParameter("movieId");
        String listIdStr = request.getParameter("listId");
        
        if (action == null || movieIdStr == null) {
            out.print("{\"success\": false, \"message\": \"Invalid request parameters\"}");
            return;
        }
        
        try {
            int movieId = Integer.parseInt(movieIdStr);
            UserListDao listDao = (UserListDao) getServletContext().getAttribute("listsDao");
            
            if (listDao == null) {
                out.print("{\"success\": false, \"message\": \"List service unavailable\"}");
                return;
            }
            
            boolean success = false;
            String message = "";
            
            if ("add".equals(action)) {
                if (listIdStr == null) {
                    out.print("{\"success\": false, \"message\": \"List ID required for add action\"}");
                    return;
                }
                int listId = Integer.parseInt(listIdStr);
                success = listDao.addMovieToList(listId, movieId);
                message = success ? "Added to list" : "Movie already in list or failed to add";
            } else if ("remove".equals(action)) {
                if (listIdStr == null) {
                    out.print("{\"success\": false, \"message\": \"List ID required for remove action\"}");
                    return;
                }
                int listId = Integer.parseInt(listIdStr);
                success = listDao.removeMovieFromList(listId, movieId);
                message = success ? "Removed from list" : "Failed to remove from list";
            } else {
                out.print("{\"success\": false, \"message\": \"Invalid action\"}");
                return;
            }
            
            out.print("{\"success\": " + success + ", \"message\": \"" + message + "\"}");
            
        } catch (NumberFormatException e) {
            out.print("{\"success\": false, \"message\": \"Invalid movie or list ID\"}");
        } catch (Exception e) {
            e.printStackTrace();
            out.print("{\"success\": false, \"message\": \"An error occurred\"}");
        }
    }
} 