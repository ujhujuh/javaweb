package com.example.javaweb.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.javaweb.entity.SysRole;
import com.example.javaweb.entity.SysUser;
import com.example.javaweb.mapper.SysRoleMapper;
import com.example.javaweb.mapper.SysUserMapper;
import com.example.javaweb.service.SysRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {

    @Autowired
    private SysRoleMapper sysRoleMapper;

    @Autowired
    private SysUserMapper sysUserMapper;

    @Override
    public IPage<SysRole> selectRoleList(IPage<SysRole> page, SysRole role) {
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(role.getRoleName()), SysRole::getRoleName, role.getRoleName())
                .like(StringUtils.hasText(role.getRoleKey()), SysRole::getRoleKey, role.getRoleKey())
                .eq(StringUtils.hasText(role.getStatus()), SysRole::getStatus, role.getStatus())
                .orderByAsc(SysRole::getRoleSort)
                .orderByAsc(SysRole::getId);
        return sysRoleMapper.selectPage(page, wrapper);
    }

    @Override
    public SysRole selectRoleById(Long roleId) {
        return sysRoleMapper.selectById(roleId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean insertRole(SysRole role) {
        return sysRoleMapper.insert(role) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateRole(SysRole role) {
        return sysRoleMapper.updateById(role) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteRoleByIds(List<Long> roleIds) {
        for (Long roleId : roleIds) {
            if (roleId.equals(1L)) {
                throw new RuntimeException("不能删除超级管理员角色");
            }
        }
        return sysRoleMapper.deleteBatchIds(roleIds) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean authMenu(Long roleId, List<Long> menuIds) {
        sysRoleMapper.deleteRoleMenuByRoleId(roleId);
        if (menuIds != null && !menuIds.isEmpty()) {
            sysRoleMapper.insertRoleMenu(roleId, menuIds);
        }
        return true;
    }

    @Override
    public List<Long> selectMenuIdsByRoleId(Long roleId) {
        return sysRoleMapper.selectMenuIdsByRoleId(roleId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean authUser(Long roleId, List<Long> userIds) {
        sysRoleMapper.deleteUserRoleByRoleId(roleId);
        if (userIds != null && !userIds.isEmpty()) {
            sysRoleMapper.insertUserRoleByRole(roleId, userIds);
        }
        return true;
    }

    @Override
    public List<SysUser> selectUsersByRoleId(Long roleId) {
        return sysRoleMapper.selectUsersByRoleId(roleId);
    }

    @Override
    public List<SysRole> exportRoleList(SysRole role) {
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(role.getRoleName()), SysRole::getRoleName, role.getRoleName())
                .like(StringUtils.hasText(role.getRoleKey()), SysRole::getRoleKey, role.getRoleKey())
                .eq(StringUtils.hasText(role.getStatus()), SysRole::getStatus, role.getStatus())
                .orderByAsc(SysRole::getRoleSort)
                .orderByAsc(SysRole::getId);
        return sysRoleMapper.selectList(wrapper);
    }

    @Override
    public List<SysRole> selectAllRoles() {
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRole::getStatus, "0")
                .orderByAsc(SysRole::getRoleSort);
        return sysRoleMapper.selectList(wrapper);
    }
}