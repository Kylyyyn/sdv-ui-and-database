<%@ page session="true" %>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>

<%
    String header = application.getInitParameter("Header");
    String footer = application.getInitParameter("Footer");

    String email = (String) session.getAttribute("email");
    String role  = (String) session.getAttribute("role");

    if (email == null || role == null || !role.equalsIgnoreCase("admin")) {
        response.sendRedirect("error_session.jsp");
        return;
    }
%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Add User</title>
    <link rel="stylesheet" type="text/css" href="css.css">
</head>

<body class="admin-body">

<header><%= header %></header>

<div id="background">
    <img class="twilight" src="images/Twilight.png">
    <img class="layer farcloudA" src="images/farcloud-1.png">
    <img class="layer farcloudB" src="images/farcloud-2.png">
    <img class="layer farcloudC" src="images/farcloud-3.png">
    <img class="layer bigcloud" src="images/bigcloud.PNG">
    <img class="layer cloudA" src="images/cloud-1.png">
    <img class="layer cloudA a2" src="images/cloud-1.png">
    <img class="layer cloudB" src="images/cloud-2.png">
    <img class="layer cloudB b2" src="images/cloud-2.png">
    <img class="layer cloudC" src="images/cloud-3.png">
    <img class="layer mountain far static" src="images/mountain-far.png">
    <img class="layer mountain mid static" src="images/mountain-mid.png">
</div>

<div class="form-outer">
    <div class="form-container">
        <img src="images/Board.png" class="board-bg" alt="">
        <h2 class="form-title">Add New User</h2>
        <form action="AddUserServlet" method="post">
            <div class="form-group">
                <label>Email</label>
                <input type="text" name="email" placeholder="Enter email">
            </div>
            <div class="form-group">
                <label>Password</label>
                <input type="password" name="password" placeholder="Enter password">
            </div>
            <div class="form-group">
                <label>Role</label>
                <select name="role">
                    <option value="admin">Admin</option>
                    <option value="guest">Guest</option>
                </select>
            </div>
            <div class="form-actions">
                <button type="submit" class="save-btn">Save</button>
                <a href="admin.jsp" class="cancel-btn">Cancel</a>
            </div>
        </form>
    </div>
</div>

<footer><%= footer %></footer>

</body>
</html>
