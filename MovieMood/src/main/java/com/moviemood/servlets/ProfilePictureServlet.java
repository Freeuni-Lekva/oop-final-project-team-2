package com.moviemood.servlets;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@WebServlet("/profile-picture/*")
public class ProfilePictureServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Get filename from URL path
        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.length() <= 1) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        
        String filename = pathInfo.substring(1); // Remove leading slash
        
        // Validate filename (security check)
        if (filename.contains("..") || filename.contains("/") || filename.contains("\\")) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid filename");
            return;
        }
        
        // Get file from permanent upload directory
        String userHome = System.getProperty("user.home");
        Path filePath = Paths.get(userHome, "moviemood-uploads", "profile-pictures", filename);
        File file = filePath.toFile();
        
        if (!file.exists() || !file.isFile()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        
        // Set appropriate content type based on file extension
        String contentType = getContentType(filename);
        if (contentType != null) {
            response.setContentType(contentType);
        }
        
        // Set cache headers for better performance
        response.setHeader("Cache-Control", "public, max-age=31536000"); // 1 year
        response.setDateHeader("Expires", System.currentTimeMillis() + 31536000000L);
        
        // Send file content
        Files.copy(filePath, response.getOutputStream());
    }
    
    private String getContentType(String filename) {
        String extension = filename.toLowerCase();
        if (extension.endsWith(".jpg") || extension.endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (extension.endsWith(".png")) {
            return "image/png";
        } else if (extension.endsWith(".gif")) {
            return "image/gif";
        }
        return "application/octet-stream";
    }
} 