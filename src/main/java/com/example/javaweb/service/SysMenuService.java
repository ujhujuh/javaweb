package com.example.javaweb.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.javaweb.entity.SysMenu;

import java.util.List;

public interface SysMenuService extends IService<SysMenu> {

    List<SysMenu> selectMenuList(SysMenu menu);

    List<SysMenu> selectMenuTree(SysMenu menu);

    SysMenu selectMenuById(Long menuId);

    boolean insertMenu(SysMenu menu);

    boolean updateMenu(SysMenu menu);

    boolean deleteMenuById(Long menuId);

    List<SysMenu> selectMenusByUserId(Long userId);

    void refreshUserMenuCache(Long userId);

    void refreshAllUserMenuCache();

    boolean hasChildByMenuId(Long menuId);

    boolean checkMenuNameUnique(SysMenu menu);

    List<SysMenu> buildMenuTree(List<SysMenu> menus);
}