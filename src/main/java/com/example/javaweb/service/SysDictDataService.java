package com.example.javaweb.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.javaweb.entity.SysDictData;

import java.util.List;

public interface SysDictDataService extends IService<SysDictData> {

    IPage<SysDictData> selectDictDataList(IPage<SysDictData> page, SysDictData dictData);

    List<SysDictData> selectDictDataList(SysDictData dictData);

    SysDictData selectDictDataById(Long dictCode);

    boolean insertDictData(SysDictData dictData);

    boolean updateDictData(SysDictData dictData);

    boolean deleteDictDataByIds(List<Long> dictCodes);

    boolean checkDictValueUnique(SysDictData dictData);
}