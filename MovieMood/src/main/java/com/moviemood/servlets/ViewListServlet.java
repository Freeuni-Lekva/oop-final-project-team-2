package com.moviemood.servlets;

import com.moviemood.bean.Movie;
import com.moviemood.bean.User;
import com.moviemood.bean.UserList;
import com.moviemood.config.Config;
import com.moviemood.dao.UserListDao;
import com.moviemood.repository.tmdb.TmdbMovieRepository;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@WebServlet("/list/view")
public class ViewListServlet extends HttpServlet {

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
        
        String listIdParam = request.getParameter("id");
        if (listIdParam == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "List ID is required");
            return;
        }
        
        try {
            int listId = Integer.parseInt(listIdParam);
            UserListDao listsDao = (UserListDao) getServletContext().getAttribute("listsDao");
            
            if (listsDao == null) {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "List service unavailable");
                return;
            }
            
            // Get the list details
            UserList list = listsDao.getListById(listId);
            if (list == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "List not found");
                return;
            }
            
            // Verify ownership (for now, only owners can view their lists)
            if (list.getUserId() != currentUser.getId()) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "You don't have permission to view this list");
                return;
            }
            
            // Get movie IDs in this list
            List<Integer> movieIds = listsDao.getListMovies(listId);
            
            // Fetch movie details from TMDB
            List<Movie> movies = fetchMoviesFromTmdb(movieIds);
            
            // Set attributes for JSP
            request.setAttribute("list", list);
            request.setAttribute("movies", movies);
            request.setAttribute("currentUser", currentUser);
            request.setAttribute("POSTER_BASE", Config.get("posterPathBase"));
            
            // Forward to the view list page
            request.getRequestDispatcher("/view-list.jsp").forward(request, response);
            
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid list ID");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error loading list");
        }
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
        String listIdParam = request.getParameter("listId");
        String movieIdParam = request.getParameter("movieId");
        
        if ("remove".equals(action) && listIdParam != null && movieIdParam != null) {
            try {
                int listId = Integer.parseInt(listIdParam);
                int movieId = Integer.parseInt(movieIdParam);
                
                UserListDao listsDao = (UserListDao) getServletContext().getAttribute("listsDao");
                if (listsDao != null) {
                    // Verify list ownership
                    UserList list = listsDao.getListById(listId);
                    if (list != null && list.getUserId() == currentUser.getId()) {
                        boolean success = listsDao.removeMovieFromList(listId, movieId);
                        if (success) {
                            System.out.println("Successfully removed movie " + movieId + " from list " + listId);
                        }
                    }
                }
            } catch (NumberFormatException e) {
                System.err.println("Invalid ID format in remove movie request");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        // Redirect back to the list view
        response.sendRedirect(request.getContextPath() + "/list/view?id=" + listIdParam);
    }
    
    /**
     * Fetch movie details from TMDB API for given movie IDs
     */
    private List<Movie> fetchMoviesFromTmdb(List<Integer> movieIds) {
        List<Movie> movies = new ArrayList<>();
        TmdbMovieRepository movieRepo = TmdbMovieRepository.getInstance();
        
        for (Integer movieId : movieIds) {
            try {
                Optional<Movie> movieOpt = movieRepo.findById(movieId);
                if (movieOpt.isPresent()) {
                    movies.add(movieOpt.get());
                }
            } catch (IOException e) {
                e.printStackTrace();
                // Continue with other movies if one fails
            }
        }
        
        return movies;
    }
} 