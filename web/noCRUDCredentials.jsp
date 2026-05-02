<%@ page session="true" %>
<%
    String header = application.getInitParameter("Header");
    String footer = application.getInitParameter("Footer");

    String errorMessage = (String) session.getAttribute("errorMessage");
    if (errorMessage != null) {
        session.removeAttribute("errorMessage");
    } else {
        errorMessage = "A required field was left blank.";
    }
%>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Error - Missing Fields</title>
      <link rel="stylesheet" type="text/css" href="css.css">
</head>
<body>
    <header><%= header %></header>

    <div id="background" style="pointer-events: none;">
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

        <div id="user-info">
            <h3>Missing Required Field</h3>
            <p><%= errorMessage %></p>
            <p>Please go back and fill in all required fields.</p>
        </div>
    </div>

    <div id="menu">
        <div id="button-wrapper2">
            <form action="admin.jsp" method="get">
                <button type="submit">Back</button>
            </form>
        </div>
    </div>

    <footer><%= footer %></footer>
</body>
</html>
