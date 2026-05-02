<%@ page session="false" %>
<%
    String header = application.getInitParameter("Header");
    String footer = application.getInitParameter("Footer");
%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Uh Oh! Something's Wrong</title>
           <link rel="stylesheet" type="text/css" href="style.css">
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
                <h3>Uh Oh!</h3>
                <p>Something's wrong.</p>
                <p>Please try again later.</p>
            </div>
        </div>

        <div id="menu" style="top: 80%;">
            <div id="button-wrapper2" style="opacity: 1; animation: none;">
                <form action="index.jsp" method="get">
                    <button type="submit"
                        onmouseover="this.style.transform='scale(1.1)'"
                        onmouseout="this.style.transform='scale(1)'"
                        style="
                            font-family: 'StardewValley';
                            font-size: 18px;
                            color: #7b5b58;
                            background: url('images/textfield-bg.png') no-repeat center;
                            background-size: 100% 100%;
                            width: 180px;
                            height: 50px;
                            border: none;
                            cursor: pointer;
                            transition: transform 0.2s ease-in-out;
                        ">
                        Back
                    </button>
                </form>
            </div>
        </div>

        <footer>
            <%= footer %>
        </footer>
    </body>
</html>
