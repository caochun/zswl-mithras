-- 经营全景视图
INSERT INTO `bifrost_system_config` (`config_key`, `config_value`, `created_by`, `updated_by`, `description`, `status`, `type`)
VALUES
('dashboard.balance.goal.2024', '224000000000000', '', '', '资产余额2024年目标', 0, 'String');

-- 客户管理-联系人信息-邮箱修改-发送通知
INSERT INTO bifrost_system_config ( config_key, config_value, created_by, updated_by, description, status, type)
VALUES ('emailChangeNotification', '108,355', 'admin', 'admin', '客户管理-联系人信息-邮箱修改-发送通知', 1, 'String');


ALTER table funds_daily_cost add `property_type` varchar(40) DEFAULT NULL COMMENT '质押资产类型';

ALTER table lease_item_vat_invoice_product add `tax_not_included` varchar(200) DEFAULT NULL COMMENT '金额（不含税）';