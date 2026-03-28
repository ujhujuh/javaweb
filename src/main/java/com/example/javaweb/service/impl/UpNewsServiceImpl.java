package com.example.javaweb.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.javaweb.entity.UpNews;
import com.example.javaweb.mapper.UpNewsMapper;
import com.example.javaweb.service.UpNewsService;
import org.springframework.stereotype.Service;

@Service
public class UpNewsServiceImpl extends ServiceImpl<UpNewsMapper, UpNews> implements UpNewsService {
}
