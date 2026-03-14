package com.example.javaweb.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.javaweb.entity.SysDictData;
import com.example.javaweb.mapper.SysDictDataMapper;
import com.example.javaweb.service.SysDictDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class SysDictDataServiceImpl extends ServiceImpl<SysDictDataMapper, SysDictData> implements SysDictDataService {

    @Autowired
    private SysDictDataMapper sysDictDataMapper;

    @Override
    public IPage<SysDictData> selectDictDataList(IPage<SysDictData> page, SysDictData dictData) {
        LambdaQueryWrapper<SysDictData> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(dictData.getDictLabel()), SysDictData::getDictLabel, dictData.getDictLabel())
                .like(StringUtils.hasText(dictData.getDictType()), SysDictData::getDictType, dictData.getDictType())
                .eq(StringUtils.hasText(dictData.getStatus()), SysDictData::getStatus, dictData.getStatus())
                .orderByAsc(SysDictData::getDictSort)
                .orderByAsc(SysDictData::getId);
        return sysDictDataMapper.selectPage(page, wrapper);
    }

    @Override
    public List<SysDictData> selectDictDataList(SysDictData dictData) {
        LambdaQueryWrapper<SysDictData> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(dictData.getDictLabel()), SysDictData::getDictLabel, dictData.getDictLabel())
                .like(StringUtils.hasText(dictData.getDictType()), SysDictData::getDictType, dictData.getDictType())
                .eq(StringUtils.hasText(dictData.getStatus()), SysDictData::getStatus, dictData.getStatus())
                .orderByAsc(SysDictData::getDictSort)
                .orderByAsc(SysDictData::getId);
        return sysDictDataMapper.selectList(wrapper);
    }

    @Override
    public SysDictData selectDictDataById(Long dictCode) {
        return sysDictDataMapper.selectById(dictCode);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean insertDictData(SysDictData dictData) {
        return sysDictDataMapper.insert(dictData) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateDictData(SysDictData dictData) {
        return sysDictDataMapper.updateById(dictData) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteDictDataByIds(List<Long> dictCodes) {
        return sysDictDataMapper.deleteBatchIds(dictCodes) > 0;
    }

    @Override
    public boolean checkDictValueUnique(SysDictData dictData) {
        Long dictCode = dictData.getId() == null ? -1L : dictData.getId();
        LambdaQueryWrapper<SysDictData> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysDictData::getDictType, dictData.getDictType())
                .eq(SysDictData::getDictValue, dictData.getDictValue());
        SysDictData info = sysDictDataMapper.selectOne(wrapper);
        if (info != null && !info.getId().equals(dictCode)) {
            return false;
        }
        return true;
    }
}