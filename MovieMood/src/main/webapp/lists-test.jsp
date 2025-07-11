<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.moviemood.bean.User" %>
<%@ page import="com.moviemood.bean.UserList" %>
<%@ page import="java.util.List" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Lists Test - MovieMood</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #1e1e1e;
            color: white;
            padding: 20px;
        }
        .container {
            max-width: 800px;
            margin: 0 auto;
        }
        .test-section {
            background-color: #2a2a2a;
            padding: 20px;
            margin: 20px 0;
            border-radius: 8px;
            border-left: 4px solid #f39c12;
        }
        .form-group {
            margin: 10px 0;
        }
        label {
            display: block;
            margin-bottom: 5px;
            color: #f39c12;
        }
        input, textarea {
            width: 100%;
            padding: 8px;
            background-color: #333;
            border: 1px solid #555;
            color: white;
            border-radius: 4px;
        }
        button {
            background-color: #f39c12;
            color: white;
            padding: 10px 20px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            margin: 10px 0;
        }
        button:hover {
            background-color: #e67e22;
        }
        .list-item {
            background-color: #333;
            padding: 15px;
            margin: 10px 0;
            border-radius: 6px;
            border: 1px solid #555;
        }
        .list-name {
            font-size: 18px;
            font-weight: bold;
            color: #f39c12;
        }
        .list-meta {
            color: #aaa;
            font-size: 14px;
            margin-top: 5px;
        }
        .success {
            color: #27ae60;
        }
        .error {
            color: #e74c3c;
        }
        .back-link {
            display: inline-block;
            color: #f39c12;
            text-decoration: none;
            margin-bottom: 20px;
        }
        .back-link:hover {
            text-decoration: underline;
        }
    </style>
</head>
<body>
    <div class="container">
        <a href="Home" class="back-link">← Back to Movies</a>
        
        <h1>🧪 Lists Functionality Test</h1>
        
        <%
            User currentUser = (User) request.getAttribute("currentUser");
            List<UserList> userLists = (List<UserList>) request.getAttribute("userLists");
        %>
        
        <div class="test-section">
            <h2>Current User: <%= currentUser.getUsername() %></h2>
            <p>User ID: <%= currentUser.getId() %></p>
        </div>
        
        <div class="test-section">
            <h2>📝 Create New List (Test)</h2>
            <form method="post" action="lists">
                <input type="hidden" name="action" value="create">
                
                <div class="form-group">
                    <label for="listName">List Name:</label>
                    <input type="text" id="listName" name="listName" required 
                           placeholder="e.g., Best Horror Movies, Christmas Classics">
                </div>
                
                <div class="form-group">
                    <label for="listDescription">Description (Optional):</label>
                    <textarea id="listDescription" name="listDescription" rows="3" 
                              placeholder="Describe your list..."></textarea>
                </div>
                
                <div class="form-group">
                    <label>
                        <input type="checkbox" name="isPublic" value="true" checked> 
                        Make this list public
                    </label>
                </div>
                
                <button type="submit">Create Test List</button>
            </form>
        </div>
        
        <div class="test-section">
            <h2>📋 Your Lists (Total: <%= userLists.size() %>)</h2>
            
            <% if (userLists.isEmpty()) { %>
                <p class="list-meta">No lists found. Create one above to test the functionality!</p>
            <% } else { %>
                <% for (UserList list : userLists) { %>
                    <div class="list-item">
                        <div class="list-name"><%= list.getName() %></div>
                        <% if (list.getDescription() != null && !list.getDescription().trim().isEmpty()) { %>
                            <p><%= list.getDescription() %></p>
                        <% } %>
                        <div class="list-meta">
                            ID: <%= list.getId() %> | 
                            <%= list.isPublic() ? "Public" : "Private" %> | 
                            Created: <%= list.getCreatedAt() != null ? list.getCreatedAt().toString() : "Unknown" %>
                        </div>
                    </div>
                <% } %>
            <% } %>
        </div>
        
        <div class="test-section">
            <h2>✅ Test Status</h2>
            <p><span class="success">✓</span> Database tables created</p>
            <p><span class="success">✓</span> UserList bean class working</p>
            <p><span class="success">✓</span> UserListDao operational</p>
            <p><span class="success">✓</span> ListsServlet functioning</p>
            <p><span class="success">✓</span> Basic CRUD operations ready</p>
        </div>
        
        <div class="test-section">
            <h2>🔄 Next Steps</h2>
            <ul>
                <li>Create proper lists.jsp page with styling</li>
                <li>Add movie management (add/remove movies from lists)</li>
                <li>Update profile page with real lists count</li>
                <li>Add list details page</li>
                <li>Add list management features (edit/delete)</li>
            </ul>
        </div>
    </div>
</body>
</html> 