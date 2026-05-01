package controller;

import java.io.IOException;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;

public class DeleteUserServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {
        String dbURL = getServletContext().getInitParameter("dbURL");
        String dbUser = getServletContext().getInitParameter("dbUser");
        String dbPass = getServletContext().getInitParameter("dbPass");
        String dbDriver = getServletContext().getInitParameter("dbDriver");

        HttpSession session = request.getSession(false);

        String email = request.getParameter("email");

        if (email == null || email.trim().isEmpty()) {
            if (session != null) {
                session.setAttribute("errorMessage", "No Email was provided for deletion.");
            }
            response.sendRedirect("noCRUDCredentials.jsp");
            return;
        }

        email = email.trim();
        if (session != null) {
            String loggedInEmail = (String) session.getAttribute("email");

            if (loggedInEmail != null && loggedInEmail.equalsIgnoreCase(email)) {
                session.setAttribute("errorMessage", "You cannot delete your own account.");
                response.sendRedirect("admin.jsp");
                return;
            }
        }
        try {
            Class.forName(dbDriver);
        } catch (ClassNotFoundException e) {
            throw new ServletException("Driver not found: " + e.getMessage());
        }

        try (Connection con = DriverManager.getConnection(dbURL, dbUser, dbPass);
                PreparedStatement ps = con.prepareStatement(
                        "DELETE FROM USERS WHERE EMAIL=?")) {
            ps.setString(1, email);
            ps.executeUpdate();
            response.sendRedirect("admin.jsp");
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendRedirect("generic_error.jsp");
        }
    }
}
