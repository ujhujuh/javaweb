package com.example.javaweb.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.javaweb.entity.SysDictType;

import java.util.List;

public interface SysDictTypeService extends IService<SysDictType> {

    IPage<SysDictType> selectDictTypeList(IPage<SysDictType> page, SysDictType dictType);

    SysDictType selectDictTypeById(Long dictId);

    boolean insertDictType(SysDictType dictType);

    boolean updateDictType(SysDictType dictType);

    boolean deleteDictTypeByIds(List<Long> dictIds);

    boolean checkDictTypeUnique(SysDictType dictType);

    List<SysDictType> selectAllDictTypes();
}