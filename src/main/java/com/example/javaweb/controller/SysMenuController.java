package com.example.javaweb.controller;

import com.example.javaweb.common.log.ApiLog;
import com.example.javaweb.common.result.Result;
import com.example.javaweb.entity.SysMenu;
import com.example.javaweb.service.SysMenuService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/system/menu")
public class SysMenuController {

    @Autowired
    private SysMenuService sysMenuService;

    @ApiLog("查询当前用户菜单")
    @GetMapping("/user")
    public Result<List<SysMenu>> userMenus() {
        Subject subject = SecurityUtils.getSubject();
        if (subject.isAuthenticated()) {
            Object principal = subject.getPrincipal();
            if (principal instanceof com.example.javaweb.entity.SysUser) {
                com.example.javaweb.entity.SysUser user = (com.example.javaweb.entity.SysUser) principal;
                List<SysMenu> menus = sysMenuService.selectMenusByUserId(user.getId());
                List<SysMenu> menuTree = sysMenuService.buildMenuTree(menus);
                return Result.success(menuTree);
            }
        }
        return Result.success(null);
    }

    @ApiLog("查询菜单列表")
    @RequiresPermissions("system:menu:list")
    @GetMapping("/list")
    public Result<List<SysMenu>> list(@RequestParam(required = false) String menuName,
                                      @RequestParam(required = false) String status) {
        SysMenu menu = new SysMenu();
        menu.setMenuName(menuName);
        menu.setStatus(status);
        return Result.success(sysMenuService.selectMenuList(menu));
    }

    @ApiLog("查询菜单树")
    @RequiresPermissions("system:menu:list")
    @GetMapping("/tree")
    public Result<List<SysMenu>> tree(@RequestParam(required = false) String menuName,
                                      @RequestParam(required = false) String status) {
        SysMenu menu = new SysMenu();
        menu.setMenuName(menuName);
        menu.setStatus(status);
        return Result.success(sysMenuService.selectMenuTree(menu));
    }

    @ApiLog("根据ID查询菜单")
    @RequiresPermissions("system:menu:query")
    @GetMapping("/{id}")
    public Result<SysMenu> getById(@PathVariable Long id) {
        return Result.success(sysMenuService.selectMenuById(id));
    }

    @ApiLog("新增菜单")
    @RequiresPermissions("system:menu:add")
    @PostMapping
    public Result<Void> add(@RequestBody SysMenu menu) {
        if (!sysMenuService.checkMenuNameUnique(menu)) {
            return Result.failed("菜单名称已存在");
        }
        sysMenuService.insertMenu(menu);
        return Result.success();
    }

    @ApiLog("修改菜单")
    @RequiresPermissions("system:menu:edit")
    @PutMapping
    public Result<Void> update(@RequestBody SysMenu menu) {
        if (!sysMenuService.checkMenuNameUnique(menu)) {
            return Result.failed("菜单名称已存在");
        }
        sysMenuService.updateMenu(menu);
        return Result.success();
    }

    @ApiLog("删除菜单")
    @RequiresPermissions("system:menu:remove")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        sysMenuService.deleteMenuById(id);
        return Result.success();
    }

    @ApiLog("刷新菜单缓存")
    @PostMapping("/refresh")
    public Result<Void> refreshCache() {
        Subject subject = SecurityUtils.getSubject();
        if (subject.isAuthenticated()) {
            Object principal = subject.getPrincipal();
            if (principal instanceof com.example.javaweb.entity.SysUser) {
                com.example.javaweb.entity.SysUser user = (com.example.javaweb.entity.SysUser) principal;
                sysMenuService.refreshUserMenuCache(user.getId());
                return Result.success();
            }
        }
        return Result.success();
    }
}