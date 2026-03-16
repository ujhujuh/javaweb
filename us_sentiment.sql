-- =============================================
-- 美股情绪指标表
-- =============================================
DROP TABLE IF EXISTS `us_sentiment`;
CREATE TABLE `us_sentiment` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `vix` DECIMAL(10, 2) DEFAULT NULL COMMENT 'VIX指数（恐慌指数）',
    `fear_greed` DECIMAL(10, 2) DEFAULT NULL COMMENT 'Fear & Greed指数（恐惧贪婪指数）',
    `naaim` DECIMAL(10, 2) DEFAULT NULL COMMENT 'NAAIM指数（机构仓位指数）',
    `rsi_sp500` DECIMAL(10, 2) DEFAULT NULL COMMENT 'RSI标普500指数（相对强弱指数）',
    `record_date` DATE NOT NULL COMMENT '记录日期（前一个工作日）',
    `vix_condition` CHAR(1) DEFAULT '0' COMMENT 'VIX是否满足条件（0否 1是）',
    `fear_greed_condition` CHAR(1) DEFAULT '0' COMMENT 'Fear & Greed是否满足条件（0否 1是）',
    `naaim_condition` CHAR(1) DEFAULT '0' COMMENT 'NAAIM是否满足条件（0否 1是）',
    `rsi_condition` CHAR(1) DEFAULT '0' COMMENT 'RSI是否满足条件（0否 1是）',
    `satisfied_count` INT(2) DEFAULT 0 COMMENT '满足条件的数量（0-4）',
    `notification_sent` CHAR(1) DEFAULT '0' COMMENT '是否已发送通知（0否 1是）',
    `create_by` VARCHAR(64) DEFAULT NULL COMMENT '创建者',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` VARCHAR(64) DEFAULT NULL COMMENT '更新者',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_record_date` (`record_date`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='美股情绪指标表';