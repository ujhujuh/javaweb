package com.example.javaweb.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.javaweb.entity.SysUser;

import java.util.List;

public interface SysUserService extends IService<SysUser> {

    IPage<SysUser> selectUserList(IPage<SysUser> page, SysUser user);

    SysUser selectUserById(Long userId);

    boolean insertUser(SysUser user);

    boolean updateUser(SysUser user);

    boolean deleteUserByIds(List<Long> userIds);

    boolean resetUserPwd(Long userId, String password);

    boolean updateUserStatus(SysUser user);

    boolean authRole(Long userId, List<Long> roleIds);

    List<SysUser> selectUsersByRoleIds(List<Long> roleIds);

    List<SysUser> exportUserList(SysUser user);
}