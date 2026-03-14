package com.example.javaweb.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.javaweb.entity.SysDictData;
import com.example.javaweb.entity.SysDictType;
import com.example.javaweb.mapper.SysDictDataMapper;
import com.example.javaweb.mapper.SysDictTypeMapper;
import com.example.javaweb.service.SysDictTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class SysDictTypeServiceImpl extends ServiceImpl<SysDictTypeMapper, SysDictType> implements SysDictTypeService {

    @Autowired
    private SysDictTypeMapper sysDictTypeMapper;

    @Autowired
    private SysDictDataMapper sysDictDataMapper;

    @Override
    public IPage<SysDictType> selectDictTypeList(IPage<SysDictType> page, SysDictType dictType) {
        LambdaQueryWrapper<SysDictType> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(dictType.getDictName()), SysDictType::getDictName, dictType.getDictName())
                .like(StringUtils.hasText(dictType.getDictType()), SysDictType::getDictType, dictType.getDictType())
                .eq(StringUtils.hasText(dictType.getStatus()), SysDictType::getStatus, dictType.getStatus())
                .orderByAsc(SysDictType::getId);
        return sysDictTypeMapper.selectPage(page, wrapper);
    }

    @Override
    public SysDictType selectDictTypeById(Long dictId) {
        return sysDictTypeMapper.selectById(dictId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean insertDictType(SysDictType dictType) {
        return sysDictTypeMapper.insert(dictType) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateDictType(SysDictType dictType) {
        SysDictType oldDict = sysDictTypeMapper.selectById(dictType.getId());
        SysDictType newDict = sysDictTypeMapper.selectById(dictType.getId());
        newDict.setDictType(dictType.getDictType());
        sysDictTypeMapper.updateById(newDict);

        LambdaQueryWrapper<SysDictData> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysDictData::getDictType, oldDict.getDictType());
        List<SysDictData> dictDataList = sysDictDataMapper.selectList(wrapper);
        for (SysDictData dictData : dictDataList) {
            dictData.setDictType(dictType.getDictType());
            sysDictDataMapper.updateById(dictData);
        }

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteDictTypeByIds(List<Long> dictIds) {
        for (Long dictId : dictIds) {
            SysDictType dictType = sysDictTypeMapper.selectById(dictId);
            LambdaQueryWrapper<SysDictData> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SysDictData::getDictType, dictType.getDictType());
            long count = sysDictDataMapper.selectCount(wrapper);
            if (count > 0) {
                throw new RuntimeException("字典类型【" + dictType.getDictName() + "】已分配字典值,不允许删除");
            }
        }
        return sysDictTypeMapper.deleteBatchIds(dictIds) > 0;
    }

    @Override
    public boolean checkDictTypeUnique(SysDictType dictType) {
        Long dictId = dictType.getId() == null ? -1L : dictType.getId();
        LambdaQueryWrapper<SysDictType> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysDictType::getDictType, dictType.getDictType());
        SysDictType info = sysDictTypeMapper.selectOne(wrapper);
        if (info != null && !info.getId().equals(dictId)) {
            return false;
        }
        return true;
    }

    @Override
    public List<SysDictType> selectAllDictTypes() {
        LambdaQueryWrapper<SysDictType> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysDictType::getStatus, "0")
                .orderByAsc(SysDictType::getId);
        return sysDictTypeMapper.selectList(wrapper);
    }
}