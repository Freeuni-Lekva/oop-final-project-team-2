<%@ page import="com.moviemood.bean.FriendRequest" %>
<%@ page import="java.util.List" %><%--
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
    <title>Friend Requests</title>
</head>
<body>

    <h2>Incoming Friend Requests</h2>
    <%
        List<FriendRequest> incomingRequests = (List<FriendRequest>) request.getAttribute("incomingRequests");
        if (incomingRequests != null && !incomingRequests.isEmpty()) {
    %>
    <ul>
        <%
            for(FriendRequest req : incomingRequests) {
        %>
            <li>
                <%= req.getSenderUsername()%> Received <%= req.getRequestTime() %>
                <form method = "post" action="accept-friend-request">
                    <input type="hidden" name = "requestId" value = "<%= req.getRequestId()%>">
                    <!-- could delete this 2 hidden inputs later -->
                    <input type="hidden" name = "senderId" value = "<%= req.getSenderId()%>">
                    <input type="hidden" name = "receiverId" value = "<%= req.getReceiverId()%>">
                    <button type="submit">Accept</button>
                </form>

                <form method = "post" action="reject-friend-request">
                    <input type="hidden" name = "requestId" value = "<%= req.getRequestId()%>">
                    <button type = "submit">Reject</button>
                </form>

            </li>
        <%
            }
        %>
    </ul>

    <%
    } else {
    %>
    <p>No incoming friend requests</p>
    <%
        }
    %>

    <h2>Sent Friend Request</h2>

    <%
        List<FriendRequest> sentRequests = (List<FriendRequest>) request.getAttribute("sentRequests");
        if(sentRequests != null && !sentRequests.isEmpty()) {
    %>

    <ul>
        <%
            for(FriendRequest req : sentRequests) {
        %>

            <li>
                To: <%= req.getReceiverUsername()%>  Sent on <%= req.getRequestTime()%>
            </li>
        <%
            }
        %>
    </ul>
    <%
    } else {
    %>
    <p>You have no sent friend requests</p>
    <%
        }
    %>

</body>
</html>
