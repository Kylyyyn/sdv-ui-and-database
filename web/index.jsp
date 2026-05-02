
<%-- REMOVE BEFORE SUBMITTING --%>
<%
    boolean devMode = Boolean.parseBoolean(application.getInitParameter("devMode"));
%>
<%-- REMOVE BEFORE SUBMITTING --%>

<%@ page import="javax.servlet.ServletContext" %>
<%
    ServletContext ctx = application;
    String siteKey = ctx.getInitParameter("recaptchaSiteKey");
%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    String header = application.getInitParameter("Header");
    String footer = application.getInitParameter("Footer");
%>
<!DOCTYPE html>
<html>
    <head>
        <script src="https://www.google.com/recaptcha/api.js" async defer></script>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Stardew Valley Homepage</title>
          <link rel="stylesheet" type="text/css" href="style.css">
    </head>
    <body>
        <header>
            <%= header%>
        </header>
        <div id="camera">
            <div id="title-scene">
                <div id="background">
                    <!-- far clouds -->
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
                    <img class="layer mountain far" src="images/mountain-far.png">
                    <img class="layer mountain mid" src="images/mountain-mid.png">
                    <img class="layer mountain near" src="images/mountain-near.png">
                    <!-- foreground -->
                    <img class="layer greenery" src="images/greenery.png">
                    <img class="layer mist" src="images/mist.png">
                    <!-- title -->
                    <img src="images/Title.png" class="title">
                    <!-- buttons -->
                    <div id="menu">
                        <div id="button-wrapper">
                            <button class="LogIn"></button>
                        </div>
                    </div>
                    <div id="loginFormContainer" style="display:none;">
                        <form action="CaptchaServlet" method="post" id="loginForm">
                            <div class="input-row">
                                <input type="text" name="username" placeholder="Email" class="image-input">
                                <button type="submit" class="img-btn ok-btn"></button>
                            </div>
                            <div class="input-row">
                                <input type="password" name="password" placeholder="Password" class="image-input">
                                <button type="button" class="img-btn clear-btn" onclick="clearFields()"></button>
                            </div>
                            <!-- for recaptcha --> 
                            <%-- REPLACE BACK WITH: <div class="g-recaptcha" data-sitekey="<%= siteKey %>"></div> --%>
                            <%-- REMOVE BEFORE SUBMITTING --%>
                            <% if (!devMode) {%>
                            <div class="g-recaptcha" data-sitekey="<%= siteKey%>"></div>
                            <% }%>
                            <%-- REMOVE BEFORE SUBMITTING --%>

                        </form>
                    </div>
                </div>
            </div>
            <footer>
                <%= footer%>
            </footer>
        </div>
        <script>
            const LogIn = document.querySelector(".LogIn");
            const menu = document.getElementById("menu");
            const formContainer = document.getElementById("loginFormContainer");

            function showForm() {
                menu.style.display = "none";
                formContainer.style.display = "block";
            }
            LogIn.addEventListener("click", showForm);
            function clearFields() {
                document.querySelector('input[name="username"]').value = "";
                document.querySelector('input[name="password"]').value = "";
            }
        </script>
    </body>
</html>