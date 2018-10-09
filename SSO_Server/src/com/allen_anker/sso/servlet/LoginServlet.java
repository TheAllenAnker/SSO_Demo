package com.allen_anker.sso.servlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

@WebServlet(name = "LoginServlet", urlPatterns = "/login")
public class LoginServlet extends HttpServlet {

    private String domains;

    @Override
    public void init() throws ServletException {
        super.init();
        domains = getInitParameter("domains");
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {
        if (Objects.equals("/login", request.getServletPath())) {
            String username = request.getParameter("username");
            String password = request.getParameter("password");

            String source = request.getParameter("source");

            if (source == null || Objects.equals("", source)) {
                String referer = request.getHeader("referer");
                source  = referer.substring(referer.indexOf("source=") + 7);
            }
            String ticket = UUID.randomUUID().toString().replace("-", "");
            if (Objects.equals(username, password)) {
                // domains cookies need to be set after redirecting to the target url
                response.sendRedirect(source + "/main?ticket=" + ticket + "&domains=" + domains.replace(source, "").trim());
            } else {
                request.setAttribute("source", source);
                // dispatch to login.jsp in current module
                request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
            }
        } else if (Objects.equals("/sso_login", request.getServletPath())) {
            // dispatch to login.jsp in current module
            request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
        }
    }
}
