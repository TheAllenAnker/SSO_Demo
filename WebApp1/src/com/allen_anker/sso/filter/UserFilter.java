package com.allen_anker.sso.filter;

import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

public class UserFilter implements Filter {

    private String server;
    private String app;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        server = filterConfig.getInitParameter("server");
        app = filterConfig.getInitParameter("app");
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        String ticket = null;
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        if (!Objects.equals(null, request.getCookies())) {
            for (Cookie cookie : request.getCookies()) {
                if (cookie.getName().equals("Ticket_Granting_Ticket")) {
                    ticket = cookie.getValue();
                    break;
                }
            }
        }

        if (!Objects.equals(ticket, null)) {
            // do some verification here, check if the user is illegal.
            filterChain.doFilter(servletRequest, servletResponse);
        }

        ticket = request.getParameter("ticket");
        if (!Objects.equals(null, ticket) && !Objects.equals("", ticket.trim())) {
            response.addCookie(new Cookie("Ticket_Granting_Ticket", ticket));
            filterChain.doFilter(servletRequest, servletResponse);
        } else {
            response.sendRedirect(server + "/sso_login?source=" + app);
        }
    }

    @Override
    public void destroy() {

    }
}
