package com.example.javaweb.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.javaweb.entity.UpNewsCategory;
import com.example.javaweb.mapper.UpNewsCategoryMapper;
import com.example.javaweb.service.UpNewsCategoryService;
import org.springframework.stereotype.Service;

@Service
public class UpNewsCategoryServiceImpl extends ServiceImpl<UpNewsCategoryMapper, UpNewsCategory> implements UpNewsCategoryService {
}
