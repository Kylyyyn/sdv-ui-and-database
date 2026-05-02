package controller;

import java.io.*;
import java.sql.SQLException;
import java.util.logging.*;
import javax.servlet.*;
import javax.servlet.http.*;
import model.User;
import model.UserDAO;

public class LoginServlet extends HttpServlet {

    private static Logger logger;

    @Override
    public void init() throws ServletException {
        setupLogger();
    }

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        String dbDriver = getServletContext().getInitParameter("dbDriver");
        String dbURL    = getServletContext().getInitParameter("dbURL");
        String dbUser   = getServletContext().getInitParameter("dbUser");
        String dbPass   = getServletContext().getInitParameter("dbPass");

        String email    = request.getParameter("username");
        String password = request.getParameter("password");

        logger.info("[START] Login attempt initiated. Email: "
                + (email != null ? email : "(blank)"));

        try {
            HttpSession session = request.getSession(true);

            Integer captchaAttempts = (Integer) session.getAttribute("captchaAttempts");
            if (captchaAttempts == null) captchaAttempts = 0;

            Boolean captchaVerified = (Boolean) session.getAttribute("captchaVerified");
            if (captchaVerified == null || !captchaVerified) {
                captchaAttempts++;
                session.setAttribute("captchaAttempts", captchaAttempts);
                logger.warning("[CAPTCHA] Failed attempt #" + captchaAttempts);

                if (captchaAttempts >= 3) {
                    logger.warning("[CAPTCHA] Maximum attempts reached. Blocking user.");
                    session.invalidate();
                    request.setAttribute("errorMessage",
                            "Maximum CAPTCHA attempts reached. Access denied.");
                    request.getRequestDispatcher("captcha_error.jsp")
                            .forward(request, response);
                    return;
                }

                session.setAttribute("captchaVerified", false);
                request.setAttribute("errorMessage",
                        "Incorrect CAPTCHA. Attempt " + captchaAttempts + " of 3.");
                request.getRequestDispatcher("captcha_failed.jsp")
                        .forward(request, response);
                return;
            }

            session.setAttribute("captchaAttempts", 0);
            session.removeAttribute("captchaVerified");
            logger.info("[CAPTCHA] Session flag confirmed for: "
                    + (email != null ? email : "(blank)"));

            if ((email == null || email.trim().isEmpty())
                    && (password == null || password.trim().isEmpty())) {
                throw new NullValueException("Email and Password cannot be blank.");
            }
            if (email == null || email.trim().isEmpty()) {
                throw new AuthenticationException(
                        "Email is blank but password provided.", "ERROR_3");
            }

            // Delegate DB lookup to UserDAO 
            UserDAO dao = new UserDAO(dbDriver, dbURL, dbUser, dbPass);
            logger.info("[USERNAME LOOKUP] Searching DB for email: " + email.trim());

            User user;
            try {
                user = dao.findByEmail(email.trim());
            } catch (ClassNotFoundException e) {
                throw new ServletException("Driver not found: " + e.getMessage());
            }

            if (user == null) {
                logger.warning("[USERNAME LOOKUP] Email not found: " + email.trim());
                if (password == null || password.trim().isEmpty()) {
                    throw new AuthenticationException(
                            "Username not found and password blank.", "ERROR_1");
                } else {
                    throw new AuthenticationException(
                            "Both email and password incorrect.", "ERROR_4");
                }
            }

            logger.info("[USERNAME LOOKUP] Email found: " + email.trim());

            String key            = getServletContext().getInitParameter("key");
            String algo           = getServletContext().getInitParameter("cipher");
            String decryptedPass  = Encryption.decrypt(user.getPassword(), key, algo);

            logger.info("[PASSWORD VALIDATION] Comparing passwords for: " + email.trim());
            String inputPassword = password != null ? password.trim() : null;
            if (inputPassword == null || !decryptedPass.equals(inputPassword)) {
                logger.warning("[PASSWORD VALIDATION] FAILED for: " + email.trim());
                logger.info("[END] Login process ended — invalid password.");
                throw new AuthenticationException(
                        "Incorrect password for existing user.", "ERROR_2");
            }

            HttpSession oldSession = request.getSession(false);
            if (oldSession != null) oldSession.invalidate();

            HttpSession newSession = request.getSession(true);
            newSession.setAttribute("email",    email.trim());
            newSession.setAttribute("role",     user.getRole());
            newSession.setAttribute("password", user.getPassword());
            newSession.setMaxInactiveInterval(300);

            logger.info("[SUCCESSFUL LOGIN] User authenticated: "
                    + email.trim() + " | Role: " + user.getRole());
            logger.info("[END] Login process ended — success.");

            response.sendRedirect("success.jsp");

        } catch (NullValueException e) {
            logger.warning("[EXCEPTION] NullValueException: " + e.getMessage());
            logger.info("[END] Login process ended — null value.");
            request.setAttribute("errorMessage", e.getMessage());
            request.getRequestDispatcher("noLoginCredentials.jsp")
                    .forward(request, response);

        } catch (AuthenticationException e) {
            logger.warning("[FAILED LOGIN] AuthenticationException ["
                    + e.getErrorCode() + "]: " + e.getMessage());
            logger.info("[END] Login process ended — authentication error.");
            request.setAttribute("errorMessage", e.getMessage());
            switch (e.getErrorCode()) {
                case "ERROR_1":
                    request.getRequestDispatcher("error_1.jsp").forward(request, response); break;
                case "ERROR_2":
                    request.getRequestDispatcher("error_2.jsp").forward(request, response); break;
                case "ERROR_3":
                    request.getRequestDispatcher("error_3.jsp").forward(request, response); break;
                case "ERROR_4":
                    request.getRequestDispatcher("error_4.jsp").forward(request, response); break;
                default:
                    request.getRequestDispatcher("generic_error.jsp").forward(request, response);
            }

        } catch (SQLException e) {
            logger.severe("[EXCEPTION] SQLException: " + e.getMessage());
            logger.info("[END] Login process ended — DB error.");
            e.printStackTrace();
            request.setAttribute("errorMessage", "Database error: " + e.getMessage());
            request.getRequestDispatcher("generic_error.jsp").forward(request, response);

        } catch (Exception e) {
            logger.severe("[EXCEPTION] Unexpected error: " + e.getMessage());
            logger.info("[END] Login process ended — unexpected error.");
            e.printStackTrace();
            request.setAttribute("errorMessage", "Unexpected error: " + e.getMessage());
            request.getRequestDispatcher("generic_error.jsp").forward(request, response);
        }
    }

    private void setupLogger() {
        if (logger != null) return;
        logger = Logger.getLogger("LoginServlet");
        logger.setUseParentHandlers(false);
        try {
            String appRoot = getServletContext().getRealPath("/");
            String logDir  = appRoot + "log";
            java.io.File dir = new java.io.File(logDir);
            if (!dir.exists()) dir.mkdirs();
            String dateStr = new java.text.SimpleDateFormat("yyyyMMdd")
                    .format(new java.util.Date());
            String logFile = logDir + java.io.File.separator + "Log_" + dateStr + ".log";
            FileHandler fh = new FileHandler(logFile, true);
            fh.setFormatter(new java.util.logging.SimpleFormatter() {
                private static final String FMT = "[%1$tF %1$tT] [%2$s] %3$s%n";
                @Override
                public synchronized String format(LogRecord lr) {
                    return String.format(FMT,
                            new java.util.Date(lr.getMillis()),
                            lr.getLevel().getLocalizedName(),
                            lr.getMessage());
                }
            });
            logger.addHandler(fh);
            logger.setLevel(Level.ALL);
        } catch (Exception e) {
            logger.addHandler(new ConsoleHandler());
            logger.warning("Could not set up FileHandler: " + e.getMessage());
        }
    }
}