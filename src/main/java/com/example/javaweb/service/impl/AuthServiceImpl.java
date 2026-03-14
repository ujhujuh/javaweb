package com.example.javaweb.service.impl;

import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.javaweb.common.exception.BusinessException;
import com.example.javaweb.dto.LoginDTO;
import com.example.javaweb.dto.RegisterDTO;
import com.example.javaweb.entity.SysLoginLog;
import com.example.javaweb.entity.SysUser;
import com.example.javaweb.entity.SysUserOnline;
import com.example.javaweb.mapper.SysLoginLogMapper;
import com.example.javaweb.mapper.SysUserMapper;
import com.example.javaweb.service.AuthService;
import com.example.javaweb.service.SysUserOnlineService;
import com.example.javaweb.util.JwtUtil;
import com.example.javaweb.util.IpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private SysLoginLogMapper sysLoginLogMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private SysUserOnlineService sysUserOnlineService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String login(LoginDTO loginDTO) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, loginDTO.getUsername());
        SysUser user = sysUserMapper.selectOne(wrapper);

        if (user == null) {
            saveLoginLog(loginDTO.getUsername(), "1", "用户不存在");
            throw new BusinessException("用户不存在");
        }

        if (!BCrypt.checkpw(loginDTO.getPassword(), user.getPassword())) {
            saveLoginLog(loginDTO.getUsername(), "1", "密码错误");
            throw new BusinessException("密码错误");
        }

        if (!"0".equals(user.getStatus())) {
            saveLoginLog(loginDTO.getUsername(), "1", "账号已被停用");
            throw new BusinessException("账号已被停用");
        }

        String token = jwtUtil.generateToken(user.getUsername());
        saveLoginLog(loginDTO.getUsername(), "0", "登录成功");

        // 创建在线用户记录
        saveUserOnline(user, token);

        return token;
    }

    @Override
    @Transactional(rollbackFor=Exception.class)
    public void register(RegisterDTO registerDTO) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, registerDTO.getUsername());
        if (sysUserMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("用户名已存在");
        }

        wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getEmail, registerDTO.getEmail());
        if (sysUserMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("邮箱已被注册");
        }

        SysUser user = new SysUser();
        user.setUsername(registerDTO.getUsername());
        user.setNickname(registerDTO.getNickname());
        user.setPassword(BCrypt.hashpw(registerDTO.getPassword()));
        user.setEmail(registerDTO.getEmail());
        user.setPhone(registerDTO.getPhone());
        user.setSex("0");
        user.setStatus("0");
        user.setDelFlag("0");

        sysUserMapper.insert(user);
    }

    @Override
    @Transactional(rollbackFor=Exception.class)
    public void logout() {
        saveLoginLog("unknown", "0", "退出登录");
        // 删除当前用户的在线记录（如果有 token）
        try {
            String token = request.getHeader("Authorization");
            if (token != null && !token.isEmpty()) {
                String username = jwtUtil.getUsernameFromToken(token);
                if (username != null) {
                    LambdaQueryWrapper<SysUser> userWrapper = new LambdaQueryWrapper<>();
                    userWrapper.eq(SysUser::getUsername, username);
                    SysUser user = sysUserMapper.selectOne(userWrapper);
                    if (user != null) {
                        sysUserOnlineService.lambdaUpdate()
                            .eq(SysUserOnline::getLoginName, username)
                            .set(SysUserOnline::getStatus, "off_line")
                            .update();
                    }
                }
            }
        } catch (Exception e) {
            // 登出时出错不影响主流程
        }
    }

    private void saveUserOnline(SysUser user, String token) {
        try {
            LambdaQueryWrapper<SysUserOnline> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SysUserOnline::getLoginName, user.getUsername());
            SysUserOnline existingOnline = sysUserOnlineService.getOne(wrapper);
            
            SysUserOnline userOnline = new SysUserOnline();
            userOnline.setSessionId(token);
            userOnline.setLoginName(user.getUsername());
            userOnline.setIpaddr(IpUtil.getIpAddr(request));
            userOnline.setLoginLocation(IpUtil.getRealAddressByIP(IpUtil.getIpAddr(request)));
            userOnline.setBrowser(IpUtil.getBrowser(request));
            userOnline.setOs(IpUtil.getOs(request));
            userOnline.setStatus("on_line");
            userOnline.setStartTimestamp(LocalDateTime.now());
            userOnline.setLastAccessTime(LocalDateTime.now());

            if (existingOnline != null) {
                // 更新现有记录
                sysUserOnlineService.updateById(userOnline);
            } else {
                // 创建新记录
                sysUserOnlineService.save(userOnline);
            }
        } catch (Exception e) {
            // 在线用户记录创建失败不影响登录流程
        }
    }

    private void saveLoginLog(String username, String status, String msg) {
        SysLoginLog loginLog = new SysLoginLog();
        loginLog.setUsername(username);
        loginLog.setIpaddr(IpUtil.getIpAddr(request));
        loginLog.setLoginLocation(IpUtil.getRealAddressByIP(IpUtil.getIpAddr(request)));
        loginLog.setBrowser(IpUtil.getBrowser(request));
        loginLog.setOs(IpUtil.getOs(request));
        loginLog.setStatus(status);
        loginLog.setMsg(msg);
        sysLoginLogMapper.insert(loginLog);
    }
}