package com.example.javaweb.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.javaweb.entity.UpUserProfile;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface UpUserProfileMapper extends BaseMapper<UpUserProfile> {

    /**
     * 根据用户ID和日期查询画像
     */
    UpUserProfile selectByUserIdAndDate(@Param("userId") Long userId, @Param("profileDate") LocalDate profileDate);

    /**
     * 查询用户的历史画像列表
     */
    List<UpUserProfile> selectHistoryByUserId(@Param("userId") Long userId);

    /**
     * 查询指定日期范围内的画像
     */
    List<UpUserProfile> selectByDateRange(@Param("userId") Long userId,
                                           @Param("startDate") LocalDate startDate,
                                           @Param("endDate") LocalDate endDate);
}