package com.example.javaweb.config;

import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Druid 监控页面配置
 */
@Configuration
public class DruidConfig {

    /**
     * 配置 Druid StatViewServlet（监控页面）
     */
    @Bean
    public ServletRegistrationBean<StatViewServlet> statViewServlet() {
        ServletRegistrationBean<StatViewServlet> registrationBean = new ServletRegistrationBean<>(
                new StatViewServlet(),
                "/druid/*"
        );
        // 配置监控页面访问密码
        registrationBean.addInitParameter("loginUsername", "admin");
        registrationBean.addInitParameter("loginPassword", "admin123");
        // 禁用重置功能
        registrationBean.addInitParameter("resetEnable", "false");
        // 允许所有 IP 访问（生产环境请限制）
        registrationBean.addInitParameter("allow", "");
        // 启用 session 状态检查
        registrationBean.addInitParameter("sessionStatEnable", "true");
        return registrationBean;
    }

    /**
     * 配置 Druid WebStatFilter（监控统计）
     */
    @Bean
    public FilterRegistrationBean<WebStatFilter> webStatFilter() {
        FilterRegistrationBean<WebStatFilter> filterRegistrationBean = new FilterRegistrationBean<>(new WebStatFilter());
        // 拦截所有请求
        filterRegistrationBean.addUrlPatterns("/*");
        // 排除不需要监控的请求
        filterRegistrationBean.addInitParameter("exclusions", "*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid/*");
        // 监控 session 统计
        filterRegistrationBean.addInitParameter("profileEnable", "true");
        // 启用 session 统计
        filterRegistrationBean.addInitParameter("sessionStatEnable", "true");
        return filterRegistrationBean;
    }
}