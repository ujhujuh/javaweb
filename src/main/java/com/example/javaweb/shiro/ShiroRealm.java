package com.example.javaweb.shiro;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.javaweb.common.exception.BusinessException;
import com.example.javaweb.entity.SysMenu;
import com.example.javaweb.entity.SysRole;
import com.example.javaweb.entity.SysUser;
import com.example.javaweb.mapper.SysUserMapper;
import com.example.javaweb.util.JwtUtil;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class ShiroRealm extends AuthorizingRealm {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof JwtToken;
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        SysUser user = (SysUser) principals.getPrimaryPrincipal();
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();

        List<SysRole> roles = sysUserMapper.selectRolesByUserId(user.getId());
        Set<String> roleKeys = new HashSet<>();
        boolean isAdmin = "admin".equalsIgnoreCase(user.getUsername());
        for (SysRole role : roles) {
            roleKeys.add(role.getRoleKey());
            if ("admin".equalsIgnoreCase(role.getRoleKey())) {
                isAdmin = true;
            }
        }
        info.setRoles(roleKeys);

        if (isAdmin) {
            // 超级管理员兜底权限，避免角色菜单关系异常导致误拦截
            info.addStringPermission("*:*:*");
            info.addStringPermission("*");
            return info;
        }

        List<SysMenu> menus = sysUserMapper.selectMenusByUserId(user.getId());
        Set<String> perms = new HashSet<>();
        for (SysMenu menu : menus) {
            if (menu.getPerms() != null && !menu.getPerms().isEmpty()) {
                perms.add(menu.getPerms());
            }
        }
        info.setStringPermissions(perms);

        return info;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        JwtToken jwtToken = (JwtToken) token;
        String tokenStr = (String) jwtToken.getPrincipal();

        String username = jwtUtil.getUsernameFromToken(tokenStr);
        if (username == null) {
            throw new BusinessException("token无效");
        }

        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, username);
        SysUser user = sysUserMapper.selectOne(wrapper);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        if (!"0".equals(user.getStatus())) {
            throw new BusinessException("账号已被停用");
        }

        if (!jwtUtil.validateToken(tokenStr, username)) {
            throw new BusinessException("token已过期");
        }

        return new SimpleAuthenticationInfo(user, tokenStr, getName());
    }
}
