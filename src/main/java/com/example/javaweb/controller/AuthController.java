package com.example.javaweb.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.javaweb.common.log.ApiLog;
import com.example.javaweb.common.result.Result;
import com.example.javaweb.dto.LoginDTO;
import com.example.javaweb.dto.RegisterDTO;
import com.example.javaweb.entity.SysUser;
import com.example.javaweb.mapper.SysUserMapper;
import com.example.javaweb.service.AuthService;
import com.example.javaweb.util.JwtUtil;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @ApiLog("用户登录")
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@Validated @RequestBody LoginDTO loginDTO) {
        String token = authService.login(loginDTO);
        SysUser user = sysUserMapper.selectOne(
            new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, loginDTO.getUsername())
        );
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("user", user);
        return Result.success(data);
    }

    @ApiLog("用户注册")
    @PostMapping("/register")
    public Result<Void> register(@Validated @RequestBody RegisterDTO registerDTO) {
        authService.register(registerDTO);
        return Result.success();
    }

    @ApiLog("用户退出")
    @RequiresAuthentication
    @PostMapping("/logout")
    public Result<Void> logout() {
        authService.logout();
        return Result.success();
    }

    @ApiLog("获取当前用户信息")
    @GetMapping("/userInfo")
    public Result<SysUser> getUserInfo(@RequestHeader("Authorization") String token) {
        if (token == null || token.isEmpty()) {
            return Result.failed(401, "未登录");
        }
        String username = jwtUtil.getUsernameFromToken(token);
        if (username == null) {
            return Result.failed(401, "token无效");
        }
        SysUser user = sysUserMapper.selectOne(
            new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, username)
        );
        return Result.success(user);
    }

    @GetMapping("/unauthorized")
    public Result<Void> unauthorized() {
        return Result.failed(401, "未登录或token已过期");
    }
}