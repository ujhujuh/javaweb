package com.example.javaweb.interceptor;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.javaweb.common.context.CurrentUserContext;
import com.example.javaweb.entity.SysUser;
import com.example.javaweb.mapper.SysUserMapper;
import com.example.javaweb.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class CurrentUserInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private SysUserMapper sysUserMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        CurrentUserContext.clear();

        // 解析 Accept-Language 请求头
        String acceptLanguage = request.getHeader("Accept-Language");
        if (acceptLanguage != null && !acceptLanguage.trim().isEmpty()) {
            // 提取语言代码（例如：en-US -> en, zh-CN -> zh）
            String language = acceptLanguage.trim().toLowerCase();
            if (language.startsWith("en")) {
                CurrentUserContext.setLanguage("en");
            } else {
                CurrentUserContext.setLanguage("zh");
            }
        }

        String token = request.getHeader("Authorization");
        if (token == null || token.trim().isEmpty()) {
            return true;
        }

        String normalizedToken = normalizeToken(token);
        if (normalizedToken.isEmpty()) {
            return true;
        }

        String username = jwtUtil.getUsernameFromToken(normalizedToken);
        if (username == null || username.trim().isEmpty()) {
            return true;
        }

        SysUser user = sysUserMapper.selectOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, username));
        if (user != null) {
            CurrentUserContext.setUser(user);
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        CurrentUserContext.clear();
    }

    private String normalizeToken(String token) {
        String value = token.trim();
        if (value.regionMatches(true, 0, "Bearer ", 0, 7)) {
            return value.substring(7).trim();
        }
        return value;
    }
}
