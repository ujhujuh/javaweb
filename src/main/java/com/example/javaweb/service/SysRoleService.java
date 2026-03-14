package com.example.javaweb.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.javaweb.entity.SysRole;
import com.example.javaweb.entity.SysUser;

import java.util.List;

public interface SysRoleService extends IService<SysRole> {

    IPage<SysRole> selectRoleList(IPage<SysRole> page, SysRole role);

    SysRole selectRoleById(Long roleId);

    boolean insertRole(SysRole role);

    boolean updateRole(SysRole role);

    boolean deleteRoleByIds(List<Long> roleIds);

    boolean authMenu(Long roleId, List<Long> menuIds);

    List<Long> selectMenuIdsByRoleId(Long roleId);

    boolean authUser(Long roleId, List<Long> userIds);

    List<SysUser> selectUsersByRoleId(Long roleId);

    List<SysRole> exportRoleList(SysRole role);

    List<SysRole> selectAllRoles();
}