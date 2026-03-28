package com.example.javaweb.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.javaweb.entity.UpMaterialOrder;
import com.example.javaweb.mapper.UpMaterialOrderMapper;
import com.example.javaweb.service.UpMaterialOrderService;
import org.springframework.stereotype.Service;

@Service
public class UpMaterialOrderServiceImpl extends ServiceImpl<UpMaterialOrderMapper, UpMaterialOrder>
        implements UpMaterialOrderService {
}
