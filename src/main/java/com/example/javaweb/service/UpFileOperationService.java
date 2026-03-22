package com.example.javaweb.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.javaweb.entity.UpFileOperation;

import java.time.LocalDate;
import java.util.List;

public interface UpFileOperationService extends IService<UpFileOperation> {

    IPage<UpFileOperation> selectList(IPage<UpFileOperation> page, UpFileOperation operation);

    List<UpFileOperation> selectByUserIdAndDate(Long userId, LocalDate date);

    List<UpFileOperation> selectByUserIdAndDateRange(Long userId, LocalDate startDate, LocalDate endDate);
}