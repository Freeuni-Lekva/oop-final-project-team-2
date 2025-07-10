<%@ page import="com.moviemood.bean.FriendRequest" %>
<%@ page import="java.util.List" %>
<%@ page import="com.moviemood.bean.User" %><%--
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
    <link rel="stylesheet" href="friend-requests.css?v=<%= System.currentTimeMillis() %>">

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
        <h1>Friends</h1>
        <p>Manage Your connections and discover new people</p>

        <div class="tabs">
            <a href="friend-requests.jsp?tab=your_friends" class="<%= "your_friends".equals(tab) ? "active" : "" %>"><button>Your Friends</button></a>
            <a href="friend-requests.jsp?tab=suggestions" class="<%= "suggestions".equals(tab) ? "active" : "" %>"><button>Suggestions</button></a>
            <a href="friend-requests.jsp?tab=incoming" class="<%= "incoming".equals(tab) ? "active" : "" %>"><button>Friend Requests</button></a>
            <a href="friend-requests.jsp?tab=sent" class="<%= "sent".equals(tab) ? "active" : "" %>"><button>Sent Requests</button></a>
        </div>

        <div class = "search-bar">
            <form action="send-friend-request" method="post">
                <div class="search-wrapper">
                    <input type="text" name="receiverUsername" placeholder="Search For Friends">
                    <button type="submit" class="icon-button">
                        <i class="fa-solid fa-magnifying-glass" style="color:#f39c12"><img src="Images/magnifying-glass-solid.svg" alt=""></i>
                    </button>
                </div>
            </form>
        </div>


        <% if("incoming".equals(tab)) { %>
        <h2>Incoming Requests</h2>
        <%
            List<FriendRequest> incomingRequests = (List<FriendRequest>) request.getAttribute("incomingRequests");
            if (incomingRequests != null && !incomingRequests.isEmpty()) {
        %>
        <ul class="request-list">
            <%
                for(FriendRequest req : incomingRequests) {
            %>
                <li class="friend-request">
                    <span><%= req.getSenderUsername()%> Sent a request on <%= req.getRequestTime() %></span>
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
            if(sentRequests != null && !sentRequests.isEmpty()) {
        %>

        <ul class="request-list">
            <%
                for(FriendRequest req : sentRequests) {
            %>

                <li class="friend-request">
                    <span>To: <%= req.getReceiverUsername()%>  Sent on <%= req.getRequestTime()%></span>
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
            List<User> allFriends = (List<User>) request.getAttribute("allFriends");
            if (allFriends != null && !allFriends.isEmpty()) {
        %>
        <ul class="request-list">
            <% for (User friend : allFriends) { %>
            <li class="friend-request">
                <span><%= friend.getUsername() %></span>
            </li>
            <% } %>
        </ul>
        <%
        } else {
        %>
        <p class="empty-message">You don't have any friends yet.</p>
        <%
            }
        %>
        <% } %>
    </div>
</body>
</html>
