<%@ page session="false" %>
<%
    String header = application.getInitParameter("Header");
    String footer = application.getInitParameter("Footer");
%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Error - Email and Password are Incorrect</title>
           <link rel="stylesheet" type="text/css" href="css.css">
    </head>
    <body>
        <header>
            <%= header %>
        </header>
        <div id="background" class="no-pointer">
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
            <div id="user-info">
                <h3>User info not in Database</h3>
                <p>You entered an email and password that is not in our database.</p>
                <p>Please enter your correct login credentials and try again.</p>
            </div>
        </div>

        <div id="menu" class="menu-404">
            <div id="button-wrapper2" class="button-visible">
                <form action="index.jsp" method="get">
                    <button type="submit" class="back-btn">
                        Back
                    </button>
                </form>
            </div>
        </div>

        <footer>
            <%= footer%>
        </footer>
    </body>
</html>