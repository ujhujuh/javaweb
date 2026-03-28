package com.example.javaweb.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.javaweb.entity.UpMaterial;
import com.example.javaweb.mapper.UpMaterialMapper;
import com.example.javaweb.service.UpMaterialService;
import org.springframework.stereotype.Service;

@Service
public class UpMaterialServiceImpl extends ServiceImpl<UpMaterialMapper, UpMaterial> implements UpMaterialService {
}
