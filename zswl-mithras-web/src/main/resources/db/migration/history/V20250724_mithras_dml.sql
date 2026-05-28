-- 修改分类名称
update new_ftp_parameter_setting_config set category_display = '资产行业计价标准' where category = 'INDUSTRY_ASSET_VALUATION';

-- 修改参数名称
update new_ftp_parameter_setting_config set param_name = '其他产业类-鼓励介入类' where category = 'INDUSTRY_ASSET_VALUATION' and param_name = '鼓励介入类';
update new_ftp_parameter_setting_config set param_name = '其他产业类-适度支持类' where category = 'INDUSTRY_ASSET_VALUATION' and param_name = '适度支持类';
update new_ftp_parameter_setting_config set param_name = '其他产业类-谨慎支持类' where category = 'INDUSTRY_ASSET_VALUATION' and param_name = '谨慎支持类';

-- 新增参数
INSERT INTO `new_ftp_parameter_setting_config` (`category`, `category_display`, `param_name`, `value`)
VALUES
    ('INDUSTRY_ASSET_VALUATION', '资产行业计价标准', '公共事业类', 15000),
    ('INDUSTRY_ASSET_VALUATION', '资产行业计价标准', '民生消费类', 15000),
    ('INDUSTRY_ASSET_VALUATION', '资产行业计价标准', '国有产业类', 15000),
    ('COLLABORATIVE_MINIMUM_RATE', '协同最低收益率', '3年内（含）', 26000),
    ('COLLABORATIVE_MINIMUM_RATE', '协同最低收益率', '3-5年（含）', 27000),
    ('COLLABORATIVE_MINIMUM_RATE', '协同最低收益率', '5年以上', 28000);

-- 新增功能接口
INSERT INTO `bifrost_function` (`code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`)
VALUES
    ('newftpquarterlybasepricingextdetail', '集团控股公司季度最低收益率-详情', 0, 484, NULL, NULL, NULL, 'POST', '/new/ftp/quarterly/base/pricing/ext/detail', 1, NULL),
    ('newftpquarterlybasepricingextmodify', '集团控股公司季度最低收益率-编辑', 0, 484, NULL, NULL, NULL, 'POST', '/new/ftp/quarterly/base/pricing/ext/modify', 2, NULL);