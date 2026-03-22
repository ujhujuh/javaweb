package com.example.javaweb.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.javaweb.entity.UpFileOperation;
import com.example.javaweb.mapper.UpFileOperationMapper;
import com.example.javaweb.service.UpFileOperationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class UpFileOperationServiceImpl extends ServiceImpl<UpFileOperationMapper, UpFileOperation> implements UpFileOperationService {

    @Autowired
    private UpFileOperationMapper upFileOperationMapper;

    @Override
    public IPage<UpFileOperation> selectList(IPage<UpFileOperation> page, UpFileOperation operation) {
        LambdaQueryWrapper<UpFileOperation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(operation.getUserId() != null, UpFileOperation::getUserId, operation.getUserId())
                .eq(operation.getOperationType() != null, UpFileOperation::getOperationType, operation.getOperationType())
                .like(operation.getFileType() != null, UpFileOperation::getFileType, operation.getFileType())
                .orderByDesc(UpFileOperation::getOperationTime);
        return upFileOperationMapper.selectPage(page, wrapper);
    }

    @Override
    public List<UpFileOperation> selectByUserIdAndDate(Long userId, LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(23, 59, 59);
        LambdaQueryWrapper<UpFileOperation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UpFileOperation::getUserId, userId)
                .between(UpFileOperation::getOperationTime, startOfDay, endOfDay)
                .orderByDesc(UpFileOperation::getOperationTime);
        return upFileOperationMapper.selectList(wrapper);
    }

    @Override
    public List<UpFileOperation> selectByUserIdAndDateRange(Long userId, LocalDate startDate, LocalDate endDate) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);
        LambdaQueryWrapper<UpFileOperation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UpFileOperation::getUserId, userId)
                .between(UpFileOperation::getOperationTime, startDateTime, endDateTime)
                .orderByDesc(UpFileOperation::getOperationTime);
        return upFileOperationMapper.selectList(wrapper);
    }
}