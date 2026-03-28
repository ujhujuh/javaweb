package com.example.javaweb.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.javaweb.entity.UpNewsComment;
import com.example.javaweb.mapper.UpNewsCommentMapper;
import com.example.javaweb.service.UpNewsCommentService;
import org.springframework.stereotype.Service;

@Service
public class UpNewsCommentServiceImpl extends ServiceImpl<UpNewsCommentMapper, UpNewsComment> implements UpNewsCommentService {
}
