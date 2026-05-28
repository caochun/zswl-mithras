update management_report set report_type_name = '运营报表' where id in (3, 4, 13, 14, 16, 40, 43, 55, 56, 57);
update management_report set report_type_name = '风控报表' where id in (37, 58);
update management_report set report_type_name = '资金报表' where id in (19, 20);
update management_report set report_type_name = '财务报表' where id in (49, 50, 51, 52, 53, 38, 1, 2, 21, 5, 6);
update management_report set report_type_name = '财务底表' where id in (44, 45, 46, 47, 48);
update management_report set report_type_name = '监管报送' where id in (7, 8, 9, 10, 28, 29, 30, 31, 32, 33, 34, 35);
update management_report set report_type_name = '已弃用' where id in (22, 25, 26, 27);
update bifrost_menu set type = 1 where id = 739;
update bifrost_menu set id = -218 where id = 218;

-- 同业对比分析
INSERT INTO `bifrost_function` (`code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`)
VALUES
    ('peercomparisonconfig', '同业对比分析-用户对标企业配置', 0, 727, NULL, NULL, NULL, 'POST', '/peer/comparison/config', 1, NULL),
    ('peercomparisonlist', '同业对比分析-明细数据集合', 0, 727, NULL, NULL, NULL, 'POST', '/peer/comparison/list', 1, NULL),
    ('peercomparisonrefresh', '同业对比分析-财年数据刷新', 0, 727, NULL, NULL, NULL, 'POST', '/peer/comparison/refresh', 1, NULL),
    ('peercomparisonenterpriseState', '同业对比分析-企业状态', 0, 727, NULL, NULL, NULL, 'POST', '/peer/comparison/enterpriseState', 1, NULL),
    ('peercomparisonlatestYear', '同业对比分析-获取最新财年', 0, 727, NULL, NULL, NULL, 'POST', '/peer/comparison/latestYear', 1, NULL),
    ('peercomparison', '同业对比分析-同业分析比较', 0, 727, NULL, NULL, NULL, 'POST', '/peer/comparison', 1, NULL);

INSERT INTO `dashboard_config` (`dashboard_key`, `dashboard_display`, `order_num`)
VALUES
    ('IndustryIndexComparison', '行业指标对比', 600);

INSERT INTO `dashboard_authority_config` (`user_id`, `dashboard_key`)
VALUES
    (125, 'IndustryIndexComparison');
-- 同业对比分析

-- 初始化公开信信息拷贝状态
-- 上线的时候之前的付款申请统一不管，直接初始化为1
UPDATE payment_base_info SET is_init_public_info = 1 WHERE 1 = 1;
UPDATE payment_base_info_lib SET is_init_public_info = 1 WHERE 1 = 1;
INSERT INTO bifrost_function (code, name, menu_id, method, path, type)
VALUES('publicInfoCheck','公开信息-检查是否存在客户没有维护公开信息','15','POST','/public/info/check','2');

INSERT INTO `bifrost_function` (`code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`) VALUES ('kpiprojectdistributionprev', '项目分配-查看上个版本', 0, 288, NULL, NULL, NULL, 'POST', '/kpi/projectdistribution/prev', 1, NULL);




-- 资金报表低表数据
INSERT INTO `management_report` (`report_name`, `report_url`, `report_type`, `sort_num`, report_type_name, `report_source`, `report_key`)
VALUES ('现金流表（资金）', 'http://10.158.11.178/page/k795cfe8508494276ad246af', 'financebottom', 100, '资金底表', 'GUAN_YUAN', 'REPORT_54');

INSERT INTO `management_report` (`report_name`, `report_url`, `report_type`, `sort_num`, report_type_name, `report_source`, `report_key`)
VALUES ('核销表（资金）', 'http://10.158.11.178/page/wb157baf8009845b6964bb4b', 'financebottom', 200, '资金底表', 'GUAN_YUAN', 'REPORT_55' );

INSERT INTO `management_report` (`report_name`, `report_url`, `report_type`, `sort_num`, report_type_name, `report_source`, `report_key`)
VALUES ('授信表（资金）', 'http://10.158.11.178/page/je0803b3ddcf14cdc9a90eed', 'financebottom', 300, '资金底表', 'GUAN_YUAN', 'REPORT_56');

INSERT INTO `management_report` (`report_name`, `report_url`, `report_type`, `sort_num`, report_type_name, `report_source`, `report_key`)
VALUES ('融资合同表(资金)', 'http://10.158.11.178/page/e6fc3c02c64bf40faa2a7bf1', 'financebottom', 400, '资金底表', 'GUAN_YUAN', 'REPORT_57' );


-- 资金报表数据
INSERT INTO `management_report` (`report_name`, `report_url`, `report_type`, `sort_num`, report_type_name, `report_source`, `report_key` )
VALUES ('授信汇总表', 'http://10.158.11.178/page/i2e934f3b30a34224a8203ba', 'finance', 500, '资金报表', 'GUAN_YUAN', 'REPORT_58' );
INSERT INTO `management_report` (`report_name`, `report_url`, `report_type`, `sort_num`, report_type_name, `report_source`, `report_key` )
VALUES ('还本付息明细表', 'http://10.158.11.178/page/ue3da83d90b7c4d30997a008', 'finance', 600 , '资金报表', 'GUAN_YUAN', 'REPORT_59');
INSERT INTO `management_report` (`report_name`, `report_url`, `report_type`, `sort_num`, report_type_name, `report_source`, `report_key` )
VALUES ('融资合同汇总表', 'http://10.158.11.178/page/u87c175f747524ecb922da13', 'finance', 700, '资金报表', 'GUAN_YUAN', 'REPORT_60' );



update bifrost_system_config set config_value = '{
	"user": {
		"cailili": "yunying",
		"tianfeiyan": "yunying",
		"liuxuhao": "yunying",
		"chenliu": "yunying",
		"gexiaoqing": "yunying",
		"admin": "yunying,capital,finance,riskcontrol",
		"readonly": "yunying,capital,finance,caiwu,riskcontrol,funds,financebottom,newCaiwu,fundbottom",
		"dushunshun": "yunying,capital,finance,caiwu,riskcontrol",
		"luxiaojia": "yunying,capital,finance,caiwu,riskcontrol,funds",
		"luye": "yunying,capital,finance,caiwu,riskcontrol,funds",
		"kongyan": "yunying,capital,finance,caiwu,riskcontrol",
		"yaohongfeng": "yunying,capital,finance,caiwu",
		"wangjun": "yunying,capital,finance,caiwu",
		"zhoufei": "yunying,capital",
		"hedong": "yunying,capital",
		"wujie": "yuying,capital"
	},
	"org": {
		"2": "yunying,capital,finance",
		"3": "riskcontrol",
		"13": "capital,finance,caiwu,financebottom,newCaiwu",
		"22": "capital,yunying,finance,funds,financebottom",
		"24": "yunying"
	}
}' where config_key = 'managementReportPermission';
