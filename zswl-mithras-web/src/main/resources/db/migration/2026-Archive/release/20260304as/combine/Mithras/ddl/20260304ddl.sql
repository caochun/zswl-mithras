

-- ============= 来自文件：20260206_liushaokang_001_ddl.sql ============= 

-- 人工录入舆情,舆情表新增字段
ALTER TABLE risk_control_opinion_monitor
ADD COLUMN relation_company_code VARCHAR(50) DEFAULT NULL COMMENT '关联主体统一社会信用代码',
ADD COLUMN relation_description VARCHAR(500) DEFAULT NULL COMMENT '描述说明';


-- ============= 来自文件：20260206_liushaokang_001_ddl.sql ============= 

-- 131-02接通慧眼预警数据
-- 预警表添加字段
ALTER TABLE risk_control_warn_monitor
MODIFY link_address LONGTEXT NULL COMMENT '链接地址',
ADD COLUMN source_xinsight_id BIGINT DEFAULT NULL COMMENT '慧眼数据唯一标识',
ADD COLUMN data_source VARCHAR(20) DEFAULT 'FHC' COMMENT '数据来源: FHC-金控, XINSIGHT-慧眼',
ADD UNIQUE INDEX idx_source_xinsight_id (source_xinsight_id),
ADD INDEX idx_data_source (data_source);


-- ============= 来自文件：20260206_liushaokang_001_ddl.sql ============= 

-- XMX-131-03：SFTP沙盘文件上传
-- 创建沙盘数据记录表
CREATE TABLE client_sand_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    client_id BIGINT DEFAULT NULL COMMENT '客户ID',
    usc_code VARCHAR(50) NOT NULL COMMENT '统一社会信用代码',
    client_name VARCHAR(50) NOT NULL COMMENT '客户名称',
    record_date DATE DEFAULT NULL COMMENT '记录日期 yyyy-MM-dd',
    data_source VARCHAR(20) DEFAULT NULL COMMENT '数据来源：VW-vw视图 WB-外部客户',
	data_type VARCHAR(20) DEFAULT NULL COMMENT '数据类型：A-增加 D-删除',
	data_mark VARCHAR(20) DEFAULT '0' COMMENT '数据标记,是否转为系统内部数据:1-是,0-否',
	first_mark VARCHAR(20) DEFAULT NULL COMMENT '是否第一版: 1-是,0-否',
    INDEX idx_usc_code (usc_code),
    INDEX idx_record_date (record_date),
    INDEX idx_data_source (data_source)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客户沙盘数据记录表';

-- 创建外部客户名单表
CREATE TABLE external_customer (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
	client_name VARCHAR(50) NOT NULL COMMENT '客户名称',
    usc_code VARCHAR(50) NOT NULL COMMENT '统一社会信用代码',
	is_use VARCHAR(10) DEFAULT '0' COMMENT '是否使用: 0-没有,1-已使用',
    INDEX idx_usc_code (usc_code),
	INDEX idx_is_use (is_use)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='外部客户名单表';


-- ============= 来自文件：202602028_linlili_001_ddl.sql ============= 

ALTER TABLE filing_first_level_config ADD docx_enable_flag int(11) DEFAULT 0 NULL COMMENT '模板模板是否展示,0不展示，1展示';
