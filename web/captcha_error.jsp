<%@ page session="false" %>
<%
    String header = application.getInitParameter("Header");
    String footer = application.getInitParameter("Footer");
%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Error - Too Many CAPTCHA Attempts</title>
            <link rel="stylesheet" type="text/css" href="css.css">
    </head>
    <body>
        <header>
            <%= header %>
        </header>
        <div id="background" style="pointer-events: none;">
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
                <h3>Too Many Failed Attempts</h3>
                <p>You have exceeded the maximum number of CAPTCHA attempts.</p>
                <p>Please close your browser and try again later.</p>
            </div>
        </div>
        <footer>
            <%= footer %>
        </footer>
    </body>
</html>