-- 拨备参数调整
update `budget_parameter_config` set `config_value` = '[{"ftpIndustryCategory":"FTP_PUBLIC_UTILITIES","termRange":"THREE_YEAR","riskReserve":"10000"},{"ftpIndustryCategory":"FTP_PUBLIC_UTILITIES","termRange":"THREE_TO_FIVE_YEAR","riskReserve":"10000"},{"ftpIndustryCategory":"FTP_PUBLIC_UTILITIES","termRange":"MORE_THAN_FIVE_YEAR","riskReserve":"10000"},{"ftpIndustryCategory":"FTP_CIVIL_CONSUMPTION","termRange":"THREE_YEAR","riskReserve":"10000"},{"ftpIndustryCategory":"FTP_CIVIL_CONSUMPTION","termRange":"THREE_TO_FIVE_YEAR","riskReserve":"10000"},{"ftpIndustryCategory":"FTP_CIVIL_CONSUMPTION","termRange":"MORE_THAN_FIVE_YEAR","riskReserve":"10000"},{"ftpIndustryCategory":"FTP_STATE_OWNED_INDUSTRY","termRange":"THREE_YEAR","riskReserve":"10000"},{"ftpIndustryCategory":"FTP_STATE_OWNED_INDUSTRY","termRange":"THREE_TO_FIVE_YEAR","riskReserve":"15000"},{"ftpIndustryCategory":"FTP_STATE_OWNED_INDUSTRY","termRange":"MORE_THAN_FIVE_YEAR","riskReserve":"20000"},{"ftpIndustryCategory":"FTP_OTHER_INDUSTRY","termRange":"THREE_YEAR","riskReserve":"10000"},{"ftpIndustryCategory":"FTP_OTHER_INDUSTRY","termRange":"THREE_TO_FIVE_YEAR","riskReserve":"15000"},{"ftpIndustryCategory":"FTP_OTHER_INDUSTRY","termRange":"MORE_THAN_FIVE_YEAR","riskReserve":"20000"}]' where `config_key` = 'RISK_RATIO';


-- ftp收益率

INSERT INTO bifrost_menu (code, level, sort_no, type, path, parent_id, icon, name)
VALUES ('ftpIncome', 3, 0, null, '/budget/pricing/ftpYield', null, null, 'FTP收益率');

INSERT INTO bifrost_custom_tree_menu_ref (custom_tree_id, menu_id, sort_no)
VALUES (88, (select id from bifrost_menu where code = 'ftpIncome'), 0);


insert into bifrost_function (code, name, sort_no, menu_id, method, path, type)
values  ('ftpIncomeBaseInfoList', '资金管理-融资管理-ftp收益表列表', 0, (select id from bifrost_menu where code = 'ftpIncome'), 'POST', '/ftp/income/base/info/list', 2),
        ('ftpIncomeBaseInfoDetail', '资金管理-融资管理-ftp收益表详情', 0, (select id from bifrost_menu where code = 'ftpIncome'), 'POST',
         '/ftp/income/base/info/detail', 2),
        ('ftpIncomeDetailRecordList', '资金管理-融资管理-ftp收益记录表列表', 0, (select id from bifrost_menu where code = 'ftpIncome'), 'POST',
         '/ftp/income/detail/record/list', 2),
        ('ftpIncomeOrganizationList', '资金管理-融资管理-ftp收益记录获取融资机构', 0, (select id from bifrost_menu where code = 'ftpIncome'), 'POST',
         '/ftp/income/organization/list', 2);

insert into bifrost_function (code, name, sort_no, menu_id, method, path, type)
values  ('ftpIncomeBaseInfoCount', '资金管理-融资管理-ftp收益表统计', 0, (select id from bifrost_menu where code = 'ftpIncome'), 'POST',
         '/ftp/income/base/info/count', 2);




