package com.example.javaweb.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.javaweb.entity.SysRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SysRoleMapper extends BaseMapper<SysRole> {

    @Select("SELECT m.* FROM sys_menu m INNER JOIN sys_role_menu rm ON m.id = rm.menu_id WHERE rm.role_id = #{roleId} AND m.status = '0' ORDER BY m.order_num")
    List<com.example.javaweb.entity.SysMenu> selectMenusByRoleId(@Param("roleId") Long roleId);

    void deleteRoleMenuByRoleId(@Param("roleId") Long roleId);

    void insertRoleMenu(@Param("roleId") Long roleId, @Param("menuIds") List<Long> menuIds);

    List<Long> selectMenuIdsByRoleId(@Param("roleId") Long roleId);

    void deleteUserRoleByRoleId(@Param("roleId") Long roleId);

    void insertUserRoleByRole(@Param("roleId") Long roleId, @Param("userIds") List<Long> userIds);

    List<com.example.javaweb.entity.SysUser> selectUsersByRoleId(@Param("roleId") Long roleId);
}