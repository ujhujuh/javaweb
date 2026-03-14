package com.example.javaweb.service.impl;

import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.javaweb.entity.SysUser;
import com.example.javaweb.mapper.SysUserMapper;
import com.example.javaweb.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Override
    public IPage<SysUser> selectUserList(IPage<SysUser> page, SysUser user) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(user.getUsername()), SysUser::getUsername, user.getUsername())
                .like(StringUtils.hasText(user.getNickname()), SysUser::getNickname, user.getNickname())
                .like(StringUtils.hasText(user.getPhone()), SysUser::getPhone, user.getPhone())
                .eq(StringUtils.hasText(user.getStatus()), SysUser::getStatus, user.getStatus())
                .orderByAsc(SysUser::getId);
        return sysUserMapper.selectPage(page, wrapper);
    }

    @Override
    public SysUser selectUserById(Long userId) {
        SysUser user = sysUserMapper.selectById(userId);
        if (user != null) {
            List<Long> roleIds = sysUserMapper.selectRoleIdsByUserId(userId);
            user.setRoleIds(roleIds);
        }
        return user;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean insertUser(SysUser user) {
        if (!StringUtils.hasText(user.getPassword())) {
            user.setPassword(BCrypt.hashpw("123456"));
        } else {
            user.setPassword(BCrypt.hashpw(user.getPassword()));
        }
        return sysUserMapper.insert(user) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateUser(SysUser user) {
        return sysUserMapper.updateById(user) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteUserByIds(List<Long> userIds) {
        for (Long userId : userIds) {
            if (userId.equals(1L)) {
                throw new RuntimeException("不能删除超级管理员");
            }
        }
        return sysUserMapper.deleteBatchIds(userIds) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean resetUserPwd(Long userId, String password) {
        SysUser user = new SysUser();
        user.setId(userId);
        user.setPassword(BCrypt.hashpw(password));
        return sysUserMapper.updateById(user) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateUserStatus(SysUser user) {
        if (user.getId().equals(1L)) {
            throw new RuntimeException("不能停用超级管理员");
        }
        return sysUserMapper.updateById(user) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean authRole(Long userId, List<Long> roleIds) {
        sysUserMapper.deleteUserRoleByUserId(userId);
        if (roleIds != null && !roleIds.isEmpty()) {
            sysUserMapper.insertUserRole(userId, roleIds);
        }
        return true;
    }

    @Override
    public List<SysUser> selectUsersByRoleIds(List<Long> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) {
            return new ArrayList<>();
        }
        return sysUserMapper.selectUsersByRoleIds(roleIds);
    }

    @Override
    public List<SysUser> exportUserList(SysUser user) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(user.getUsername()), SysUser::getUsername, user.getUsername())
                .like(StringUtils.hasText(user.getNickname()), SysUser::getNickname, user.getNickname())
                .like(StringUtils.hasText(user.getPhone()), SysUser::getPhone, user.getPhone())
                .eq(StringUtils.hasText(user.getStatus()), SysUser::getStatus, user.getStatus())
                .orderByAsc(SysUser::getId);
        return sysUserMapper.selectList(wrapper);
    }
}