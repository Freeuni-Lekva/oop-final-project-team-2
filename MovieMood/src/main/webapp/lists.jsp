<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.moviemood.bean.User" %>
<%@ page import="com.moviemood.bean.UserList" %>
<%@ page import="java.util.List" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>My Lists - MovieMood</title>
    <link rel="stylesheet" href="assets/css/navbar.css">
    <link rel="stylesheet" type="text/css" href="assets/css/mainpage.css">
    <style>
        .list-card-container {
            position: relative;
        }
        
        .list-card {
            background: rgba(255, 255, 255, 0.1);
            border-radius: 15px;
            overflow: hidden;
            transition: transform 0.3s, box-shadow 0.3s;
            backdrop-filter: blur(10px);
            border: 1px solid rgba(255, 255, 255, 0.2);
            min-height: 200px;
            display: flex;
            flex-direction: column;
        }

        .list-card:hover {
            transform: translateY(-5px);
            box-shadow: 0 10px 30px rgba(0, 0, 0, 0.3);
        }

        .list-header {
            background: linear-gradient(135deg, #f39c12, #e67e22);
            padding: 20px;
            color: white;
            flex-shrink: 0;
        }

        .list-name {
            font-size: 18px;
            font-weight: 600;
            margin-bottom: 5px;
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
            color: white;
            text-decoration: none;
            display: block;
            transition: color 0.3s ease;
        }

        .list-name:hover {
            color: rgba(255, 255, 255, 0.8);
            text-decoration: none;
        }

        .list-privacy {
            font-size: 12px;
            opacity: 0.9;
            background: rgba(255, 255, 255, 0.2);
            padding: 2px 8px;
            border-radius: 10px;
            display: inline-block;
        }

        .list-content {
            padding: 15px 20px;
            flex-grow: 1;
            display: flex;
            flex-direction: column;
        }

        .list-description {
            color: #bdc3c7;
            font-size: 14px;
            line-height: 1.4;
            margin-bottom: 15px;
            flex-grow: 1;
        }

        .list-meta {
            display: flex;
            justify-content: space-between;
            align-items: center;
            color: #95a5a6;
            font-size: 12px;
            margin-top: auto;
        }

        .list-actions {
            position: absolute;
            top: 10px;
            right: 10px;
            opacity: 0;
            transition: opacity 0.3s ease;
            display: flex;
            gap: 5px;
        }

        .list-card-container:hover .list-actions {
            opacity: 1;
        }

        .action-btn {
            background: rgba(0, 0, 0, 0.7);
            color: white;
            border: none;
            border-radius: 50%;
            width: 28px;
            height: 28px;
            font-size: 12px;
            cursor: pointer;
            display: flex;
            align-items: center;
            justify-content: center;
            transition: background 0.3s ease;
        }

        .action-btn.delete:hover {
            background: rgba(220, 53, 69, 0.9);
        }

        .action-btn.edit:hover {
            background: rgba(52, 152, 219, 0.9);
        }

        .create-list-btn {
            background: linear-gradient(135deg, #f39c12, #e67e22);
            color: white;
            text-decoration: none;
            padding: 15px 30px;
            border-radius: 25px;
            font-weight: 600;
            transition: all 0.3s ease;
            box-shadow: 0 4px 15px rgba(243, 156, 18, 0.3);
            display: inline-block;
            margin-bottom: 30px;
        }

        .create-list-btn:hover {
            transform: translateY(-2px);
            box-shadow: 0 6px 20px rgba(243, 156, 18, 0.4);
            color: white;
            text-decoration: none;
        }

        .empty-lists {
            text-align: center;
            padding: 60px 20px;
            color: #bdc3c7;
        }

        .empty-lists h3 {
            font-size: 24px;
            margin-bottom: 10px;
            color: #95a5a6;
        }

        .empty-lists p {
            font-size: 16px;
            margin-bottom: 30px;
        }

        .lists-grid {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
            gap: 20px;
        }



        @media (max-width: 768px) {
            .lists-grid {
                grid-template-columns: 1fr;
                gap: 15px;
            }
            
            .create-list-btn {
                display: block;
                text-align: center;
                margin: 0 auto 30px auto;
                max-width: 250px;
            }
        }
    </style>
</head>
<body>
<!-- Include Navigation Bar -->
<jsp:include page="WEB-INF/includes/navbar.jsp" />

<main class="main-content">
    <div class="container">
        <h1 class="hero-title">My <span class="highlight">Lists</span></h1>
        
        <%
            User currentUser = (User) request.getAttribute("currentUser");
            List<UserList> userLists = (List<UserList>) request.getAttribute("userLists");
        %>
        
        <!-- Create New List Button -->
        <a href="#" class="create-list-btn" onclick="showCreateListForm()">+ Create New List</a>
        
        <section class="popular-section">
            <h2 class="section-title">Your Movie Lists (<%= userLists.size() %>)</h2>
            
            <% if (userLists.isEmpty()) { %>
                <div class="empty-lists">
                    <h3>No lists yet</h3>
                    <p>Create your first movie list to organize your favorite films!</p>
                    <a href="#" class="create-list-btn" onclick="showCreateListForm()">Create Your First List</a>
                </div>
            <% } else { %>
                <div class="lists-grid">
                    <% for (UserList list : userLists) { %>
                        <div class="list-card-container">
                            <div class="list-card">
                                <div class="list-header">
                                    <a href="<%= request.getContextPath() %>/list/view?id=<%= list.getId() %>" 
                                       class="list-name" 
                                       title="Click to view <%= list.getName() %>">
                                        <%= list.getName() %>
                                    </a>
                                    <div class="list-privacy"><%= list.isPublic() ? "Public" : "Private" %></div>
                                </div>
                                <div class="list-content">
                                    <div class="list-description">
                                        <% if (list.getDescription() != null && !list.getDescription().trim().isEmpty()) { %>
                                            <%= list.getDescription() %>
                                        <% } else { %>
                                            <em>No description</em>
                                        <% } %>
                                    </div>
                                    <div class="list-meta">
                                        <span><%= list.getMovieCount() %> movie<%= list.getMovieCount() != 1 ? "s" : "" %></span>
                                        <span><%= list.getCreatedAt() != null ? 
                                            list.getCreatedAt().toString().substring(0, 10) : "Unknown" %></span>
                                    </div>

                                </div>
                            </div>
                            <div class="list-actions">
                                <button class="action-btn edit" title="Edit List" onclick="editList(<%= list.getId() %>)">✎</button>
                                <button class="action-btn delete" title="Delete List" onclick="deleteList(<%= list.getId() %>)">✕</button>
                            </div>
                        </div>
                    <% } %>
                </div>
            <% } %>
        </section>
    </div>
</main>

<!-- Create List Modal -->
<div id="createListModal" style="display: none; position: fixed; top: 0; left: 0; width: 100%; height: 100%; background: rgba(0,0,0,0.8); z-index: 1000;">
    <div style="position: absolute; top: 50%; left: 50%; transform: translate(-50%, -50%); background: #2a2a2a; padding: 30px; border-radius: 15px; width: 90%; max-width: 500px;">
        <h3 style="color: #f39c12; margin-bottom: 20px;">Create New List</h3>
        <form method="post" action="lists">
            <input type="hidden" name="action" value="create">
            
            <div style="margin-bottom: 15px;">
                <label style="display: block; color: #f39c12; margin-bottom: 5px;">List Name:</label>
                <input type="text" name="listName" required style="width: 100%; padding: 10px; background: #333; border: 1px solid #555; color: white; border-radius: 5px;" placeholder="e.g., Best Horror Movies">
            </div>
            
            <div style="margin-bottom: 15px;">
                <label style="display: block; color: #f39c12; margin-bottom: 5px;">Description (Optional):</label>
                <textarea name="listDescription" rows="3" style="width: 100%; padding: 10px; background: #333; border: 1px solid #555; color: white; border-radius: 5px; resize: vertical;" placeholder="Describe your list..."></textarea>
            </div>
            
            <div style="margin-bottom: 20px;">
                <label style="color: #bdc3c7;">
                    <input type="checkbox" name="isPublic" value="true" checked style="margin-right: 8px;"> 
                    Make this list public
                </label>
            </div>
            
            <div style="display: flex; gap: 10px; justify-content: flex-end;">
                <button type="button" onclick="hideCreateListForm()" style="background: #666; color: white; border: none; padding: 10px 20px; border-radius: 5px; cursor: pointer;">Cancel</button>
                <button type="submit" style="background: #f39c12; color: white; border: none; padding: 10px 20px; border-radius: 5px; cursor: pointer;">Create List</button>
            </div>
        </form>
    </div>
</div>

<!-- Edit List Modal -->
<div id="editListModal" style="display: none; position: fixed; top: 0; left: 0; width: 100%; height: 100%; background: rgba(0,0,0,0.8); z-index: 1000;">
    <div style="position: absolute; top: 50%; left: 50%; transform: translate(-50%, -50%); background: #2a2a2a; padding: 30px; border-radius: 15px; width: 90%; max-width: 500px;">
        <h3 style="color: #f39c12; margin-bottom: 20px;">Edit List</h3>
        <form method="post" action="lists">
            <input type="hidden" name="action" value="edit">
            <input type="hidden" name="listId" id="editListId">
            
            <div style="margin-bottom: 15px;">
                <label style="display: block; color: #f39c12; margin-bottom: 5px;">List Name:</label>
                <input type="text" name="listName" id="editListName" required style="width: 100%; padding: 10px; background: #333; border: 1px solid #555; color: white; border-radius: 5px;">
            </div>
            
            <div style="margin-bottom: 15px;">
                <label style="display: block; color: #f39c12; margin-bottom: 5px;">Description (Optional):</label>
                <textarea name="listDescription" id="editListDescription" rows="3" style="width: 100%; padding: 10px; background: #333; border: 1px solid #555; color: white; border-radius: 5px; resize: vertical;"></textarea>
            </div>
            
            <div style="margin-bottom: 20px;">
                <label style="color: #bdc3c7;">
                    <input type="checkbox" name="isPublic" id="editListPublic" value="true" style="margin-right: 8px;"> 
                    Make this list public
                </label>
            </div>
            
            <div style="display: flex; gap: 10px; justify-content: flex-end;">
                <button type="button" onclick="hideEditListForm()" style="background: #666; color: white; border: none; padding: 10px 20px; border-radius: 5px; cursor: pointer;">Cancel</button>
                <button type="submit" style="background: #f39c12; color: white; border: none; padding: 10px 20px; border-radius: 5px; cursor: pointer;">Update List</button>
            </div>
        </form>
    </div>
</div>

<!-- Hidden Delete Form -->
<form id="deleteListForm" method="post" action="lists" style="display: none;">
    <input type="hidden" name="action" value="delete">
    <input type="hidden" name="listId" id="deleteListId">
</form>

<script>
    function showCreateListForm() {
        document.getElementById('createListModal').style.display = 'block';
    }
    
    function hideCreateListForm() {
        document.getElementById('createListModal').style.display = 'none';
    }
    
    // Store list data for easy access
    const listData = {
        <% for (UserList list : userLists) { %>
        <%= list.getId() %>: {
            name: '<%= list.getName().replace("'", "\\'") %>',
            description: '<%= list.getDescription() != null ? list.getDescription().replace("'", "\\'") : "" %>',
            isPublic: <%= list.isPublic() %>
        },
        <% } %>
    };
    
    function editList(listId) {
        const list = listData[listId];
        if (!list) return;
        
        // Populate the edit form with current data
        document.getElementById('editListId').value = listId;
        document.getElementById('editListName').value = list.name;
        document.getElementById('editListDescription').value = list.description;
        document.getElementById('editListPublic').checked = list.isPublic;
        
        // Show the edit modal
        document.getElementById('editListModal').style.display = 'block';
    }
    
    function hideEditListForm() {
        document.getElementById('editListModal').style.display = 'none';
    }
    
    function deleteList(listId) {
        const list = listData[listId];
        if (!list) return;
        
        if (confirm('Are you sure you want to delete "' + list.name + '"?\n\nThis action cannot be undone and will remove all movies from this list.')) {
            // Set the list ID and submit the delete form
            document.getElementById('deleteListId').value = listId;
            document.getElementById('deleteListForm').submit();
        }
    }
    
    // Close modals when clicking outside
    document.getElementById('createListModal').addEventListener('click', function(e) {
        if (e.target === this) {
            hideCreateListForm();
        }
    });
    
    document.getElementById('editListModal').addEventListener('click', function(e) {
        if (e.target === this) {
            hideEditListForm();
        }
    });
</script>

</body>
</html> 