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

    String dbURL    = application.getInitParameter("dbURL");
    String dbUser   = application.getInitParameter("dbUser");
    String dbPass   = application.getInitParameter("dbPass");
    String dbDriver = application.getInitParameter("dbDriver");

    String userEmail = "";
    String userRole  = "";

    try {
        Class.forName(dbDriver);
        Connection con = DriverManager.getConnection(dbURL, dbUser, dbPass);
        PreparedStatement ps = con.prepareStatement(
            "SELECT EMAIL, USERROLE FROM USERS WHERE EMAIL = ?");
        ps.setString(1, editEmail);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            userEmail = rs.getString("EMAIL");
            userRole  = rs.getString("USERROLE");
        }
        rs.close(); ps.close(); con.close();
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
    <link rel="stylesheet" type="text/css" href="style.css">
</head>

<body class="admin-body">

<header><%= header %></header>

<div id="background">
    <img class="twilight"           src="images/Twilight.png">
    <img class="layer farcloudA"    src="images/farcloud-1.png">
    <img class="layer farcloudB"    src="images/farcloud-2.png">
    <img class="layer farcloudC"    src="images/farcloud-3.png">
    <img class="layer bigcloud"     src="images/bigcloud.PNG">
    <img class="layer cloudA"       src="images/cloud-1.png">
    <img class="layer cloudA a2"    src="images/cloud-1.png">
    <img class="layer cloudB"       src="images/cloud-2.png">
    <img class="layer cloudB b2"    src="images/cloud-2.png">
    <img class="layer cloudC"       src="images/cloud-3.png">
    <img class="layer mountain far static" src="images/mountain-far.png">
    <img class="layer mountain mid static" src="images/mountain-mid.png">
</div>

<div class="form-outer">
    <div class="form-container">
        <img src="images/Board.png" class="board-bg" alt="">
        <h2 class="form-title">Edit User</h2>

        <form id="updateUserForm" action="UpdateUserServlet" method="post"
              onsubmit="return validateUpdateUser(event)">

            <%-- originalEmail is the immutable identifier sent to the servlet --%>
            <input type="hidden" name="originalEmail" value="<%= userEmail %>">

            <div class="form-group">
                <label for="email">Email</label>
                <%--
                    The email field is DISABLED so the browser does NOT submit it.
                    This prevents the username from being changed and eliminates
                    any SQLi vector through this field.
                    The hidden originalEmail field above is used by the servlet instead.
                --%>
                <input type="email" id="email"
                       value="<%= userEmail %>"
                       disabled
                       title="Username cannot be changed.">
                <small class="field-hint">Username cannot be edited.</small>
            </div>

            <div class="form-group">
                <label for="password">New Password</label>
                <input type="password" id="password" name="password"
                       placeholder="Enter new password"
                       maxlength="64"
                       autocomplete="new-password">
                <span class="field-error" id="passwordError"></span>
            </div>

            <div class="form-group">
                <label for="role">Role</label>
                <select id="role" name="role">
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

<script>
    function validateUpdateUser(e) {
        let valid = true;

        document.getElementById("passwordError").textContent = "";

        const passwordVal = document.getElementById("password").value.trim();

        if (passwordVal === "") {
            document.getElementById("passwordError").textContent =
                "Password is required.";
            valid = false;
        } else if (passwordVal.length < 6) {
            document.getElementById("passwordError").textContent =
                "Password must be at least 6 characters.";
            valid = false;
        }

        if (!valid) e.preventDefault();
        return valid;
    }
</script>

</body>
</html>
