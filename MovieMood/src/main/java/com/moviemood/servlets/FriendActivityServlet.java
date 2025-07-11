package com.moviemood.servlets;

import com.moviemood.bean.FriendActivity;
import com.moviemood.bean.User;
import com.moviemood.dao.FriendActivityDao;
import com.moviemood.repository.tmdb.TmdbMovieRepository;
import com.moviemood.bean.Movie;
import org.apache.commons.dbcp2.BasicDataSource;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@WebServlet("/friends-activity")
public class FriendActivityServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
    
        User currentUser = (User) request.getSession().getAttribute("user");
        if (currentUser == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        try {
            FriendActivityDao activityDao = (FriendActivityDao) getServletContext().getAttribute("activityDao");
            
            // if (activityDao == null) {
            //     BasicDataSource dataSource = 
            //         (BasicDataSource) getServletContext().getAttribute("dataSource");
            //     activityDao = new FriendActivityDao(dataSource);
            // }

            List<FriendActivity> activities = activityDao.getFriendActivities(currentUser.getId(), 50);
            
            enrichWithMovieTitles(activities);
            
            request.setAttribute("activities", activities);
            request.setAttribute("currentUser", currentUser);
            request.getRequestDispatcher("/friends-activity.jsp").forward(request, response);
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Failed to load friends' activities. Please try again.");
            request.getRequestDispatcher("/friends-activity.jsp").forward(request, response);
        }
    }

    /**
     * Enrich activities with actual movie titles from TMDB API
     */
    private void enrichWithMovieTitles(List<FriendActivity> activities) {
        TmdbMovieRepository movieRepo = TmdbMovieRepository.getInstance();
        
        for (FriendActivity activity : activities) {
            if (activity.getMovieId() > 0) {
                try {
                    Optional<Movie> movieOpt = movieRepo.findById(activity.getMovieId());
                    if (movieOpt.isPresent()) {
                        activity.setMovieTitle(movieOpt.get().getTitle());
                    } else {
                        activity.setMovieTitle("Unknown Movie");
                    }
                } catch (Exception e) {
                    activity.setMovieTitle("Movie #" + activity.getMovieId());
                }
            }
        }
    }
} 