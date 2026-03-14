package com.example.javaweb.controller;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.javaweb.common.log.ApiLog;
import com.example.javaweb.common.result.Result;
import com.example.javaweb.dto.RoleExcelDTO;
import com.example.javaweb.entity.SysRole;
import com.example.javaweb.entity.SysUser;
import com.example.javaweb.service.SysRoleService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/system/role")
public class SysRoleController {

    @Autowired
    private SysRoleService sysRoleService;

    @ApiLog("分页查询角色")
    @RequiresPermissions("system:role:list")
    @GetMapping("/list")
    public Result<IPage<SysRole>> list(@RequestParam(defaultValue = "1") Integer current,
                                      @RequestParam(defaultValue = "10") Integer size,
                                      @RequestParam(required = false) String roleName,
                                      @RequestParam(required = false) String roleKey,
                                      @RequestParam(required = false) String status) {
        Page<SysRole> page = new Page<>(current, size);
        SysRole role = new SysRole();
        role.setRoleName(roleName);
        role.setRoleKey(roleKey);
        role.setStatus(status);
        return Result.success(sysRoleService.selectRoleList(page, role));
    }

    @ApiLog("查询所有角色")
    @GetMapping("/all")
    public Result<List<SysRole>> all() {
        return Result.success(sysRoleService.selectAllRoles());
    }

    @ApiLog("根据ID查询角色")
    @RequiresPermissions("system:role:query")
    @GetMapping("/{id}")
    public Result<SysRole> getById(@PathVariable Long id) {
        return Result.success(sysRoleService.selectRoleById(id));
    }

    @ApiLog("新增角色")
    @RequiresPermissions("system:role:add")
    @PostMapping
    public Result<Void> add(@RequestBody SysRole role) {
        sysRoleService.insertRole(role);
        return Result.success();
    }

    @ApiLog("修改角色")
    @RequiresPermissions("system:role:edit")
    @PutMapping
    public Result<Void> update(@RequestBody SysRole role) {
        sysRoleService.updateRole(role);
        return Result.success();
    }

    @ApiLog("删除角色")
    @RequiresPermissions("system:role:remove")
    @DeleteMapping("/{ids}")
    public Result<Void> delete(@PathVariable Long[] ids) {
        sysRoleService.deleteRoleByIds(Arrays.asList(ids));
        return Result.success();
    }

    @ApiLog("角色菜单授权")
    @RequiresPermissions("system:role:edit")
    @PutMapping("/authMenu")
    public Result<Void> authMenu(@RequestParam Long roleId, @RequestBody List<Long> menuIds) {
        sysRoleService.authMenu(roleId, menuIds);
        return Result.success();
    }

    @ApiLog("查询角色菜单ID")
    @GetMapping("/menuIds/{roleId}")
    public Result<List<Long>> getMenuIds(@PathVariable Long roleId) {
        return Result.success(sysRoleService.selectMenuIdsByRoleId(roleId));
    }

    @ApiLog("角色分配用户")
    @RequiresPermissions("system:role:edit")
    @PutMapping("/authUser")
    public Result<Void> authUser(@RequestParam Long roleId, @RequestBody List<Long> userIds) {
        sysRoleService.authUser(roleId, userIds);
        return Result.success();
    }

    @ApiLog("查询角色用户")
    @GetMapping("/users/{roleId}")
    public Result<List<SysUser>> getUsers(@PathVariable Long roleId) {
        return Result.success(sysRoleService.selectUsersByRoleId(roleId));
    }

    @ApiLog("导出角色")
    @RequiresPermissions("system:role:export")
    @PostMapping("/export")
    public void export(HttpServletResponse response, @RequestBody SysRole role) throws IOException {
        List<SysRole> roleList = sysRoleService.exportRoleList(role);
        List<RoleExcelDTO> excelList = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        for (SysRole sysRole : roleList) {
            RoleExcelDTO dto = new RoleExcelDTO();
            BeanUtils.copyProperties(sysRole, dto);
            if (sysRole.getCreateTime() != null) {
                dto.setCreateTime(sysRole.getCreateTime().format(formatter));
            }
            dto.setStatus("0".equals(sysRole.getStatus()) ? "正常" : "停用");
            excelList.add(dto);
        }

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("角色列表", "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");

        EasyExcel.write(response.getOutputStream(), RoleExcelDTO.class)
                .sheet("角色列表")
                .doWrite(excelList);
    }
}