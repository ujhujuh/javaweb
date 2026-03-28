package com.example.javaweb.shiro;

import com.alibaba.fastjson2.JSON;
import com.example.javaweb.common.result.Result;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.web.filter.authc.BasicHttpAuthenticationFilter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class JwtFilter extends BasicHttpAuthenticationFilter {

    private static final List<String> PATHS_TO_SKIP = Arrays.asList(
            "/doc.html",
            "/webjars",
            "/swagger-resources",
            "/v2/api-docs",
            "/v3/api-docs",
            "/swagger-ui",
            "/swagger-ui.html",
            "/favicon.ico",
            "/api/auth/login",
            "/api/auth/register",
            "/api/auth/unauthorized",
            "/api/auth/userInfo",
            "/api/system/notice/latest",
            "/api/portal/news/home",
            "/api/portal/news/categories",
            "/api/portal/news/list",
            "/api/portal/news/detail",
            "/auth/login",
            "/auth/register",
            "/auth/unauthorized",
            "/druid",
            "/api/userprofile/software",
            "/api/userprofile/file",
            "/api/userprofile/browser"
    );

    @Override
    protected boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        res.setHeader("Access-Control-Allow-Origin", req.getHeader("Origin"));
        res.setHeader("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS");
        res.setHeader("Access-Control-Allow-Headers", "Content-Type,Authorization");
        res.setHeader("Access-Control-Allow-Credentials", "true");

        if ("OPTIONS".equals(req.getMethod())) {
            res.setStatus(HttpServletResponse.SC_OK);
            return false;
        }

        String requestURI = req.getRequestURI();
        if ("GET".equalsIgnoreCase(req.getMethod()) && requestURI.matches("^/api/portal/news/\\d+/comments$")) {
            return true;
        }
        for (String path : PATHS_TO_SKIP) {
            if (requestURI.equals(path) || requestURI.startsWith(path + "/") || requestURI.startsWith(path + ".") || (path.equals("/druid") && requestURI.startsWith("/druid"))) {
                return true;
            }
        }

        return super.preHandle(request, response);
    }

    @Override
    protected boolean isLoginAttempt(ServletRequest request, ServletResponse response) {
        HttpServletRequest req = (HttpServletRequest) request;
        String token = req.getHeader("Authorization");
        return token != null;
    }

    @Override
    protected boolean executeLogin(ServletRequest request, ServletResponse response) throws AuthenticationException {
        HttpServletRequest req = (HttpServletRequest) request;
        String token = req.getHeader("Authorization");
        JwtToken jwtToken = new JwtToken(token);
        getSubject(request, response).login(jwtToken);
        return true;
    }

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws IOException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        if (!isLoginAttempt(request, response)) {
            res.setContentType("application/json;charset=utf-8");
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            res.getWriter().write(JSON.toJSONString(Result.failed(401, "未登录")));
            return false;
        }

        return executeLogin(request, response);
    }
}
