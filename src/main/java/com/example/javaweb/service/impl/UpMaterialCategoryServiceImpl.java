package com.example.javaweb.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.javaweb.entity.UpMaterialCategory;
import com.example.javaweb.mapper.UpMaterialCategoryMapper;
import com.example.javaweb.service.UpMaterialCategoryService;
import org.springframework.stereotype.Service;

@Service
public class UpMaterialCategoryServiceImpl extends ServiceImpl<UpMaterialCategoryMapper, UpMaterialCategory>
        implements UpMaterialCategoryService {
}
