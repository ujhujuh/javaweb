package com.example.javaweb.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.example.javaweb.common.context.CurrentUserContext;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * MyBatis-Plus 字段自动填充处理器
 */
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    /**
     * 插入时自动填充
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, LocalDateTime.now());
        this.strictInsertFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
        this.strictInsertFill(metaObject, "createBy", String.class, getCurrentUsername());
        this.strictInsertFill(metaObject, "updateBy", String.class, getCurrentUsername());
    }

    /**
     * 更新时自动填充
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
        this.strictUpdateFill(metaObject, "updateBy", String.class, getCurrentUsername());
    }

    /**
     * 获取当前用户名
     */
    private String getCurrentUsername() {
        try {
            String username = CurrentUserContext.getUsername();
            if (username != null && !username.isEmpty()) {
                return username;
            }
        } catch (Exception e) {
            // 忽略异常，返回默认值
        }
        return "system";
    }
}