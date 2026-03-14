package com.example.javaweb.common.log;

import com.alibaba.fastjson2.JSON;
import com.example.javaweb.entity.SysOperLog;
import com.example.javaweb.entity.SysUser;
import com.example.javaweb.service.SysOperLogService;
import com.example.javaweb.service.SysUserService;
import com.example.javaweb.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

@Slf4j
@Aspect
@Component
public class LogAspect {

    @Autowired
    private SysOperLogService sysOperLogService;

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private JwtUtil jwtUtil;

    @Pointcut("@annotation(com.example.javaweb.common.log.ApiLog)")
    public void logPointCut() {
    }

    @Around("logPointCut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        long beginTime = System.currentTimeMillis();
        Object result = null;
        Exception exception = null;

        try {
            result = point.proceed();
            return result;
        } catch (Exception e) {
            exception = e;
            throw e;
        } finally {
            long time = System.currentTimeMillis() - beginTime;
            saveLog(point, result, exception, time);
        }
    }

    private void saveLog(ProceedingJoinPoint joinPoint, Object result, Exception exception, long time) {
        try {
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Method method = signature.getMethod();
            ApiLog apiLog = method.getAnnotation(ApiLog.class);
            String description = apiLog != null ? apiLog.value() : "";

            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpServletRequest request = attributes != null ? attributes.getRequest() : null;

            SysOperLog operLog = new SysOperLog();
            operLog.setTitle(description);
            operLog.setBusinessType(getBusinessType(description));
            operLog.setMethod(joinPoint.getTarget().getClass().getName() + "." + signature.getName());
            operLog.setRequestMethod(request != null ? request.getMethod() : "");
            operLog.setOperatorType(1);
            operLog.setOperName(getCurrentUsername(request));
            operLog.setOperUrl(request != null ? request.getRequestURI() : "");
            operLog.setOperIp(request != null ? request.getRemoteAddr() : "");
            operLog.setOperLocation("未知");
            operLog.setOperParam(JSON.toJSONString(joinPoint.getArgs()));
            operLog.setJsonResult(result != null ? JSON.toJSONString(result) : "");
            operLog.setStatus(exception != null ? 1 : 0);
            operLog.setErrorMsg(exception != null ? exception.getMessage() : "");
            operLog.setCostTime(time);

            sysOperLogService.save(operLog);
        } catch (Exception e) {
            log.error("保存操作日志失败", e);
        }
    }

    private String getCurrentUsername(HttpServletRequest request) {
        try {
            String token = request.getHeader("Authorization");
            if (token != null && !token.isEmpty()) {
                String username = jwtUtil.getUsernameFromToken(token);
                if (username != null) {
                    return username;
                }
            }
        } catch (Exception e) {
            log.error("获取当前用户名失败", e);
        }
        return "未知用户";
    }

    private Integer getBusinessType(String description) {
        if (description.contains("新增") || description.contains("添加")) {
            return 1;
        } else if (description.contains("修改") || description.contains("更新")) {
            return 2;
        } else if (description.contains("删除")) {
            return 3;
        } else if (description.contains("授权")) {
            return 4;
        } else if (description.contains("导出")) {
            return 5;
        } else if (description.contains("导入")) {
            return 6;
        } else if (description.contains("强退")) {
            return 7;
        } else if (description.contains("生成代码")) {
            return 8;
        } else if (description.contains("清空")) {
            return 9;
        }
        return 0;
    }

}