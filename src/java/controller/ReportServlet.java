package controller;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import model.PDFReportGenerator;
import model.User;
import model.UserDAO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.logging.*;

public class ReportServlet extends HttpServlet {

    private static Logger logger;

    @Override
    public void init() throws ServletException {
        setupLogger();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("email") == null) {
            logger.warning("[SESSION] No active session — redirecting to error_session.jsp");
            response.sendRedirect("error_session.jsp");
            return;
        }

        String sessionEmail = (String) session.getAttribute("email");
        String sessionRole = (String) session.getAttribute("role");
        String type = request.getParameter("type");

        logger.info("[START] Report generation initiated. Type: " + type
                + " | Email: " + sessionEmail);

        if ("admin".equalsIgnoreCase(type) && !"admin".equalsIgnoreCase(sessionRole)) {
            logger.warning("[AUTH] Unauthorized admin report attempt by: " + sessionEmail);
            response.sendRedirect("error_session.jsp");
            return;
        }

        String dbDriver = getServletContext().getInitParameter("dbDriver");
        String dbURL = getServletContext().getInitParameter("dbURL");
        String dbUser = getServletContext().getInitParameter("dbUser");
        String dbPass = getServletContext().getInitParameter("dbPass");
        String key = getServletContext().getInitParameter("key");
        String cipher = getServletContext().getInitParameter("cipher");

        UserDAO dao = new UserDAO(dbDriver, dbURL, dbUser, dbPass);
        String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        try {
            byte[] pdfBytes;

            if ("admin".equalsIgnoreCase(type)) {
                logger.info("[REPORT] Fetching all users for admin report. Requested by: "
                        + sessionEmail);
                List<User> users = dao.findAll();
                logger.info("[REPORT] Retrieved " + users.size() + " users. Generating admin PDF...");
                pdfBytes = PDFReportGenerator.generateAdminReport(users, sessionEmail, timestamp);
                logger.info("[REPORT] Admin PDF generated successfully for: " + sessionEmail);

            } else {
                logger.info("[REPORT] Fetching user data for guest report. Email: " + sessionEmail);
                User user = dao.findByEmail(sessionEmail);
                if (user == null) {
                    logger.warning("[REPORT] User not found in DB: " + sessionEmail
                            + " — redirecting to error_session.jsp");
                    response.sendRedirect("error_session.jsp");
                    return;
                }
                String decryptedPassword = Encryption.decrypt(user.getPassword(), key, cipher);
                logger.info("[REPORT] Generating guest PDF for: " + sessionEmail);
                pdfBytes = PDFReportGenerator.generateGuestReport(user, decryptedPassword, timestamp);
                logger.info("[REPORT] Guest PDF generated successfully for: " + sessionEmail);
            }

            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition",
                    "attachment; filename=\"" + type + "_report.pdf\"");
            response.setContentLength(pdfBytes.length);
            response.getOutputStream().write(pdfBytes);

            logger.info("[END] Report process ended — PDF sent successfully. Type: " + type
                    + " | Email: " + sessionEmail);

        } catch (Exception e) {
            logger.severe("[EXCEPTION] Error generating PDF report for: " + sessionEmail
                    + " | " + e.getMessage());
            logger.info("[END] Report process ended — error.");
            throw new ServletException("Error generating PDF report", e);
        }
    }

    private void setupLogger() {
        if (logger != null) {
            return;
        }
        logger = Logger.getLogger("AppLogger");
        logger.setUseParentHandlers(false);
        try {
            String appRoot = getServletContext().getRealPath("/");
            String logDir = appRoot + "log";
            java.io.File dir = new java.io.File(logDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }
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
