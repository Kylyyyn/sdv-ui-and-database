package controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import javax.servlet.*;
import javax.servlet.http.*;

public class CaptchaServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(true);

        if (session.getAttribute("captchaAttempts") == null) {
            session.setAttribute("captchaAttempts", 0);
        }

        response.sendRedirect("index.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        String secretKey = getServletContext().getInitParameter("recaptchaSecretKey");
        String recaptchaToken = request.getParameter("g-recaptcha-response");

        HttpSession session = request.getSession(true);

        int attempts = (session.getAttribute("captchaAttempts") != null)
                ? (int) session.getAttribute("captchaAttempts") : 0;

        if (attempts >= 3) {
            request.setAttribute("errorMessage",
                    "You have exceeded the maximum number of CAPTCHA attempts.");
            request.getRequestDispatcher("captcha_error.jsp")
                    .forward(request, response);
            return;
        }

        if (recaptchaToken == null || recaptchaToken.trim().isEmpty()) {
            attempts++;
            session.setAttribute("captchaAttempts", attempts);
            request.setAttribute("errorMessage",
                    "Please complete the CAPTCHA. You have "
                    + (3 - attempts) + " attempt(s) remaining.");
            request.getRequestDispatcher("captcha_failed.jsp")
                    .forward(request, response);
            return;
        }

        boolean captchaOk = verifyRecaptcha(secretKey, recaptchaToken);

        if (!captchaOk) {
            attempts++;
            session.setAttribute("captchaAttempts", attempts);

            if (attempts >= 3) {
                request.setAttribute("errorMessage",
                        "You have exceeded the maximum number of CAPTCHA attempts.");
                request.getRequestDispatcher("captcha_error.jsp")
                        .forward(request, response);
            } else {
                request.setAttribute("errorMessage",
                        "CAPTCHA verification failed. You have "
                        + (3 - attempts) + " attempt(s) remaining.");
                request.getRequestDispatcher("captcha_failed.jsp")
                        .forward(request, response);
            }
            return;
        }

        session.setAttribute("captchaAttempts", 0);
        session.setAttribute("captchaVerified", true);

        request.getRequestDispatcher("/LoginServlet").forward(request, response);
    }

    static boolean verifyRecaptcha(String secretKey, String token) {
        try {
            String params = "secret=" + URLEncoder.encode(secretKey, "UTF-8")
                    + "&response=" + URLEncoder.encode(token, "UTF-8");

            URL url = new URL("https://www.google.com/recaptcha/api/siteverify");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setDoOutput(true);
            con.setConnectTimeout(5000);
            con.setReadTimeout(5000);
            con.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");

            try (OutputStream os = con.getOutputStream()) {
                os.write(params.getBytes("UTF-8"));
            }

            StringBuilder sb = new StringBuilder();
            try (InputStream is = con.getInputStream()) {
                int ch;
                while ((ch = is.read()) != -1) {
                    sb.append((char) ch);
                }
            }
            String json = sb.toString();
            return json.contains("\"success\": true")
                    || json.contains("\"success\":true");

        } catch (Exception e) {
            return false;
        }
        
    }
}