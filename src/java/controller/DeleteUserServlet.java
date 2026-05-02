package controller;

import java.io.IOException;
import java.sql.SQLException;
import javax.servlet.*;
import javax.servlet.http.*;
import model.UserDAO;

public class DeleteUserServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

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

        String dbDriver = getServletContext().getInitParameter("dbDriver");
        String dbURL    = getServletContext().getInitParameter("dbURL");
        String dbUser   = getServletContext().getInitParameter("dbUser");
        String dbPass   = getServletContext().getInitParameter("dbPass");

        UserDAO dao = new UserDAO(dbDriver, dbURL, dbUser, dbPass);
        try {
            dao.delete(email);
            if (session != null) {
                session.setAttribute("successMessage", "User '" + email + "' has been deleted successfully!");
            }
            response.sendRedirect("admin.jsp");
        } catch (ClassNotFoundException e) {
            throw new ServletException("Driver not found: " + e.getMessage());
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendRedirect("generic_error.jsp");
        }
    }
}
