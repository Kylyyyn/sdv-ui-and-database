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

public class ReportServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("email") == null) {
            response.sendRedirect("error_session.jsp");
            return;
        }

        String sessionEmail = (String) session.getAttribute("email");
        String sessionRole  = (String) session.getAttribute("role");
        String type         = request.getParameter("type");

        if ("admin".equalsIgnoreCase(type) && !"admin".equalsIgnoreCase(sessionRole)) {
            response.sendRedirect("error_session.jsp");
            return;
        }

        String dbDriver = getServletContext().getInitParameter("dbDriver");
        String dbURL    = getServletContext().getInitParameter("dbURL");
        String dbUser   = getServletContext().getInitParameter("dbUser");
        String dbPass   = getServletContext().getInitParameter("dbPass");
        String key      = getServletContext().getInitParameter("key");
        String cipher   = getServletContext().getInitParameter("cipher");

        UserDAO dao = new UserDAO(dbDriver, dbURL, dbUser, dbPass);
        String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        try {
            byte[] pdfBytes;

            if ("admin".equalsIgnoreCase(type)) {
                List<User> users = dao.findAll();
                pdfBytes = PDFReportGenerator.generateAdminReport(users, sessionEmail, timestamp);

            } else {
                User user = dao.findByEmail(sessionEmail);
                if (user == null) {
                    response.sendRedirect("error_session.jsp");
                    return;
                }
                String decryptedPassword = Encryption.decrypt(user.getPassword(), key, cipher);
                pdfBytes = PDFReportGenerator.generateGuestReport(user, decryptedPassword, timestamp);
            }

            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition",
                    "attachment; filename=\"" + type + "_report.pdf\"");
            response.setContentLength(pdfBytes.length);
            response.getOutputStream().write(pdfBytes);

        } catch (Exception e) {
            throw new ServletException("Error generating PDF report", e);
        }
    }
}