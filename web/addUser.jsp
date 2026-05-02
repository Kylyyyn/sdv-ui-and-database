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
        <h2 class="form-title">Add New User</h2>

        <form id="addUserForm" action="AddUserServlet" method="post"
              onsubmit="return validateAddUser(event)">

            <div class="form-group">
                <label for="email">Email</label>
                <input type="email" id="email" name="email"
                       placeholder="Enter email"
                       maxlength="100"
                       autocomplete="off">
                <span class="field-error" id="emailError"></span>
            </div>

            <div class="form-group">
                <label for="password">Password</label>
                <input type="password" id="password" name="password"
                       placeholder="Enter password"
                       maxlength="64"
                       autocomplete="new-password">
                <span class="field-error" id="passwordError"></span>
            </div>

            <div class="form-group">
                <label for="role">Role</label>
                <select id="role" name="role">
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

<script>
    function validateAddUser(e) {
        let valid = true;

        document.getElementById("emailError").textContent    = "";
        document.getElementById("passwordError").textContent = "";

        const emailVal    = document.getElementById("email").value.trim();
        const passwordVal = document.getElementById("password").value.trim();

        if (emailVal === "") {
            document.getElementById("emailError").textContent = "Email is required.";
            valid = false;
        } else if (!/^[\w._%+\-]+@[\w.\-]+\.[a-zA-Z]{2,}$/.test(emailVal)) {
            document.getElementById("emailError").textContent =
                "Please enter a valid email address.";
            valid = false;
        }

        if (passwordVal === "") {
            document.getElementById("passwordError").textContent = "Password is required.";
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
