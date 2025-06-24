package com.moviemood.filters;

import com.moviemood.bean.User;
import com.moviemood.dao.UserDao;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.UUID;


/**
 * Authentication Filter that handles user session management and automatic login via remember tokens.
 *
 * Performs the following operations:
 * 1. Checks if the user already has an active session
 * 2. If no active session exists, attempts to restore user authentication using a "remember_token" cookie
 * 3. Validates the remember token against the database and creates a new session if valid
 *
 * The filter works in conjunction with the LoginServlet, which creates the remember token cookie
 * when users check "Remember Me" during login. This allows users to remain logged in even after:
 * - Browser restarts
 * - Server restarts (since tokens are stored in database, not server memory)
 *
 * Flow:
 * - User logs in with "Remember Me" → LoginServlet creates remember_token cookie
 * - User visits any page → AuthFilter checks for existing session
 * - If no session found → AuthFilter looks for remember_token cookie
 * - If valid token found in database → AuthFilter creates new session and user is automatically logged in
 */
@WebFilter("/*")
public class AuthFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        //
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpReq = (HttpServletRequest) request;
        HttpSession session = httpReq.getSession(false);

        // If user is not already in session, try to restore from cookie
        if (session == null || session.getAttribute("user") == null) {
            Cookie[] cookies = httpReq.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if ("remember_token".equals(cookie.getName())) {
                        String token = cookie.getValue();
                        ServletContext context = httpReq.getServletContext();
                        UserDao userDao = (UserDao) context.getAttribute("userDao");

                        if (userDao != null) {
                            User user = userDao.getUserByToken(token);
                            if (user != null) {
                                HttpSession newSession = httpReq.getSession(true);
                                newSession.setAttribute("user", user);

                                // Regenerating token for security
                                String newToken = UUID.randomUUID().toString();
                                userDao.updateRememberToken(user.getUsername(), newToken);

                                // Update the cookie with new token
                                Cookie newCookie = new Cookie("remember_token", newToken);
                                newCookie.setMaxAge(60 * 60 * 24 * 30); // 30 days again
                                newCookie.setHttpOnly(true);
                                ((HttpServletResponse) response).addCookie(newCookie);
                            }
                        }
                        break;
                    }
                }
            }
        }

        // Continue the filter chain
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        //
    }
}
