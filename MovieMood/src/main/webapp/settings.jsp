<%@ page import="com.moviemood.bean.User" %>

<%
    User currentUser = (User) request.getAttribute("currentUser");
    String successMessage = (String) request.getAttribute("successMessage");
    String errorMessage = (String) request.getAttribute("errorMessage");
    String infoMessage = (String) request.getAttribute("infoMessage");
%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Settings - MovieMood</title>
    <link rel="stylesheet" href="assets/css/navbar.css">
    <link rel="stylesheet" type="text/css" href="assets/css/mainpage.css">
    <link rel="stylesheet" type="text/css" href="assets/css/profile.css">
    <style>
        /* Settings-specific styles */
        .settings-section {
            background: rgba(255, 255, 255, 0.05);
            border-radius: 15px;
            padding: 30px;
            margin-bottom: 30px;
            backdrop-filter: blur(10px);
            border: 1px solid rgba(255, 255, 255, 0.1);
            text-align: center;
        }

        .section-title {
            font-size: 1.5rem;
            font-weight: 600;
            color: #f39c12;
            margin-bottom: 20px;
            border-bottom: 2px solid #f39c12;
            padding-bottom: 10px;
        }

        .form-group {
            margin-bottom: 20px;
            max-width: 400px;
            margin-left: auto;
            margin-right: auto;
        }

        .form-group label {
            display: block;
            font-weight: 600;
            color: #fff;
            margin-bottom: 8px;
            text-align: center;
        }

        .form-group input {
            width: 100%;
            padding: 12px;
            background: rgba(255, 255, 255, 0.1);
            border: 1px solid rgba(255, 255, 255, 0.2);
            border-radius: 8px;
            color: #fff;
            font-size: 1rem;
            transition: all 0.3s ease;
        }

        .form-group input:focus {
            outline: none;
            border-color: #f39c12;
            box-shadow: 0 0 0 3px rgba(243, 156, 18, 0.1);
        }

        .form-group input::placeholder {
            color: rgba(255, 255, 255, 0.5);
        }

        .form-group small {
            color: #bdc3c7;
            display: block;
            margin-top: 5px;
            font-size: 0.9rem;
        }

        .current-info {
            background: rgba(243, 156, 18, 0.1);
            border: 1px solid rgba(243, 156, 18, 0.3);
            border-radius: 8px;
            padding: 15px;
            margin-bottom: 20px;
            max-width: 400px;
            margin-left: auto;
            margin-right: auto;
            text-align: center;
        }

        .current-info h4 {
            color: #f39c12;
            margin-bottom: 8px;
            font-size: 1.1rem;
        }

        .current-info p {
            color: #ecf0f1;
            margin: 0;
            font-size: 1rem;
        }

        .profile-preview {
            width: 100px;
            height: 100px;
            border-radius: 50%;
            object-fit: cover;
            border: 3px solid #f39c12;
            margin: 10px auto 0 auto;
            display: block;
        }

        .profile-avatar-large {
            width: 100px;
            height: 100px;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 36px;
            font-weight: bold;
            color: white;
            border: 3px solid #f39c12;
            margin: 10px auto 0 auto;
            line-height: 1;
            text-align: center;
        }

        .form-btn {
            background: linear-gradient(135deg, #f39c12, #e67e22);
            color: white;
            padding: 12px 24px;
            border: none;
            border-radius: 8px;
            font-weight: 600;
            font-size: 1rem;
            cursor: pointer;
            transition: all 0.3s ease;
            box-shadow: 0 4px 15px rgba(243, 156, 18, 0.3);
        }

        .form-btn:hover {
            transform: translateY(-2px);
            box-shadow: 0 6px 20px rgba(243, 156, 18, 0.4);
        }

        .form-btn.danger {
            background: linear-gradient(135deg, #e74c3c, #c0392b);
            box-shadow: 0 4px 15px rgba(231, 76, 60, 0.3);
        }

        .form-btn.danger:hover {
            box-shadow: 0 6px 20px rgba(231, 76, 60, 0.4);
        }

        .danger-section {
            border: 2px solid #e74c3c;
            background: rgba(231, 76, 60, 0.1);
        }

        .danger-section .section-title {
            color: #e74c3c;
            border-bottom-color: #e74c3c;
        }

        .warning-text {
            color: #e74c3c;
            margin-bottom: 20px;
            font-weight: 500;
            background: rgba(231, 76, 60, 0.1);
            padding: 15px;
            border-radius: 8px;
            border: 1px solid rgba(231, 76, 60, 0.3);
        }

        .message {
            padding: 15px;
            border-radius: 8px;
            margin-bottom: 20px;
            text-align: center;
            font-weight: 500;
        }

        .message.success {
            background: rgba(46, 204, 113, 0.2);
            border: 1px solid #2ecc71;
            color: #2ecc71;
        }

        .message.error {
            background: rgba(231, 76, 60, 0.2);
            border: 1px solid #e74c3c;
            color: #e74c3c;
        }

        .message.info {
            background: rgba(52, 152, 219, 0.2);
            border: 1px solid #3498db;
            color: #3498db;
        }

        .settings-header {
            text-align: center;
            margin-bottom: 40px;
            padding: 30px;
            background: rgba(255, 255, 255, 0.05);
            border-radius: 15px;
            backdrop-filter: blur(10px);
            border: 1px solid rgba(255, 255, 255, 0.1);
        }

        .settings-header h1 {
            font-size: 2.5rem;
            font-weight: 700;
            color: #fff;
            margin-bottom: 10px;
        }

        .settings-header p {
            color: #bdc3c7;
            font-size: 1.1rem;
            margin: 0;
        }

        input[type="file"] {
            padding: 8px;
            background: rgba(255, 255, 255, 0.05);
            border: 1px solid rgba(255, 255, 255, 0.2);
            border-radius: 8px;
            color: #fff;
            width: 100%;
        }

        input[type="file"]:focus {
            outline: none;
            border-color: #f39c12;
            box-shadow: 0 0 0 3px rgba(243, 156, 18, 0.1);
        }

        @media (max-width: 768px) {
            .settings-section {
                padding: 20px;
            }

            .settings-header h1 {
                font-size: 2rem;
            }
        }
    </style>
</head>
<body>
    <!-- Include Navigation Bar -->
    <jsp:include page="WEB-INF/includes/navbar.jsp" />

    <main class="main-content">
        <div class="container">
            <!-- Settings Header -->
            <div class="settings-header">
                <h1>Account Settings</h1>
                <p>Manage your MovieMood account preferences</p>
            </div>

            <!-- Messages -->
            <% if (successMessage != null) { %>
                <div class="message success">
                    <%= successMessage %>
                </div>
            <% } %>

            <% if (errorMessage != null) { %>
                <div class="message error">
                    <%= errorMessage %>
                </div>
            <% } %>

            <% if (infoMessage != null) { %>
                <div class="message info">
                    <%= infoMessage %>
                </div>
            <% } %>

            <!-- Profile Picture Settings -->
            <div class="settings-section">
                <h2 class="section-title">Profile Picture</h2>
                
                <div class="current-info">
                    <h4>Current Profile Picture</h4>
                    <div>
                        <% if (currentUser.getProfilePicture() != null && !currentUser.getProfilePicture().isEmpty()) { %>
                            <img src="/profile-picture/<%= currentUser.getProfilePicture() %>" alt="Current Profile Picture" class="profile-preview">
                        <% } else { %>
                            <div class="profile-avatar-large">
                                <%= currentUser.getUsername().substring(0, 1).toUpperCase() %>
                            </div>
                        <% } %>
                    </div>
                </div>

                <form method="post" action="settings" enctype="multipart/form-data">
                    <input type="hidden" name="action" value="updateProfilePicture">
                    
                    <div class="form-group">
                        <label for="profilePicture">Upload New Profile Picture</label>
                        <input type="file" id="profilePicture" name="profilePicture" accept="image/*" required>
                        <small>Supported formats: JPG, PNG, GIF. Max size: 5MB</small>
                    </div>

                    <button type="submit" class="form-btn">Update Profile Picture</button>
                </form>

                <% if (currentUser.getProfilePicture() != null && !currentUser.getProfilePicture().isEmpty()) { %>
                <form method="post" action="settings" style="margin-top: 15px;">
                    <input type="hidden" name="action" value="deleteProfilePicture">
                    <button type="submit" class="form-btn" style="background: linear-gradient(135deg, #95a5a6, #7f8c8d); box-shadow: 0 4px 15px rgba(149, 165, 166, 0.3);" 
                            onclick="return confirm('Are you sure you want to delete your profile picture?')">
                        Delete Profile Picture
                    </button>
                </form>
                <% } %>
            </div>

            <!-- Username Settings -->
            <div class="settings-section">
                <h2 class="section-title">Username</h2>
                
                <div class="current-info">
                    <h4>Current Username</h4>
                    <p><%= currentUser.getUsername() %></p>
                </div>

                <form method="post" action="settings">
                    <input type="hidden" name="action" value="updateUsername">
                    
                    <div class="form-group">
                        <label for="newUsername">New Username</label>
                        <input type="text" id="newUsername" name="newUsername" placeholder="Enter new username" required>
                    </div>

                    <button type="submit" class="form-btn">Update Username</button>
                </form>
            </div>

            <!-- Password Settings -->
            <div class="settings-section">
                <h2 class="section-title">Password</h2>
                
                <form method="post" action="settings">
                    <input type="hidden" name="action" value="updatePassword">
                    
                    <div class="form-group">
                        <label for="currentPassword">Current Password</label>
                        <input type="password" id="currentPassword" name="currentPassword" placeholder="Enter current password" required>
                    </div>

                    <div class="form-group">
                        <label for="newPassword">New Password</label>
                        <input type="password" id="newPassword" name="newPassword" placeholder="Enter new password" required>
                    </div>

                    <div class="form-group">
                        <label for="confirmPassword">Confirm New Password</label>
                        <input type="password" id="confirmPassword" name="confirmPassword" placeholder="Confirm new password" required>
                    </div>

                    <button type="submit" class="form-btn">Update Password</button>
                </form>
            </div>

            <!-- Delete Account Section -->
            <div class="settings-section danger-section">
                <h2 class="section-title">Delete Account</h2>
                <div class="warning-text">
                    <strong>Warning:</strong> This action cannot be undone. All your data will be permanently deleted.
                </div>
                
                <form method="post" action="settings" id="deleteAccountForm">
                    <input type="hidden" name="action" value="deleteAccount">
                    <button type="submit" class="form-btn danger">
                        Delete My Account
                    </button>
                </form>
            </div>
        </div>
    </main>

    <script>
        // Clear form fields after successful submission
        <% if (successMessage != null) { %>
            // Clear all form inputs
            const inputs = document.querySelectorAll('input[type="text"], input[type="password"]');
            inputs.forEach(input => {
                if (input.name !== 'action') {
                    input.value = '';
                }
            });
        <% } %>

        // Password confirmation validation
        const newPasswordInput = document.getElementById('newPassword');
        const confirmPasswordInput = document.getElementById('confirmPassword');

        function validatePasswords() {
            if (newPasswordInput.value !== confirmPasswordInput.value) {
                confirmPasswordInput.setCustomValidity('Passwords do not match');
            } else {
                confirmPasswordInput.setCustomValidity('');
            }
        }

        if (newPasswordInput && confirmPasswordInput) {
            newPasswordInput.addEventListener('input', validatePasswords);
            confirmPasswordInput.addEventListener('input', validatePasswords);
        }

        // Delete account confirmation
        const deleteAccountForm = document.getElementById('deleteAccountForm');
        if (deleteAccountForm) {
            deleteAccountForm.addEventListener('submit', function(e) {
                if (!confirm('Are you sure you want to delete your account? This action cannot be undone.')) {
                    e.preventDefault();
                }
            });
        }
    </script>
</body>
</html> 