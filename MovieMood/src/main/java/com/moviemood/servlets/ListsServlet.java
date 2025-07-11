package com.moviemood.servlets;

import com.moviemood.bean.User;
import com.moviemood.bean.UserList;
import com.moviemood.dao.UserListDao;
import com.moviemood.dao.UserDao;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/lists")
public class ListsServlet extends HttpServlet {

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
        
        // Determine whose lists to show
        String username = request.getParameter("user");
        User targetUser = currentUser; // Default to current user
        boolean isOwnLists = true;
        
        if (username != null && !username.trim().isEmpty()) {
            // Viewing someone else's lists
            try {
                UserDao userDao = (UserDao) getServletContext().getAttribute("userDao");
                if (userDao != null) {
                    User foundUser = userDao.getUserByUsername(username);
                    if (foundUser != null) {
                        targetUser = foundUser;
                        isOwnLists = (currentUser.getId() == foundUser.getId());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                // Fall back to current user if error
            }
        }
        
        List<UserList> userLists = new ArrayList<>();
        
        // Get user lists from database
        try {
            UserListDao listsDao = (UserListDao) getServletContext().getAttribute("listsDao");
            if (listsDao != null) {
                if (isOwnLists) {
                    // Show all lists for own profile
                    userLists = listsDao.getUserLists(targetUser.getId());
                } else {
                    // Show only public lists for other users' profiles
                    userLists = listsDao.getPublicUserLists(targetUser.getId());
                }
                
                // Populate movie counts for each list
                for (UserList list : userLists) {
                    int movieCount = listsDao.getListMovieCount(list.getId());
                    list.setMovieCount(movieCount);
                }
                
                System.out.println("Retrieved " + userLists.size() + " lists for user: " + targetUser.getUsername());
            } else {
                System.err.println("ListsDao is null - not properly initialized");
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Continue with empty list on error
            System.err.println("Error retrieving user lists: " + e.getMessage());
        }
        
        // Set attributes for JSP
        request.setAttribute("userLists", userLists);
        request.setAttribute("currentUser", currentUser);
        request.setAttribute("targetUser", targetUser);
        request.setAttribute("isOwnLists", isOwnLists);
        
        // Forward to the lists page
        request.getRequestDispatcher("/lists.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        User currentUser = null;
        if (session != null) {
            currentUser = (User) session.getAttribute("user");
        }
        
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }
        
        String action = request.getParameter("action");
        
        if ("create".equals(action)) {
            // Create a new list
            String listName = request.getParameter("listName");
            String listDescription = request.getParameter("listDescription");
            boolean isPublic = "true".equals(request.getParameter("isPublic"));
            
            if (listName != null && !listName.trim().isEmpty()) {
                try {
                    UserListDao listsDao = (UserListDao) getServletContext().getAttribute("listsDao");
                    if (listsDao != null) {
                        UserList newList = new UserList(currentUser.getId(), listName.trim(), 
                                                      listDescription, isPublic);
                        UserList createdList = listsDao.createList(newList);
                        
                        if (createdList != null) {
                            System.out.println("Successfully created list: " + createdList.getName() + 
                                             " with ID: " + createdList.getId());
                        } else {
                            System.err.println("Failed to create list");
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    System.err.println("Error creating list: " + e.getMessage());
                }
            }
        } else if ("edit".equals(action)) {
            // Edit an existing list
            String listIdStr = request.getParameter("listId");
            String listName = request.getParameter("listName");
            String listDescription = request.getParameter("listDescription");
            boolean isPublic = "true".equals(request.getParameter("isPublic"));
            
            if (listIdStr != null && listName != null && !listName.trim().isEmpty()) {
                try {
                    int listId = Integer.parseInt(listIdStr);
                    UserListDao listsDao = (UserListDao) getServletContext().getAttribute("listsDao");
                    if (listsDao != null) {
                        // Verify the list belongs to the current user
                        UserList existingList = listsDao.getListById(listId);
                        if (existingList != null && existingList.getUserId() == currentUser.getId()) {
                            // Create updated UserList object
                            UserList updatedList = new UserList(existingList.getUserId(), listName.trim(), listDescription, isPublic);
                            updatedList.setId(listId);
                            updatedList.setCreatedAt(existingList.getCreatedAt()); // Preserve creation date
                            
                            boolean success = listsDao.updateList(updatedList);
                            if (success) {
                                System.out.println("Successfully updated list ID: " + listId);
                            } else {
                                System.err.println("Failed to update list ID: " + listId);
                            }
                        } else {
                            System.err.println("List not found or unauthorized: " + listId);
                        }
                    }
                } catch (NumberFormatException e) {
                    System.err.println("Invalid list ID format: " + listIdStr);
                } catch (Exception e) {
                    e.printStackTrace();
                    System.err.println("Error updating list: " + e.getMessage());
                }
            }
        } else if ("delete".equals(action)) {
            // Delete a list
            String listIdStr = request.getParameter("listId");
            
            if (listIdStr != null) {
                try {
                    int listId = Integer.parseInt(listIdStr);
                    UserListDao listsDao = (UserListDao) getServletContext().getAttribute("listsDao");
                    if (listsDao != null) {
                        // Verify the list belongs to the current user
                        UserList existingList = listsDao.getListById(listId);
                        if (existingList != null && existingList.getUserId() == currentUser.getId()) {
                            boolean success = listsDao.deleteList(listId);
                            if (success) {
                                System.out.println("Successfully deleted list ID: " + listId);
                            } else {
                                System.err.println("Failed to delete list ID: " + listId);
                            }
                        } else {
                            System.err.println("List not found or unauthorized: " + listId);
                        }
                    }
                } catch (NumberFormatException e) {
                    System.err.println("Invalid list ID format: " + listIdStr);
                } catch (Exception e) {
                    e.printStackTrace();
                    System.err.println("Error deleting list: " + e.getMessage());
                }
            }
        }
        
        // Redirect back to GET to show updated lists
        response.sendRedirect(request.getContextPath() + "/lists");
    }
} 