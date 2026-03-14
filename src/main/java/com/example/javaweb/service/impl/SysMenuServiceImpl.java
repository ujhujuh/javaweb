package com.example.javaweb.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.javaweb.entity.SysMenu;
import com.example.javaweb.mapper.SysMenuMapper;
import com.example.javaweb.service.SysMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements SysMenuService {

    @Autowired
    private SysMenuMapper sysMenuMapper;

    @Override
    public List<SysMenu> selectMenuList(SysMenu menu) {
        LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(menu.getMenuName()), SysMenu::getMenuName, menu.getMenuName())
                .eq(StringUtils.hasText(menu.getStatus()), SysMenu::getStatus, menu.getStatus())
                .orderByAsc(SysMenu::getParentId)
                .orderByAsc(SysMenu::getOrderNum);
        return sysMenuMapper.selectList(wrapper);
    }

    @Override
    public List<SysMenu> selectMenuTree(SysMenu menu) {
        List<SysMenu> menus = selectMenuList(menu);
        return buildMenuTree(menus);
    }

    @Override
    public SysMenu selectMenuById(Long menuId) {
        return sysMenuMapper.selectById(menuId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean insertMenu(SysMenu menu) {
        return sysMenuMapper.insert(menu) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateMenu(SysMenu menu) {
        return sysMenuMapper.updateById(menu) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteMenuById(Long menuId) {
        if (hasChildByMenuId(menuId)) {
            throw new RuntimeException("存在子菜单,不允许删除");
        }
        return sysMenuMapper.deleteById(menuId) > 0;
    }

    @Override
    public List<SysMenu> selectMenusByUserId(Long userId) {
        return sysMenuMapper.selectMenusByUserId(userId);
    }

    @Override
    public boolean hasChildByMenuId(Long menuId) {
        LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysMenu::getParentId, menuId);
        return sysMenuMapper.selectCount(wrapper) > 0;
    }

    @Override
    public boolean checkMenuNameUnique(SysMenu menu) {
        Long menuId = menu.getId() == null ? -1L : menu.getId();
        LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysMenu::getMenuName, menu.getMenuName())
                .eq(SysMenu::getParentId, menu.getParentId());
        SysMenu info = sysMenuMapper.selectOne(wrapper);
        if (info != null && !info.getId().equals(menuId)) {
            return false;
        }
        return true;
    }

    @Override
    public List<SysMenu> buildMenuTree(List<SysMenu> menus) {
        List<SysMenu> returnList = new ArrayList<>();
        List<Long> tempList = menus.stream().map(SysMenu::getId).collect(Collectors.toList());

        for (SysMenu menu : menus) {
            if (!tempList.contains(menu.getParentId())) {
                returnList.add(menu);
            }
        }

        if (returnList.isEmpty()) {
            returnList.addAll(menus);
        }

        return getChildPerms(returnList, menus);
    }

    private List<SysMenu> getChildPerms(List<SysMenu> list, List<SysMenu> allList) {
        for (Iterator<SysMenu> it = list.iterator(); it.hasNext(); ) {
            SysMenu menu = it.next();
            List<SysMenu> childList = new ArrayList<>();
            for (SysMenu child : allList) {
                if (child.getParentId().equals(menu.getId())) {
                    childList.add(child);
                }
            }
            menu.setChildren(childList);
            if (!childList.isEmpty()) {
                getChildPerms(childList, allList);
            }
        }
        return list;
    }
}