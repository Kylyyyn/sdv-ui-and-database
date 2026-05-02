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
<%
    String header = application.getInitParameter("Header");
    String footer = application.getInitParameter("Footer");
    String email = (String) session.getAttribute("email");
    String role = (String) session.getAttribute("role");
    String password = (String) session.getAttribute("password");
%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Guest Dashboard</title>
          <link rel="stylesheet" type="text/css" href="style.css">
    </head>
    <body>
        <header>
            <%= header%>
        </header>
        <div id="background">
            <img class="twilight" src="images/Twilight.png">
            <img class="layer farcloudA" src="images/farcloud-1.png">
            <img class="layer farcloudB" src="images/farcloud-2.png">
            <img class="layer farcloudC" src="images/farcloud-3.png">

            <!-- big cloud -->
            <img class="layer bigcloud" src="images/bigcloud.PNG">

            <!-- front clouds -->
            <img class="layer cloudA" src="images/cloud-1.png">
            <img class="layer cloudA a2" src="images/cloud-1.png">
            <img class="layer cloudB" src="images/cloud-2.png">
            <img class="layer cloudB b2" src="images/cloud-2.png">
            <img class="layer cloudC" src="images/cloud-3.png">

            <!-- mountains -->
            <img class="layer mountain far static" src="images/mountain-far.png">
            <img class="layer mountain mid static" src="images/mountain-mid.png">
            <div id="user-info">
                <h2>Welcome!</h2>
                <h3>Your Account Info</h3>
                <p>Email: <%= email%></p>
                <p>Password: <%= password%></p>
                <p>Role: <%= role%></p>
            </div>

            <div id="menuv2">
                <div id="button-wrapper2">
                        <button class="LogOut" id="LogOut" onclick="window.location.href='LogoutServlet'"></button>
                        <button class="action-btn" id="downloadReport"
                                onclick="window.location.href = 'ReportServlet?type=admin'">
                            <img src="images/Report.png"
                                 onmouseover="this.src = 'images/Report2.png'"
                                 onmouseout="this.src = 'images/Report.png'"
                                 alt="Download Report"
                                 onerror="this.style.display='none'; this.parentNode.innerHTML='? Report';">
                        </button>
                </div>
            </div>
        </div>
        <footer>
            <%= footer%>
        </footer>
    </body>
</html>