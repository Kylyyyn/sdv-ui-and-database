package controller;

import java.io.IOException;
import java.sql.SQLException;
import javax.servlet.*;
import javax.servlet.http.*;
import model.User;
import model.UserDAO;

public class AddUserServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        String email    = request.getParameter("email");
        String password = request.getParameter("password");
        String role     = request.getParameter("role");

        if (email == null || email.trim().isEmpty()) {
            setErrorAndRedirect(session, response, "The Email field cannot be empty.");
            return;
        }
        if (password == null || password.trim().isEmpty()) {
            setErrorAndRedirect(session, response, "The Password field cannot be empty.");
            return;
        }
        if (role == null || role.trim().isEmpty()) {
            setErrorAndRedirect(session, response, "The Role field cannot be empty.");
            return;
        }

        email = email.trim();
        if (!email.matches("^[\\w._%+\\-]+@[\\w.\\-]+\\.[a-zA-Z]{2,}$")) {
            setErrorAndRedirect(session, response, "Please enter a valid email address.");
            return;
        }

        password = password.trim();
        role     = role.trim();
        role     = Character.toUpperCase(role.charAt(0)) + role.substring(1).toLowerCase();

        String key           = getServletContext().getInitParameter("key");
        String algo          = getServletContext().getInitParameter("cipher");
        String dbDriver      = getServletContext().getInitParameter("dbDriver");
        String dbURL         = getServletContext().getInitParameter("dbURL");
        String dbUser        = getServletContext().getInitParameter("dbUser");
        String dbPass        = getServletContext().getInitParameter("dbPass");

        String encryptedPassword = Encryption.encrypt(password, key, algo);
        User newUser = new User(email, encryptedPassword, role);

        UserDAO dao = new UserDAO(dbDriver, dbURL, dbUser, dbPass);
        try {
            dao.insert(newUser);
            if (session != null) {
                session.setAttribute("successMessage", "User '" + email + "' has been added successfully!");
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
