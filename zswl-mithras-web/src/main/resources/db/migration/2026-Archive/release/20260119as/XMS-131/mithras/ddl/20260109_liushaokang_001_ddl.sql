-- 舆情信息表添加字段
ALTER TABLE risk_control_opinion_monitor
ADD COLUMN source_xinsight_id BIGINT DEFAULT NULL COMMENT '慧眼数据ID',
ADD COLUMN data_source VARCHAR(20) DEFAULT 'FHC' COMMENT '数据来源: FHC-金控, XINSIGHT-慧眼',
ADD COLUMN news_url LONGTEXT DEFAULT NULL COMMENT '新闻链接地址',
ADD UNIQUE INDEX idx_source_xinsight_id (source_xinsight_id),
ADD INDEX idx_data_source (data_source);

-- 创建舆情监控客户名单同步表
CREATE TABLE client_vw_sync (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    client_id VARCHAR(50) NOT NULL COMMENT '客户ID',
    cert_number VARCHAR(50) NOT NULL COMMENT '证件号码',
    client_name VARCHAR(50) NOT NULL COMMENT '客户名称',
    sync_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '同步时间',
    sync_batch VARCHAR(50) COMMENT '同步批次号',
    UNIQUE KEY uk_client_id (client_id),
    INDEX idx_sync_time (sync_time),
    INDEX idx_sync_batch (sync_batch)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='监控客户同步表';

-- 创建视图(提供给慧眼查询)
CREATE OR REPLACE VIEW client_vw AS
SELECT
    client_id,
    cert_number,
    client_name
FROM client_vw_sync
ORDER BY client_id;