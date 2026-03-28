package com.example.javaweb.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.javaweb.common.log.ApiLog;
import com.example.javaweb.common.result.Result;
import com.example.javaweb.dto.SysDictDataQueryDTO;
import com.example.javaweb.dto.SysDictTypeQueryDTO;
import com.example.javaweb.entity.SysDictData;
import com.example.javaweb.entity.SysDictType;
import com.example.javaweb.service.SysDictDataService;
import com.example.javaweb.service.SysDictTypeService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/system/dict")
public class SysDictController {

    @Autowired
    private SysDictTypeService sysDictTypeService;

    @Autowired
    private SysDictDataService sysDictDataService;

    @ApiLog("分页查询字典类型")
    @RequiresPermissions("system:dict:list")
    @GetMapping("/type/list")
    public Result<IPage<SysDictType>> typeList(SysDictTypeQueryDTO queryDTO) {
        Page<SysDictType> page = new Page<>(queryDTO.getCurrent(), queryDTO.getSize());
        SysDictType dictTypeObj = new SysDictType();
        dictTypeObj.setDictName(queryDTO.getDictName());
        dictTypeObj.setDictType(queryDTO.getDictType());
        dictTypeObj.setStatus(queryDTO.getStatus());
        return Result.success(sysDictTypeService.selectDictTypeList(page, dictTypeObj));
    }

    @ApiLog("查询所有字典类型")
    @GetMapping("/type/all")
    public Result<List<SysDictType>> typeAll() {
        return Result.success(sysDictTypeService.selectAllDictTypes());
    }

    @ApiLog("根据ID查询字典类型")
    @RequiresPermissions("system:dict:query")
    @GetMapping("/type/{id}")
    public Result<SysDictType> getTypeById(@PathVariable Long id) {
        return Result.success(sysDictTypeService.selectDictTypeById(id));
    }

    @ApiLog("新增字典类型")
    @RequiresPermissions("system:dict:add")
    @PostMapping("/type")
    public Result<Void> addType(@RequestBody SysDictType dictType) {
        if (!sysDictTypeService.checkDictTypeUnique(dictType)) {
            return Result.failed("字典类型已存在");
        }
        sysDictTypeService.insertDictType(dictType);
        return Result.success();
    }

    @ApiLog("修改字典类型")
    @RequiresPermissions("system:dict:edit")
    @PutMapping("/type")
    public Result<Void> updateType(@RequestBody SysDictType dictType) {
        if (!sysDictTypeService.checkDictTypeUnique(dictType)) {
            return Result.failed("字典类型已存在");
        }
        sysDictTypeService.updateDictType(dictType);
        return Result.success();
    }

    @ApiLog("删除字典类型")
    @RequiresPermissions("system:dict:remove")
    @DeleteMapping("/type/{ids}")
    public Result<Void> deleteType(@PathVariable Long[] ids) {
        sysDictTypeService.deleteDictTypeByIds(Arrays.asList(ids));
        return Result.success();
    }

    @ApiLog("分页查询字典数据")
    @RequiresPermissions("system:dict:list")
    @GetMapping("/data/list")
    public Result<IPage<SysDictData>> dataList(SysDictDataQueryDTO queryDTO) {
        Page<SysDictData> page = new Page<>(queryDTO.getCurrent(), queryDTO.getSize());
        SysDictData dictData = new SysDictData();
        dictData.setDictLabel(queryDTO.getDictLabel());
        dictData.setDictType(queryDTO.getDictType());
        dictData.setStatus(queryDTO.getStatus());
        return Result.success(sysDictDataService.selectDictDataList(page, dictData));
    }

    @ApiLog("查询字典数据列表")
    @GetMapping("/data/list/{dictType}")
    public Result<List<SysDictData>> dataByType(@PathVariable String dictType) {
        SysDictData dictData = new SysDictData();
        dictData.setDictType(dictType);
        dictData.setStatus("0");
        return Result.success(sysDictDataService.selectDictDataList(dictData));
    }

    @ApiLog("根据ID查询字典数据")
    @RequiresPermissions("system:dict:query")
    @GetMapping("/data/{id}")
    public Result<SysDictData> getDataById(@PathVariable Long id) {
        return Result.success(sysDictDataService.selectDictDataById(id));
    }

    @ApiLog("新增字典数据")
    @RequiresPermissions("system:dict:add")
    @PostMapping("/data")
    public Result<Void> addData(@RequestBody SysDictData dictData) {
        if (!sysDictDataService.checkDictValueUnique(dictData)) {
            return Result.failed("字典数据值已存在");
        }
        sysDictDataService.insertDictData(dictData);
        return Result.success();
    }

    @ApiLog("修改字典数据")
    @RequiresPermissions("system:dict:edit")
    @PutMapping("/data")
    public Result<Void> updateData(@RequestBody SysDictData dictData) {
        if (!sysDictDataService.checkDictValueUnique(dictData)) {
            return Result.failed("字典数据值已存在");
        }
        sysDictDataService.updateDictData(dictData);
        return Result.success();
    }

    @ApiLog("删除字典数据")
    @RequiresPermissions("system:dict:remove")
    @DeleteMapping("/data/{ids}")
    public Result<Void> deleteData(@PathVariable Long[] ids) {
        sysDictDataService.deleteDictDataByIds(Arrays.asList(ids));
        return Result.success();
    }
}
