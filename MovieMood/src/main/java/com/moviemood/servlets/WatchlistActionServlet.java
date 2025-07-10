package com.moviemood.servlets;

import com.moviemood.bean.User;
import com.moviemood.dao.UserWatchlistDao;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/watchlist/action")
public class WatchlistActionServlet extends HttpServlet {

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
            out.print("{\"success\": false, \"message\": \"Please log in to manage your watchlist\"}");
            return;
        }
        
        String action = request.getParameter("action");
        String movieIdStr = request.getParameter("movieId");
        
        if (action == null || movieIdStr == null) {
            out.print("{\"success\": false, \"message\": \"Invalid request parameters\"}");
            return;
        }
        
        try {
            int movieId = Integer.parseInt(movieIdStr);
            UserWatchlistDao watchlistDao = (UserWatchlistDao) getServletContext().getAttribute("watchlistDao");
            
            if (watchlistDao == null) {
                out.print("{\"success\": false, \"message\": \"Watchlist service unavailable\"}");
                return;
            }
            
            boolean success = false;
            String message = "";
            
            if ("add".equals(action)) {
                success = watchlistDao.addMovieToWatchList(currentUser.getId(), movieId);
                message = success ? "Added to watchlist" : "Failed to add to watchlist";
            } else if ("remove".equals(action)) {
                success = watchlistDao.removeMovieFromWatchList(currentUser.getId(), movieId);
                message = success ? "Removed from watchlist" : "Failed to remove from watchlist";
            } else {
                out.print("{\"success\": false, \"message\": \"Invalid action\"}");
                return;
            }
            
            out.print("{\"success\": " + success + ", \"message\": \"" + message + "\"}");
            
        } catch (NumberFormatException e) {
            out.print("{\"success\": false, \"message\": \"Invalid movie ID\"}");
        } catch (Exception e) {
            e.printStackTrace();
            out.print("{\"success\": false, \"message\": \"An error occurred\"}");
        }
    }
} 