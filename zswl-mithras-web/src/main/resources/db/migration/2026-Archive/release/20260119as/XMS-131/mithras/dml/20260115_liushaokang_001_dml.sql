-- 删除系统配置表中的金控舆情数据开关
delete from bifrost_system_config where config_key = 'FHC_OPINION_SWITCH';

-- 系统配置表中插入金控舆情数据开关
INSERT INTO
bifrost_system_config (config_key, config_value, created_by, updated_by, description, status, `type`)
VALUES('FHC_OPINION_SWITCH', 'CLOSE', 'admin', 'admin', '金控舆情数据开关', 1, 'String');