package com.example.javaweb.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.javaweb.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {

    @Select("SELECT r.* FROM sys_role r INNER JOIN sys_user_role ur ON r.id = ur.role_id WHERE ur.user_id = #{userId}")
    List<com.example.javaweb.entity.SysRole> selectRolesByUserId(@Param("userId") Long userId);

    @Select("SELECT ur.role_id FROM sys_user_role ur WHERE ur.user_id = #{userId}")
    List<Long> selectRoleIdsByUserId(@Param("userId") Long userId);

    @Select("SELECT m.* FROM sys_menu m INNER JOIN sys_role_menu rm ON m.id = rm.menu_id INNER JOIN sys_user_role ur ON rm.role_id = ur.role_id WHERE ur.user_id = #{userId} AND m.status = '0' ORDER BY m.order_num")
    List<com.example.javaweb.entity.SysMenu> selectMenusByUserId(@Param("userId") Long userId);

    void deleteUserRoleByUserId(@Param("userId") Long userId);

    void insertUserRole(@Param("userId") Long userId, @Param("roleIds") List<Long> roleIds);

    List<SysUser> selectUsersByRoleIds(@Param("roleIds") List<Long> roleIds);
}