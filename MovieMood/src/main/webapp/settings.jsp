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
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        body {
            font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
            background-color: #1e1e1e;
            color: #fff;
            line-height: 1.6;
            min-height: 100vh;
        }

        .container {
            max-width: 800px;
            margin: 0 auto;
            padding: 40px 20px;
        }

        .navigation {
            text-align: center;
            margin-bottom: 30px;
        }

        .nav-link {
            color: #00d4ff;
            text-decoration: none;
            font-size: 1.1rem;
        }

        .nav-link:hover {
            text-decoration: underline;
        }

        .settings-header {
            text-align: center;
            margin-bottom: 40px;
        }

        .settings-header h1 {
            font-size: 2.5rem;
            font-weight: 700;
            color: #fff;
            margin-bottom: 10px;
        }

        .settings-header p {
            color: #ccc;
            font-size: 1.1rem;
        }

        .message {
            padding: 15px;
            border-radius: 8px;
            margin-bottom: 20px;
            text-align: center;
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

        .settings-section {
            background: rgba(255, 255, 255, 0.05);
            border-radius: 10px;
            padding: 30px;
            margin-bottom: 30px;
            border: 1px solid rgba(255, 255, 255, 0.1);
        }

        .section-title {
            font-size: 1.5rem;
            font-weight: 600;
            color: #00d4ff;
            margin-bottom: 20px;
            border-bottom: 2px solid #00d4ff;
            padding-bottom: 10px;
        }

        .form-group {
            margin-bottom: 20px;
        }

        .form-group label {
            display: block;
            font-weight: 600;
            color: #fff;
            margin-bottom: 8px;
        }

        .form-group input {
            width: 100%;
            padding: 12px;
            background: rgba(255, 255, 255, 0.1);
            border: 1px solid rgba(255, 255, 255, 0.2);
            border-radius: 6px;
            color: #fff;
            font-size: 1rem;
        }

        .form-group input:focus {
            outline: none;
            border-color: #00d4ff;
            box-shadow: 0 0 0 3px rgba(0, 212, 255, 0.1);
        }

        .form-group input::placeholder {
            color: rgba(255, 255, 255, 0.5);
        }

        .btn {
            padding: 12px 24px;
            background: #00d4ff;
            color: #1e1e1e;
            border: none;
            border-radius: 6px;
            font-weight: 600;
            font-size: 1rem;
            cursor: pointer;
            transition: all 0.3s ease;
        }

        .btn:hover {
            background: #00b8e6;
            transform: translateY(-2px);
        }

        .btn:active {
            transform: translateY(0);
        }

        .current-info {
            background: rgba(0, 212, 255, 0.1);
            border: 1px solid rgba(0, 212, 255, 0.3);
            border-radius: 6px;
            padding: 15px;
            margin-bottom: 20px;
        }

        .current-info h4 {
            color: #00d4ff;
            margin-bottom: 5px;
        }

        .current-info p {
            color: #ccc;
            margin: 0;
        }

        .current-profile-pic {
            margin-top: 10px;
        }

        .profile-preview {
            width: 100px;
            height: 100px;
            border-radius: 50%;
            object-fit: cover;
            border: 3px solid #00d4ff;
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
            border: 3px solid #00d4ff;
        }

        input[type="file"] {
            padding: 8px;
            background: rgba(255, 255, 255, 0.05);
            border: 1px solid rgba(255, 255, 255, 0.2);
            border-radius: 6px;
            color: #fff;
            width: 100%;
        }

        input[type="file"]:focus {
            outline: none;
            border-color: #00d4ff;
            box-shadow: 0 0 0 3px rgba(0, 212, 255, 0.1);
        }

        @media (max-width: 768px) {
            .container {
                padding: 20px 10px;
            }

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
    <div class="container">
        <div class="navigation">
            <a href="profile" class="nav-link">&lt; Back to Profile</a>
        </div>

        <div class="settings-header">
            <h1>Account Settings</h1>
            <p>Manage your MovieMood account preferences</p>
        </div>

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
                <div class="current-profile-pic">
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
                    <small style="color: #ccc; display: block; margin-top: 5px;">Supported formats: JPG, PNG, GIF. Max size: 5MB</small>
                </div>

                <button type="submit" class="btn">Update Profile Picture</button>
            </form>
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

                <button type="submit" class="btn">Update Username</button>
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

                <button type="submit" class="btn">Update Password</button>
            </form>
        </div>

        <!-- Delete Account Section -->
        <div class="settings-section" style="border: 2px solid #e74c3c; background: rgba(231, 76, 60, 0.1);">
            <h2 class="section-title" style="color: #e74c3c;">Delete Account</h2>
            <p style="color: #e74c3c; margin-bottom: 20px; font-weight: 500;">
                Warning: This action cannot be undone. All your data will be permanently deleted.
            </p>
            
            <form method="post" action="settings" id="deleteAccountForm">
                <input type="hidden" name="action" value="deleteAccount">
                <button type="submit" class="btn" style="background: linear-gradient(135deg, #e74c3c, #c0392b); color: white; border: none;">
                    Delete My Account
                </button>
            </form>
        </div>
    </div>

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

        newPasswordInput.addEventListener('input', validatePasswords);
        confirmPasswordInput.addEventListener('input', validatePasswords);

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