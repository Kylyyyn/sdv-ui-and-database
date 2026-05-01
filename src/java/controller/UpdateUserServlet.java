package controller;
import java.io.IOException;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;
public class UpdateUserServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
                          throws ServletException, IOException {
        String dbURL    = getServletContext().getInitParameter("dbURL");
        String dbUser   = getServletContext().getInitParameter("dbUser");
        String dbPass   = getServletContext().getInitParameter("dbPass");
        String dbDriver = getServletContext().getInitParameter("dbDriver");

        HttpSession session = request.getSession(false);

        String originalEmail = request.getParameter("originalEmail");
        String email         = request.getParameter("email");
        String password      = request.getParameter("password");
        String role          = request.getParameter("role");

        if (originalEmail == null || originalEmail.trim().isEmpty()) {
            if (session != null) session.setAttribute("errorMessage", "The original Email is missing. Please try again.");
            response.sendRedirect("noCRUDCredentials.jsp");
            return;
        }
        if (email == null || email.trim().isEmpty()) {
            if (session != null) session.setAttribute("errorMessage", "The Email field cannot be empty.");
            response.sendRedirect("noCRUDCredentials.jsp");
            return;
        }
        if (password == null || password.trim().isEmpty()) {
            if (session != null) session.setAttribute("errorMessage", "The Password field cannot be empty.");
            response.sendRedirect("noCRUDCredentials.jsp");
            return;
        }
        if (role == null || role.trim().isEmpty()) {
            if (session != null) session.setAttribute("errorMessage", "The Role field cannot be empty.");
            response.sendRedirect("noCRUDCredentials.jsp");
            return;
        }

        originalEmail = originalEmail.trim();
        email         = email.trim();
        password      = password.trim();
        role          = role.trim();
        role          = Character.toUpperCase(role.charAt(0)) + role.substring(1).toLowerCase();

        try {
            Class.forName(dbDriver);
        } catch (ClassNotFoundException e) {
            throw new ServletException("Driver not found: " + e.getMessage());
        }

        try (Connection con = DriverManager.getConnection(dbURL, dbUser, dbPass);
             PreparedStatement ps = con.prepareStatement(
                "UPDATE USERS SET EMAIL=?, PASSWORD=?, USERROLE=? WHERE EMAIL=?")) {
            ps.setString(1, email);
            ps.setString(2, password);
            ps.setString(3, role);
            ps.setString(4, originalEmail);
            ps.executeUpdate();
            response.sendRedirect("admin.jsp");
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendRedirect("generic_error.jsp");
        }
    }
}