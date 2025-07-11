package com.moviemood.servlets;

import com.moviemood.bean.FriendRequest;
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
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;

@WebServlet("/friend-requests")
public class FriendRequestsServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User currentUser = (User) request.getSession().getAttribute("user");
        if (currentUser == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        try {
            FriendRequestDao friendRequestDao =  (FriendRequestDao) getServletContext().getAttribute("friendRequestDao");
            FriendshipDao friendshipDao = (FriendshipDao) getServletContext().getAttribute("friendshipDao");
            UserDao userDao = (UserDao) getServletContext().getAttribute("userDao");

            // Determine whose friends to show
            String username = request.getParameter("user");
            User targetUser = currentUser; // Default to current user
            boolean isOwnFriends = true;
            
            if (username != null && !username.trim().isEmpty()) {
                // Viewing someone else's friends
                User foundUser = userDao.getUserByUsername(username);
                if (foundUser != null) {
                    targetUser = foundUser;
                    isOwnFriends = (currentUser.getId() == foundUser.getId());
                } else {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "User not found");
                    return;
                }
            }

            int userId = targetUser.getId();
            List<User> allFriends = friendshipDao.getFriendsByUserId(userId);

            // Only show private data for current user
            List<FriendRequest> incomingRequests = new ArrayList<>();
            List<FriendRequest> sentRequests = new ArrayList<>();
            List<FriendSuggestion> friendSuggestions = new ArrayList<>();
            
            if (isOwnFriends) {
                incomingRequests = friendRequestDao.getIncomingRequests(currentUser.getId());
                sentRequests = friendRequestDao.getSentRequests(currentUser.getId());
                friendSuggestions = friendshipDao.getFriendSuggestions(currentUser.getId(), 10);
            }

            String searchQuery = request.getParameter("search");
            if (searchQuery != null && !searchQuery.trim().isEmpty()) {
                String currentTab = request.getParameter("tab");
                if (currentTab == null) currentTab = "your_friends";
                
                if ("your_friends".equals(currentTab)) {
                    allFriends = searchFriends(allFriends, searchQuery);
                } else if ("suggestions".equals(currentTab) && isOwnFriends) {
                    friendSuggestions = searchInSuggestionsAndOthers(friendSuggestions, searchQuery, currentUser.getId(), userDao, friendshipDao);
                }
            }

            request.setAttribute("incomingRequests", incomingRequests);
            request.setAttribute("sentRequests", sentRequests);
            request.setAttribute("allFriends", allFriends);
            request.setAttribute("friendSuggestions", friendSuggestions);
            request.setAttribute("currentUser", currentUser);
            request.setAttribute("targetUser", targetUser);
            request.setAttribute("isOwnFriends", isOwnFriends);
            request.getRequestDispatcher("friend-requests.jsp").forward(request, response);

        }catch(Exception e) {
            e.printStackTrace();
            throw new ServletException("Failed to load friend requests");
        }
    }
    
    /**
     * Search within existing friends list
     */
    private List<User> searchFriends(List<User> friends, String searchQuery) {
        List<User> filteredFriends = new ArrayList<>();
        String query = searchQuery.toLowerCase().trim();
        
        for (User friend : friends) {
            if (friend.getUsername().toLowerCase().contains(query)) {
                filteredFriends.add(friend);
            }
        }
        
        return filteredFriends;
    }
    
    /**
     * Search within suggestions and add other users if needed
     */
    private List<FriendSuggestion> searchInSuggestionsAndOthers(List<FriendSuggestion> suggestions, 
                                                               String searchQuery, int userId, 
                                                               UserDao userDao, FriendshipDao friendshipDao) {
        List<FriendSuggestion> results = new ArrayList<>();
        String query = searchQuery.toLowerCase().trim();
        
        for (FriendSuggestion suggestion : suggestions) {
            if (suggestion.getUser().getUsername().toLowerCase().contains(query)) {
                results.add(suggestion);
            }
        }
        
        try {
            List<User> searchResults = userDao.searchUserByQuery(query);
            
            List<User> currentFriends = friendshipDao.getFriendsByUserId(userId);
            Set<Integer> friendIds = new HashSet<>();
            for (User friend : currentFriends) {
                friendIds.add(friend.getId());
            }
            
            Set<Integer> suggestionIds = new HashSet<>();
            for (FriendSuggestion suggestion : results) {
                suggestionIds.add(suggestion.getUser().getId());
            }
            
            Set<Integer> excludedIds = friendshipDao.getExcludedUserIds(userId);
            
            for (User searchResult : searchResults) {
                if (searchResult.getId() != userId && 
                    !friendIds.contains(searchResult.getId()) && 
                    !suggestionIds.contains(searchResult.getId()) &&
                    !excludedIds.contains(searchResult.getId())) {
                    
                    FriendSuggestion newSuggestion = new FriendSuggestion(searchResult, 0);
                    results.add(newSuggestion);
                }
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return results;
    }
}
