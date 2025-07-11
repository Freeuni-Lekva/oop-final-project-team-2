package com.moviemood.servlets;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.moviemood.bean.User;
import com.moviemood.dao.UserDao;
import com.moviemood.exceptions.UserAlreadyExistsException;
import org.apache.commons.dbcp2.BasicDataSource;
import org.mindrot.jbcrypt.BCrypt;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.UUID;

@WebServlet("/auth/google")
public class GoogleAuthServlet extends HttpServlet {

    private UserDao userDao;
    private String googleClientId;

    @Override
    public void init() throws ServletException {
        super.init();
        try {
            BasicDataSource dataSource = (BasicDataSource) getServletContext().getAttribute("dataSource");
            if (dataSource == null) {
                throw new ServletException("DataSource not found in ServletContext");
            }

            userDao = new UserDao(dataSource);

            // Hardcode the Google Client ID
            googleClientId = "632596920546-o05uh5426a18fv57pd73vhodlp5jon7n.apps.googleusercontent.com";

        } catch (Exception e) {
            throw new ServletException("Failed to initialize GoogleAuthServlet", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");

        try {
            String credential = getCredentialFromRequest(request);

            if (credential == null) {
                response.setStatus(400);
                response.getWriter().write("{\"error\": \"No credential provided\"}");
                return;
            }

            GoogleIdToken.Payload payload = verifyGoogleToken(credential);

            if (payload != null) {
                String email = payload.getEmail();
                String name = (String) payload.get("name");

                User existingUser = userDao.getUserByEmail(email);
                boolean isNewUser = (existingUser == null);

                User user;
                if (existingUser != null) {
                    user = existingUser;
                } else {
                    user = createNewGoogleUser(email, name);
                }

                if (user != null) {
                    HttpSession session = request.getSession();
                    session.setAttribute("user", user);
                    session.setAttribute("username", user.getUsername());

                    // Set remember token for Google users
                    String rememberToken = UUID.randomUUID().toString();
                    userDao.updateRememberToken(user.getUsername(), rememberToken);

                    // Set remember cookie
                    Cookie rememberCookie = new Cookie("remember_token", rememberToken);
                    rememberCookie.setMaxAge(30 * 24 * 60 * 60); // 30 days
                    rememberCookie.setPath("/");
                    response.addCookie(rememberCookie);

                    if (isNewUser) {
                        response.setStatus(200);
                        response.getWriter().write("{\"success\": true, \"redirect\": \"/movie-preferences\"}");
                    } else {
                        response.setStatus(200);
                        response.getWriter().write("{\"success\": true, \"redirect\": \"/index.jsp\"}");
                    }
                }else {
                    response.setStatus(500);
                    response.getWriter().write("{\"error\": \"Failed to process user\"}");
                }
            } else {
                response.setStatus(401);
                response.getWriter().write("{\"error\": \"Invalid Google token\"}");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(500);
            response.getWriter().write("{\"error\": \"Server error: " + e.getMessage() + "\"}");
        }
    }

    private String getCredentialFromRequest(HttpServletRequest request) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = request.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }

        String json = sb.toString();
        if (json.contains("\"credential\":")) {
            int start = json.indexOf("\"credential\":\"") + 14;
            int end = json.indexOf("\"", start);
            if (start > 13 && end > start) {
                return json.substring(start, end);
            }
        }
        return null;
    }

    private GoogleIdToken.Payload verifyGoogleToken(String idTokenString) {
        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    new NetHttpTransport(),
                    new GsonFactory())
                    .setAudience(Collections.singletonList(googleClientId))
                    .build();

            GoogleIdToken idToken = verifier.verify(idTokenString);
            if (idToken != null) {
                return idToken.getPayload();
            }
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private User createNewGoogleUser(String email, String name) throws UserAlreadyExistsException {
        String randomPassword = BCrypt.hashpw(UUID.randomUUID().toString(), BCrypt.gensalt());

        String username = (name != null && !name.isEmpty()) ?
                name.replaceAll("[^a-zA-Z0-9]", "").toLowerCase() :
                email.split("@")[0];

        username = ensureUniqueUsername(username);

        userDao.insertUser(username, email, randomPassword);

        User newUser = userDao.getUserByEmail(email);

        if (newUser != null) {
            userDao.verifyUser(newUser.getId());
            newUser = userDao.getUserByEmail(email);
        }

        return newUser;
    }

    private String ensureUniqueUsername(String baseUsername) {
        String username = baseUsername;
        int counter = 1;

        while (userDao.getUserByUsername(username) != null) {
            username = baseUsername + counter;
            counter++;
        }

        return username;
    }
}
