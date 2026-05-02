<%
    response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
    response.setHeader("Pragma", "no-cache");
    response.setDateHeader("Expires", 0);

    if (session.getAttribute("email") == null) {
        response.sendRedirect("error_session.jsp");
        return;
    }
%>
<%@ page session="true" %>
<%@ page import="java.sql.*" %>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>

<%
    String header = application.getInitParameter("Header");
    String footer = application.getInitParameter("Footer");
    String email = (String) session.getAttribute("email");
    String role = (String) session.getAttribute("role");
    if (email == null || role == null || !role.equalsIgnoreCase("admin")) {
        response.sendRedirect("error_session.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Admin Dashboard</title>
        <link rel="stylesheet" type="text/css" href="style.css">
    </head>

    <body class="admin-body">
        <div id="camera">

            <header><%= header%></header>
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

            <div class="admin-header">
                <h2 class="admin-title">Admin Dashboard</h2>
                <span class="welcome-msg">Welcome, <%= email%></span>
            </div>

            <div id="user-info">
                <!-- Users table -->
                <div class="table-wrapper">
                    <form method="get" class="search-bar">
                        <div class="input-row">
                            <input type="text" name="search"
                                   placeholder="Search email or role..."
                                   class="image-input"
                                   value="<%= request.getParameter("search") != null ? request.getParameter("search") : ""%>">

                            <button type="submit" class="img-btn ok-btn"></button>
                        </div>
                    </form>
                    <%
                        int pages = 1;
                        int recordsPerPage = 9;
                        if (request.getParameter("page") != null) {
                            pages = Integer.parseInt(request.getParameter("page"));
                        }
                        int start = (pages - 1) * recordsPerPage;
                        String search = request.getParameter("search");
                        if (search == null)
                            search = "";
                    %>
                    <table class="admin-table">
                        <thead>
                            <tr>
                                <th>Email</th>
                                <th>Password</th>
                                <th>Role</th>
                            </tr>
                        </thead>
                        <tbody>
                            <%
                                String dbURL = application.getInitParameter("dbURL");
                                String dbUser = application.getInitParameter("dbUser");
                                String dbPass = application.getInitParameter("dbPass");

                                boolean hasData = false;

                                try {
                                    Class.forName("org.apache.derby.jdbc.ClientDriver");
                                    Connection con = DriverManager.getConnection(dbURL, dbUser, dbPass);
                                    PreparedStatement stmt;

                                    String sql = "SELECT EMAIL, PASSWORD, USERROLE FROM USERS "
                                            + "WHERE LOWER(EMAIL) LIKE ? OR LOWER(USERROLE) LIKE ? "
                                            + "OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";

                                    stmt = con.prepareStatement(sql);
                                    stmt.setString(1, "%" + search.toLowerCase() + "%");
                                    stmt.setString(2, "%" + search.toLowerCase() + "%");
                                    stmt.setInt(3, start);
                                    stmt.setInt(4, recordsPerPage);

                                    ResultSet rs = stmt.executeQuery();
                                    while (rs.next()) {
                                        hasData = true;
                            %>
                            <tr data-email="<%= rs.getString("EMAIL")%>">
                                <td><%= rs.getString("EMAIL")%></td>
                                <td><%= rs.getString("PASSWORD")%></td>
                                <td><%= rs.getString("USERROLE")%></td>
                            </tr>
                            <%
                                }
                                if (!hasData) {
                            %>
                            <tr>
                                <td colspan="3">No users found.</td>
                            </tr>
                            <%
                                }
                                rs.close();
                                stmt.close();
                                con.close();
                            } catch (Exception e) {
                            %>
                            <tr>
                                <td colspan="3">Database Error: <%= e.getMessage()%></td>
                            </tr>
                            <%
                                }
                            %>
                        </tbody>
                    </table>
                    <div class="pagination">

                        <% if (pages > 1) {%>
                        <a href="?page=<%= pages - 1%>&search=<%= search%>" class="page-btn prev"></a>
                        <% } else { %>
                        <span class="page-btn prev disabled"></span>
                        <% }%>

                        <span class="page-info">Page <%= pages%></span>

                        <% if (hasData) {%>
                        <a href="?page=<%= pages + 1%>&search=<%= search%>" class="page-btn next"></a>
                        <% } else { %>
                        <span class="page-btn next disabled"></span>
                        <% }%>

                    </div>

                    <div class="admin-actions">
                        <button class="action-btn" id="addUser">
                            <img src="images/Add.png" onmouseover="this.src = 'images/Add2.png'" onmouseout="this.src = 'images/Add.png'" alt="Add User" onerror="this.style.display='none';this.parentNode.innerHTML='+ Add';">
                        </button>
                        <button class="action-btn" id="updateUser">
                            <img src="images/Update.png" onmouseover="this.src = 'images/Update2.png'" onmouseout="this.src = 'images/Update.png'" alt="Edit User" onerror="this.style.display='none';this.parentNode.innerHTML='✎ Update';">
                        </button>
                        <button class="action-btn" id="deleteUser">
                            <img src="images/Delete.png" onmouseover="this.src = 'images/Delete2.png'" onmouseout="this.src = 'images/Delete.png'" alt="Delete User" onerror="this.style.display='none';this.parentNode.innerHTML='✕ Delete';">
                        </button>
                        <button class="action-btn" id="LogOut" onclick="window.location.href = 'LogoutServlet'">
                            <img src="images/LogOut.png" onmouseover="this.src = 'images/LogOut2.png'" onmouseout="this.src = 'images/LogOut.png'" alt="Delete User" onerror="this.style.display='none';this.parentNode.innerHTML='Log Out';">
                        </button>
                        <button class="action-btn" id="downloadReport"
                                onclick="window.location.href = 'ReportServlet?type=admin'">
                            <img src="images/Report.png"
                                 onmouseover="this.src = 'images/Report2.png'"
                                 onmouseout="this.src = 'images/Report.png'"
                                 alt="Download Report"
                                 onerror="this.style.display='none'; this.parentNode.innerHTML='📄 Report';">
                        </button>
                    </div>
                    <script>
                        let selectedEmail = null;

                        // Row selection
                        document.querySelectorAll('.admin-table tbody tr').forEach(row => {
                            row.addEventListener('click', () => {
                                document.querySelectorAll('.admin-table tbody tr')
                                        .forEach(r => r.classList.remove('selected'));
                                row.classList.add('selected');
                                selectedEmail = row.dataset.email;
                            });
                        });

                        document.getElementById('addUser').addEventListener('click', () => {
                            window.location.href = 'addUser.jsp';
                        });

                        document.getElementById('updateUser').addEventListener('click', () => {
                            if (!selectedEmail) {
                                alert('Please select a user first.');
                                return;
                            }
                            window.location.href = 'updateUser.jsp?email=' + encodeURIComponent(selectedEmail);
                        });

                        document.getElementById('deleteUser').addEventListener('click', () => {
                            if (!selectedEmail) {
                                alert('Please select a user first.');
                                return;
                            }
                            let currentUser = "<%= email%>";
                            //admin cant delete own account
                            if (selectedEmail === currentUser) {
                                alert("You cannot delete your own account.");
                                return;
                            }
                            if (confirm('Are you sure you want to delete ' + selectedEmail + '?')) {
                                window.location.href = 'DeleteUserServlet?email=' + encodeURIComponent(selectedEmail);
                            }
                        });
                    </script>
                </div>
            </div>
            <footer><%= footer%></footer>
    </body>
</html>
