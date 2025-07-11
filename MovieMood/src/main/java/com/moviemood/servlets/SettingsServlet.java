package com.moviemood.servlets;

import com.moviemood.bean.User;
import com.moviemood.dao.UserDao;
import com.moviemood.exceptions.UserAlreadyExistsException;
import org.mindrot.jbcrypt.BCrypt;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@WebServlet("/settings")
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024,    // 1 MB
    maxFileSize = 1024 * 1024 * 5,      // 5 MB
    maxRequestSize = 1024 * 1024 * 10   // 10 MB
)
public class SettingsServlet extends HttpServlet {

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
        
        // Set current user for the settings form
        request.setAttribute("currentUser", currentUser);
        
        // Forward to settings JSP
        request.getRequestDispatcher("/settings.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
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
        
        String action = request.getParameter("action");
        UserDao userDao = (UserDao) getServletContext().getAttribute("userDao");
        
        if (userDao == null) {
            request.setAttribute("errorMessage", "User service not available");
            request.setAttribute("currentUser", currentUser);
            request.getRequestDispatcher("/settings.jsp").forward(request, response);
            return;
        }
        
        try {
            if ("updateUsername".equals(action)) {
                handleUsernameUpdate(request, response, currentUser, userDao);
            } else if ("updatePassword".equals(action)) {
                handlePasswordUpdate(request, response, currentUser, userDao);
            } else if ("updateProfilePicture".equals(action)) {
                handleProfilePictureUpdate(request, response, currentUser, userDao);
            } else if ("deleteAccount".equals(action)) {
                handleAccountDeletion(request, response, currentUser, userDao);
            } else {
                request.setAttribute("errorMessage", "Invalid action");
                request.setAttribute("currentUser", currentUser);
                request.getRequestDispatcher("/settings.jsp").forward(request, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "An error occurred while updating your settings");
            request.setAttribute("currentUser", currentUser);
            request.getRequestDispatcher("/settings.jsp").forward(request, response);
        }
    }
    
    private void handleUsernameUpdate(HttpServletRequest request, HttpServletResponse response, 
                                      User currentUser, UserDao userDao) 
            throws ServletException, IOException {
        
        String newUsername = request.getParameter("newUsername");
        
        if (newUsername == null || newUsername.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Username cannot be empty");
            request.setAttribute("currentUser", currentUser);
            request.getRequestDispatcher("/settings.jsp").forward(request, response);
            return;
        }
        
        newUsername = newUsername.trim();
        
        // Check if username is the same
        if (newUsername.equals(currentUser.getUsername())) {
            request.setAttribute("infoMessage", "Username is already set to this value");
            request.setAttribute("currentUser", currentUser);
            request.getRequestDispatcher("/settings.jsp").forward(request, response);
            return;
        }
        
        try {
            boolean updated = userDao.updateUsername(currentUser.getId(), newUsername);
            if (updated) {
                // Update the user object in session
                currentUser.setUsername(newUsername);
                request.getSession().setAttribute("user", currentUser);
                
                request.setAttribute("successMessage", "Username updated successfully!");
                request.setAttribute("currentUser", currentUser);
                request.getRequestDispatcher("/settings.jsp").forward(request, response);
            } else {
                request.setAttribute("errorMessage", "Failed to update username");
                request.setAttribute("currentUser", currentUser);
                request.getRequestDispatcher("/settings.jsp").forward(request, response);
            }
        } catch (UserAlreadyExistsException e) {
            request.setAttribute("errorMessage", "Username is already taken");
            request.setAttribute("currentUser", currentUser);
            request.getRequestDispatcher("/settings.jsp").forward(request, response);
        }
    }
    
    private void handlePasswordUpdate(HttpServletRequest request, HttpServletResponse response, 
                                      User currentUser, UserDao userDao) 
            throws ServletException, IOException {
        
        String currentPassword = request.getParameter("currentPassword");
        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");
        
        if (currentPassword == null || currentPassword.trim().isEmpty() ||
            newPassword == null || newPassword.trim().isEmpty() ||
            confirmPassword == null || confirmPassword.trim().isEmpty()) {
            request.setAttribute("errorMessage", "All password fields are required");
            request.setAttribute("currentUser", currentUser);
            request.getRequestDispatcher("/settings.jsp").forward(request, response);
            return;
        }
        
        // Verify current password
        if (!BCrypt.checkpw(currentPassword, currentUser.getHashedPassword())) {
            request.setAttribute("errorMessage", "Current password is incorrect");
            request.setAttribute("currentUser", currentUser);
            request.getRequestDispatcher("/settings.jsp").forward(request, response);
            return;
        }
        
        // Check if new password matches confirmation
        if (!newPassword.equals(confirmPassword)) {
            request.setAttribute("errorMessage", "New password and confirmation do not match");
            request.setAttribute("currentUser", currentUser);
            request.getRequestDispatcher("/settings.jsp").forward(request, response);
            return;
        }
        
        // Hash new password
        String newPasswordHash = BCrypt.hashpw(newPassword, BCrypt.gensalt());
        
        boolean updated = userDao.updatePassword(currentUser.getId(), newPasswordHash);
        if (updated) {
            // Update the user object in session
            currentUser.setHashedPassword(newPasswordHash);
            request.getSession().setAttribute("user", currentUser);
            
            request.setAttribute("successMessage", "Password updated successfully!");
            request.setAttribute("currentUser", currentUser);
            request.getRequestDispatcher("/settings.jsp").forward(request, response);
        } else {
            request.setAttribute("errorMessage", "Failed to update password");
            request.setAttribute("currentUser", currentUser);
            request.getRequestDispatcher("/settings.jsp").forward(request, response);
        }
    }
    
    private void handleProfilePictureUpdate(HttpServletRequest request, HttpServletResponse response, 
                                             User currentUser, UserDao userDao) 
            throws ServletException, IOException {
        
        try {
            Part filePart = request.getPart("profilePicture");
            
            if (filePart == null || filePart.getSize() == 0) {
                request.setAttribute("errorMessage", "Please select a profile picture to upload");
                request.setAttribute("currentUser", currentUser);
                request.getRequestDispatcher("/settings.jsp").forward(request, response);
                return;
            }
            
            // Validate file type
            String contentType = filePart.getContentType();
            if (!isValidImageType(contentType)) {
                request.setAttribute("errorMessage", "Please upload a valid image file (JPG, PNG, GIF)");
                request.setAttribute("currentUser", currentUser);
                request.getRequestDispatcher("/settings.jsp").forward(request, response);
                return;
            }
            
            // Create uploads directory in a permanent location (outside webapp)
            String userHome = System.getProperty("user.home");
            String uploadPath = userHome + File.separator + "moviemood-uploads" + File.separator + "profile-pictures";
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }
            
            // Generate unique filename
            String originalFileName = getFileName(filePart);
            String fileExtension = "";
            if (originalFileName != null && originalFileName.contains(".")) {
                fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
            }
            String uniqueFileName = currentUser.getId() + "_" + UUID.randomUUID().toString() + fileExtension;
            
            // Save the file
            Path filePath = Paths.get(uploadPath, uniqueFileName);
            Files.copy(filePart.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            
            // Store just the filename in database (file is in permanent location)
            String relativePath = uniqueFileName;
            
            // Delete old profile picture if it exists
            if (currentUser.getProfilePicture() != null && !currentUser.getProfilePicture().isEmpty()) {
                try {
                    String oldPicture = currentUser.getProfilePicture();
                    Path oldFilePath;
                    
                    // Handle both old format (with path) and new format (just filename)
                    if (oldPicture.contains("/") || oldPicture.contains("\\")) {
                        // Old format with path - try both locations
                        oldFilePath = Paths.get(getServletContext().getRealPath(""), oldPicture);
                        Files.deleteIfExists(oldFilePath);
                        // Also try the new location in case it was already migrated
                        oldFilePath = Paths.get(userHome, "moviemood-uploads", "profile-pictures", 
                                               oldPicture.substring(oldPicture.lastIndexOf("/") + 1));
                        Files.deleteIfExists(oldFilePath);
                    } else {
                        // New format (just filename) - delete from permanent location
                        oldFilePath = Paths.get(userHome, "moviemood-uploads", "profile-pictures", oldPicture);
                        Files.deleteIfExists(oldFilePath);
                    }
                } catch (Exception e) {
                    // Ignore errors when deleting old file
                }
            }
            
            // Update database
            boolean updated = userDao.updateProfilePicture(currentUser.getId(), relativePath);
            if (updated) {
                // Update the user object in session
                currentUser.setProfilePicture(relativePath);
                request.getSession().setAttribute("user", currentUser);
                
                request.setAttribute("successMessage", "Profile picture updated successfully!");
                request.setAttribute("currentUser", currentUser);
                request.getRequestDispatcher("/settings.jsp").forward(request, response);
            } else {
                // Delete the uploaded file if database update failed
                Files.deleteIfExists(filePath);
                
                request.setAttribute("errorMessage", "Failed to update profile picture");
                request.setAttribute("currentUser", currentUser);
                request.getRequestDispatcher("/settings.jsp").forward(request, response);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Error uploading profile picture: " + e.getMessage());
            request.setAttribute("currentUser", currentUser);
            request.getRequestDispatcher("/settings.jsp").forward(request, response);
        }
    }
    
    private boolean isValidImageType(String contentType) {
        return contentType != null && (
            contentType.equals("image/jpeg") ||
            contentType.equals("image/jpg") ||
            contentType.equals("image/png") ||
            contentType.equals("image/gif")
        );
    }
    
    private String getFileName(Part part) {
        String contentDisposition = part.getHeader("content-disposition");
        if (contentDisposition != null) {
            for (String token : contentDisposition.split(";")) {
                if (token.trim().startsWith("filename")) {
                    return token.substring(token.indexOf('=') + 1).trim().replace("\"", "");
                }
            }
        }
        return null;
    }
    
    private void handleAccountDeletion(HttpServletRequest request, HttpServletResponse response, 
                                       User currentUser, UserDao userDao) 
            throws ServletException, IOException {
        
        try {
            // Delete profile picture file if it exists
            if (currentUser.getProfilePicture() != null && !currentUser.getProfilePicture().isEmpty()) {
                try {
                    String userHome = System.getProperty("user.home");
                    String uploadPath = userHome + File.separator + "moviemood-uploads" + File.separator + "profile-pictures";
                    Path filePath = Paths.get(uploadPath, currentUser.getProfilePicture());
                    Files.deleteIfExists(filePath);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            
            // Delete user from database (cascades to related data)
            boolean deleted = userDao.deleteUser(currentUser.getId());
            
            if (deleted) {
                // Invalidate session
                HttpSession session = request.getSession(false);
                if (session != null) {
                    session.invalidate();
                }
                
                // Redirect to home page
                response.sendRedirect(request.getContextPath() + "/Home");
            } else {
                request.setAttribute("errorMessage", "Failed to delete account. Please try again.");
                request.setAttribute("currentUser", currentUser);
                request.getRequestDispatcher("/settings.jsp").forward(request, response);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "An error occurred while deleting your account");
            request.setAttribute("currentUser", currentUser);
            request.getRequestDispatcher("/settings.jsp").forward(request, response);
        }
    }
} 