package com.moviemood.servlets;

import com.moviemood.bean.User;
import com.moviemood.dao.FriendshipDao;
import com.moviemood.dao.MovieReviewsDao;
import com.moviemood.dao.UserDao;
import com.moviemood.dao.UserFavoritesDao;
import com.moviemood.dao.UserWatchlistDao;
import com.moviemood.dao.UserListDao;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/profile")
public class ProfileServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        User currentUser = null;
        if (session != null) {
            currentUser = (User) session.getAttribute("user");
        }
        
        // Require login
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }
        
        // Get the username from URL parameter (for viewing other profiles) or use current user
        String username = request.getParameter("user");
        User profileUser;
        
        UserDao userDao = (UserDao) getServletContext().getAttribute("userDao");
        
        if (userDao == null) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                "User service not available");
            return;
        }
        
        try {
            if (username != null && !username.trim().isEmpty()) {
                // Viewing someone else's profile
                User foundUser = userDao.getUserByUsername(username);
                if (foundUser == null) {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "User not found");
                    return;
                }
                // Hide email for other users' profiles
                profileUser = new User(foundUser.getId(), foundUser.getUsername(), null, null, null);
            } else {
                // Viewing own profile
                profileUser = currentUser;
            }
            
            // Check if viewing own profile
            boolean isOwnProfile = currentUser.getId() == profileUser.getId();
            
            // Get real statistics from database
            int watchlistCount = 0;
            int reviewsCount = 0;
            int favoritesCount = 0;
            int friendsCount = 0;
            int listsCount = 0;
            
            try {
                UserWatchlistDao watchlistDao = (UserWatchlistDao) getServletContext().getAttribute("watchlistDao");
                MovieReviewsDao reviewsDao = (MovieReviewsDao) getServletContext().getAttribute("reviewsDao");
                UserFavoritesDao favoritesDao = (UserFavoritesDao) getServletContext().getAttribute("favoritesDao");
                FriendshipDao friendshipDao = (FriendshipDao) getServletContext().getAttribute("friendshipDao");
                UserListDao listsDao = (UserListDao) getServletContext().getAttribute("listsDao");
                
                if (watchlistDao != null) {
                    watchlistCount = watchlistDao.getWatchlistCount(profileUser.getId());
                }
                
                if (reviewsDao != null) {
                    reviewsCount = reviewsDao.getUserReviewCount(profileUser.getId());
                }
                
                if (favoritesDao != null) {
                    favoritesCount = favoritesDao.getFavoritesCount(profileUser.getId());
                }

                if (friendshipDao != null) {
                    friendsCount = friendshipDao.getFriendsByUserId(profileUser.getId()).size();
                }

                if (listsDao != null) {
                    listsCount = listsDao.getListsCount(profileUser.getId());
                }

            } catch (Exception e) {
                e.printStackTrace();
                // Continue with 0 counts if database error
            }
            
            // Set attributes for JSP
            request.setAttribute("profileUser", profileUser);
            request.setAttribute("isOwnProfile", isOwnProfile);
            request.setAttribute("currentUser", currentUser);
            request.setAttribute("watchlistCount", watchlistCount);
            request.setAttribute("reviewsCount", reviewsCount);
            request.setAttribute("favoritesCount", favoritesCount);
            request.setAttribute("friendsCount", friendsCount);
            request.setAttribute("listsCount", listsCount);
            
            // Forward to profile JSP
            request.getRequestDispatcher("/profile.jsp").forward(request, response);
            
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                "Error loading profile data");
        }
    }
}
