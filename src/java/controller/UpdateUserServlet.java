package controller;

import java.io.IOException;
import java.sql.SQLException;
import javax.servlet.*;
import javax.servlet.http.*;
import model.UserDAO;

public class UpdateUserServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        String originalEmail = request.getParameter("originalEmail");
        String password      = request.getParameter("password");
        String role          = request.getParameter("role");

        if (originalEmail == null || originalEmail.trim().isEmpty()) {
            setErrorAndRedirect(session, response,
                    "The original Email is missing. Please try again.");
            return;
        }
        if (password == null || password.trim().isEmpty()) {
            setErrorAndRedirect(session, response,
                    "The Password field cannot be empty.");
            return;
        }
        if (role == null || role.trim().isEmpty()) {
            setErrorAndRedirect(session, response,
                    "The Role field cannot be empty.");
            return;
        }

        originalEmail = originalEmail.trim();
        password      = password.trim();
        role          = role.trim();
        role          = Character.toUpperCase(role.charAt(0)) + role.substring(1).toLowerCase();

        String key      = getServletContext().getInitParameter("key");
        String algo     = getServletContext().getInitParameter("cipher");
        String dbDriver = getServletContext().getInitParameter("dbDriver");
        String dbURL    = getServletContext().getInitParameter("dbURL");
        String dbUser   = getServletContext().getInitParameter("dbUser");
        String dbPass   = getServletContext().getInitParameter("dbPass");

        String encryptedPassword = Encryption.encrypt(password, key, algo);

        UserDAO dao = new UserDAO(dbDriver, dbURL, dbUser, dbPass);
        try {
            dao.update(originalEmail, encryptedPassword, role);
            if (session != null) {
                session.setAttribute("successMessage", "User '" + originalEmail + "' has been updated successfully!");
            }
            response.sendRedirect("admin.jsp");
        } catch (ClassNotFoundException e) {
            throw new ServletException("Driver not found: " + e.getMessage());
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendRedirect("generic_error.jsp");
        }
    }

    private void setErrorAndRedirect(HttpSession session,
                                     HttpServletResponse response,
                                     String message) throws IOException {
        if (session != null) {
            session.setAttribute("errorMessage", message);
        }
        response.sendRedirect("noCRUDCredentials.jsp");
    }
}
