<%@ page import="com.moviemood.bean.FriendRequest" %>
<%@ page import="java.util.List" %>
<%@ page import="com.moviemood.bean.User" %>
<%@ page import="com.moviemood.bean.FriendSuggestion" %>
<%@ page import="java.time.format.DateTimeFormatter" %><%--
  Created by IntelliJ IDEA.
  User: User
  Date: 7/8/2025
  Time: 2:52 AM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<!doctype html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport"
          content="width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <link rel="stylesheet" href="assets/css/navbar.css?v=<%= System.currentTimeMillis() %>">
    <link rel="stylesheet" href="assets/css/friend-requests.css?v=<%= System.currentTimeMillis() %>">

    <title>Friend Requests</title>
</head>
<body>

    <jsp:include page="/WEB-INF/includes/navbar.jsp" />

    <%
        String tab = request.getParameter("tab");
        if (tab == null) {
            tab = "your_friends";
        }
    %>

    <div class="container">
        <h1><span class="highlight">Friends</span></h1>
        <p>Manage your connections and discover new people</p>
        
        <% 
            String message = request.getParameter("message");
            String error = request.getParameter("error");
            if (message != null && !message.isEmpty()) {
        %>
            <div class="success-message" style="background-color: #d4edda; color: #155724; padding: 10px; border-radius: 5px; margin-bottom: 15px;">
                <%= message %>
            </div>
        <% } else if (error != null && !error.isEmpty()) { %>
            <div class="error-message" style="background-color: #f8d7da; color: #721c24; padding: 10px; border-radius: 5px; margin-bottom: 15px;">
                <%= error %>
            </div>
        <% } %>

        <div class="tabs">
            <a href="friend-requests?tab=your_friends" class="<%= "your_friends".equals(tab) ? "active" : "" %>"><button>Your Friends</button></a>
            <a href="friend-requests?tab=suggestions" class="<%= "suggestions".equals(tab) ? "active" : "" %>"><button>Suggestions</button></a>
            <a href="friend-requests?tab=incoming" class="<%= "incoming".equals(tab) ? "active" : "" %>"><button>Friend Requests</button></a>
            <a href="friend-requests?tab=sent" class="<%= "sent".equals(tab) ? "active" : "" %>"><button>Sent Requests</button></a>
        </div>

        <% if ("your_friends".equals(tab) || "suggestions".equals(tab)) { %>
        <div class = "search-bar">
            <form action="friend-requests" method="get">
                <div class="search-wrapper">
                    <input type="text" name="search" placeholder="Search For Friends" value="<%= request.getParameter("search") != null ? request.getParameter("search") : "" %>">
                    <input type="hidden" name="tab" value="<%= tab %>">
                    <button type="submit" class="icon-button">
                        <i class="fa-solid fa-magnifying-glass" style="color:#f39c12"><img src="Images/magnifying-glass-solid.svg" alt=""></i>
                    </button>
                </div>
            </form>
        </div>
        <% } %>


        <% if("incoming".equals(tab)) { %>
        <h2>Incoming Requests</h2>
        <%
            List<FriendRequest> incomingRequests = (List<FriendRequest>) request.getAttribute("incomingRequests");
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMMM d, yyyy");
            if (incomingRequests != null && !incomingRequests.isEmpty()) {
        %>
        <ul class="request-list">
            <%
                for(FriendRequest req : incomingRequests) {
            %>
                <li class="friend-request">
                    <span><%= req.getSenderUsername()%> sent a request on <%= req.getRequestTime().format(dateFormatter) %></span>
                    <div class="button-group">
                        <form method = "post" action="accept-friend-request">
                            <input type="hidden" name = "requestId" value = "<%= req.getRequestId()%>">
                            <!-- could delete this 2 hidden inputs later -->
                            <input type="hidden" name = "senderId" value = "<%= req.getSenderId()%>">
                            <input type="hidden" name = "receiverId" value = "<%= req.getReceiverId()%>">
                            <button type="submit" class="accept-button">Accept</button>
                        </form>

                        <form method = "post" action="reject-friend-request">
                            <input type="hidden" name = "requestId" value = "<%= req.getRequestId()%>">
                            <button type = "submit" class="reject-button">Reject</button>
                        </form>
                    </div>
                </li>
            <%
                }
            %>
        </ul>

        <%
        } else {
        %>
        <p class = "empty-message">No incoming friend requests</p>
        <%
            }
        %>


        <% } else if ("sent".equals(tab)) { %>
        <h2>Sent Friend Requests</h2>

        <%
            List<FriendRequest> sentRequests = (List<FriendRequest>) request.getAttribute("sentRequests");
            DateTimeFormatter sentDateFormatter = DateTimeFormatter.ofPattern("MMMM d, yyyy");
            if(sentRequests != null && !sentRequests.isEmpty()) {
        %>

        <ul class="request-list">
            <%
                for(FriendRequest req : sentRequests) {
            %>

                <li class="friend-request">
                    <span>To: <%= req.getReceiverUsername()%> • Sent on <%= req.getRequestTime().format(sentDateFormatter)%></span>
                    <div class="button-group">
                        <form method="post" action="cancel-friend-request">
                            <input type="hidden" name="requestId" value="<%= req.getRequestId()%>">
                            <button type="submit" class="cancel-button">Cancel</button>
                        </form>
                    </div>
                </li>
            <%
                }
            %>
        </ul>
        <%
        } else {
        %>
            <p class = "empty-message">You have no sent friend requests</p>
        <%
            }
        %>

        <% } else if ("your_friends".equals(tab)) { %>
        <h2>Your Friends</h2>
        <%
            String searchQuery = request.getParameter("search");
            if (searchQuery != null && !searchQuery.trim().isEmpty()) {
        %>
            <p>Search results for "<%= searchQuery %>" in your friends</p>
        <% } %>
        <%
            List<User> allFriends = (List<User>) request.getAttribute("allFriends");
            if (allFriends != null && !allFriends.isEmpty()) {
        %>
        <ul class="request-list">
            <% for (User friend : allFriends) { %>
            <li class="friend-request">
                <span> <%= friend.getUsername() %></span>
                <div class="button-group">
                    <a href="profile?user=<%= friend.getUsername() %>" class="view-profile-button">View Profile</a>
                    <form method="post" action="unfriend" style="display: inline;">
                        <input type="hidden" name="friendUsername" value="<%= friend.getUsername() %>">
                        <button type="submit" class="unfriend-button">Unfriend</button>
                    </form>
                </div>
            </li>
            <% } %>
        </ul>
        <%
        } else {
        %>
        <%
            if (searchQuery != null && !searchQuery.trim().isEmpty()) {
        %>
            <p class="empty-message">No friends found matching "<%= searchQuery %>".</p>
        <% } else { %>
            <p class="empty-message">You don't have any friends yet.</p>
        <% } %>
        <%
            }
        %>
        <% } else if ("suggestions".equals(tab)) { %>
        <h2>Friend Suggestions</h2>
        <%
            String searchQuery = request.getParameter("search");
            if (searchQuery != null && !searchQuery.trim().isEmpty()) {
        %>
            <p>Search results for "<%= searchQuery %>" in suggestions and other users</p>
        <% } else { %>
            <p>People you might know based on mutual friends</p>
        <% } %>
        <%
            List<FriendSuggestion> friendSuggestions = (List<FriendSuggestion>) request.getAttribute("friendSuggestions");
            if (friendSuggestions != null && !friendSuggestions.isEmpty()) {
        %>
        <ul class="request-list">
            <% for (FriendSuggestion suggestion : friendSuggestions) { %>
            <li class="friend-request">
                <span>
                    <%= suggestion.getUser().getUsername() %>
                    <% if (suggestion.getMutualFriendCount() == 0) { %>
                        • No mutual friends
                    <% } else if (suggestion.getMutualFriendCount() == 1) { %>
                        • 1 mutual friend
                    <% } else { %>
                        • <%= suggestion.getMutualFriendCount() %> mutual friends
                    <% } %>
                </span>
                <div class="button-group">
                    <form method="post" action="send-friend-request" style="display: inline;">
                        <input type="hidden" name="receiverUsername" value="<%= suggestion.getUser().getUsername() %>">
                        <button type="submit" class="accept-button">Add Friend</button>
                    </form>
                    <a href="profile?user=<%= suggestion.getUser().getUsername() %>" class="view-profile-button">View Profile</a>
                </div>
            </li>
            <% } %>
        </ul>
        <%
        } else {
        %>
        <%
            if (searchQuery != null && !searchQuery.trim().isEmpty()) {
        %>
            <p class="empty-message">No users found matching "<%= searchQuery %>".</p>
        <% } else { %>
            <p class="empty-message">No friend suggestions available. Connect with more people to see suggestions!</p>
        <% } %>
        <%
            }
        %>
        <% } %>
    </div>
</body>
</html>
