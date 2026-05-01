<%@ page session="true" %>
<%
    String email = (String) session.getAttribute("email");
    String role = (String) session.getAttribute("role");
    if (email == null) {
        response.sendRedirect("error_session.jsp");
        return;
    }
    if (role.equalsIgnoreCase("admin")) {
        response.sendRedirect("admin.jsp");
    } else {
        response.sendRedirect("guest.jsp");
    }
%>