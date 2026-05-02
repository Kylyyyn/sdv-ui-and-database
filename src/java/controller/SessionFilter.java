package controller;

import java.io.IOException;
import javax.servlet.*;
import javax.servlet.http.*;

public class SessionFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpReq = (HttpServletRequest) request;
        HttpServletResponse httpRes = (HttpServletResponse) response;

        HttpSession session = httpReq.getSession(false);
        String path = httpReq.getServletPath();

        boolean isProtected = path.equals("/admin.jsp")
                           || path.equals("/guest.jsp")
                           || path.equals("/addUser.jsp")
                           || path.equals("/updateUser.jsp")
                           || path.startsWith("/ReportServlet");

        if (isProtected) {
            try {
                if (session == null || session.getAttribute("email") == null) {
                    throw new InvalidSessionException("No valid session found.");
                }
                chain.doFilter(request, response);
            } catch (InvalidSessionException e) {
                httpRes.sendRedirect("error_session.jsp");
            }
        } else {
            chain.doFilter(request, response);
        }
    }

    @Override public void init(FilterConfig fc) {}
    @Override public void destroy() {}
}