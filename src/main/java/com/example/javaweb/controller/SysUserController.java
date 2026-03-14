package com.example.javaweb.controller;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.javaweb.common.log.ApiLog;
import com.example.javaweb.common.result.Result;
import com.example.javaweb.dto.SysUserQueryDTO;
import com.example.javaweb.dto.UserExcelDTO;
import com.example.javaweb.entity.SysRole;
import com.example.javaweb.entity.SysUser;
import com.example.javaweb.service.SysUserService;
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
@RequestMapping("/api/system/user")
public class SysUserController {

    @Autowired
    private SysUserService sysUserService;

    @ApiLog("分页查询用户")
    @RequiresPermissions("system:user:list")
    @GetMapping("/list")
    public Result<IPage<SysUser>> list(SysUserQueryDTO queryDTO) {
        Page<SysUser> page = new Page<>(queryDTO.getCurrent(), queryDTO.getSize());
        SysUser user = new SysUser();
        user.setUsername(queryDTO.getUsername());
        user.setNickname(queryDTO.getNickname());
        user.setPhone(queryDTO.getPhone());
        user.setStatus(queryDTO.getStatus());
        return Result.success(sysUserService.selectUserList(page, user));
    }

    @ApiLog("根据ID查询用户")
    @RequiresPermissions("system:user:query")
    @GetMapping("/{id}")
    public Result<SysUser> getById(@PathVariable Long id) {
        return Result.success(sysUserService.selectUserById(id));
    }

    @ApiLog("新增用户")
    @RequiresPermissions("system:user:add")
    @PostMapping
    public Result<Void> add(@RequestBody SysUser user) {
        sysUserService.insertUser(user);
        return Result.success();
    }

    @ApiLog("修改用户")
    @RequiresPermissions("system:user:edit")
    @PutMapping
    public Result<Void> update(@RequestBody SysUser user) {
        sysUserService.updateUser(user);
        return Result.success();
    }

    @ApiLog("删除用户")
    @RequiresPermissions("system:user:remove")
    @DeleteMapping("/{ids}")
    public Result<Void> delete(@PathVariable Long[] ids) {
        sysUserService.deleteUserByIds(Arrays.asList(ids));
        return Result.success();
    }

    @ApiLog("重置用户密码")
    @RequiresPermissions("system:user:resetPwd")
    @PutMapping("/resetPwd")
    public Result<Void> resetPwd(@RequestBody SysUser user) {
        sysUserService.resetUserPwd(user.getId(), user.getPassword());
        return Result.success();
    }

    @ApiLog("修改用户状态")
    @RequiresPermissions("system:user:edit")
    @PutMapping("/changeStatus")
    public Result<Void> changeStatus(@RequestBody SysUser user) {
        sysUserService.updateUserStatus(user);
        return Result.success();
    }

    @ApiLog("用户角色授权")
    @RequiresPermissions("system:user:edit")
    @PutMapping("/authRole")
    public Result<Void> authRole(@RequestParam Long userId, @RequestBody List<Long> roleIds) {
        sysUserService.authRole(userId, roleIds);
        return Result.success();
    }

    @ApiLog("导出用户")
    @RequiresPermissions("system:user:export")
    @PostMapping("/export")
    public void export(HttpServletResponse response, @RequestBody SysUser user) throws IOException {
        List<SysUser> userList = sysUserService.exportUserList(user);
        List<UserExcelDTO> excelList = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        for (SysUser sysUser : userList) {
            UserExcelDTO dto = new UserExcelDTO();
            BeanUtils.copyProperties(sysUser, dto);
            if (sysUser.getCreateTime() != null) {
                dto.setCreateTime(sysUser.getCreateTime().format(formatter));
            }
            dto.setStatus("0".equals(sysUser.getStatus()) ? "正常" : "停用");
            excelList.add(dto);
        }

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("用户列表", "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");

        EasyExcel.write(response.getOutputStream(), UserExcelDTO.class)
                .sheet("用户列表")
                .doWrite(excelList);
    }
}