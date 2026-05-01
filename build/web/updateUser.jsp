<%@ page session="true" %>
<%@ page import="java.sql.*" %>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>

<%
    String header = application.getInitParameter("Header");
    String footer = application.getInitParameter("Footer");

    String sessionEmail = (String) session.getAttribute("email");
    String role         = (String) session.getAttribute("role");

    if (sessionEmail == null || role == null || !role.equalsIgnoreCase("admin")) {
        response.sendRedirect("error_session.jsp");
        return;
    }

    String editEmail = request.getParameter("email");

    String dbURL  = application.getInitParameter("dbURL");
    String dbUser = application.getInitParameter("dbUser");
    String dbPass = application.getInitParameter("dbPass");

    String userEmail = "";
    String userPass  = "";
    String userRole  = "";

    try {
        Class.forName("org.apache.derby.jdbc.ClientDriver");
        Connection con = DriverManager.getConnection(dbURL, dbUser, dbPass);
        PreparedStatement ps = con.prepareStatement(
            "SELECT EMAIL, PASSWORD, USERROLE FROM USERS WHERE EMAIL = ?");
        ps.setString(1, editEmail);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            userEmail = rs.getString("EMAIL");
            userPass  = rs.getString("PASSWORD");
            userRole  = rs.getString("USERROLE");
        }
        rs.close();
        ps.close();
        con.close();
    } catch (Exception e) {
        out.println("Database error: " + e.getMessage());
        return;
    }
%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Edit User</title>
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
        <h2 class="form-title">Edit User</h2>
        <form action="UpdateUserServlet" method="post">
            <input type="hidden" name="originalEmail" value="<%= userEmail %>">
            <div class="form-group">
                <label>Email</label>
                <input type="text" name="email" value="<%= userEmail %>">
            </div>
            <div class="form-group">
                <label>Password</label>
                <input type="password" name="password" value="<%= userPass %>">
            </div>
            <div class="form-group">
                <label>Role</label>
                <select name="role">
                    <option value="admin"  <%= userRole.equalsIgnoreCase("admin")  ? "selected" : "" %>>Admin</option>
                    <option value="guest"  <%= userRole.equalsIgnoreCase("guest")  ? "selected" : "" %>>Guest</option>
                </select>
            </div>
            <div class="form-actions">
                <button type="submit" class="update-btn">Update</button>
                <a href="admin.jsp" class="cancel-btn">Cancel</a>
            </div>
        </form>
    </div>
</div>

<footer><%= footer %></footer>

</body>
</html>
