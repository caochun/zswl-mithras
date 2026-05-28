INSERT INTO `bifrost_function` (`code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`) VALUES ('appContractSignFileRecord', '获取文件后信息记录', 0, 783, NULL, NULL, NULL, 'POST', '/file/upload/record', 2, NULL);
INSERT INTO `bifrost_function` (`code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`) VALUES ('appContractSignFilePresigned', '获取文件预上传地址', 0, 783, NULL, NULL, NULL, 'POST', '/file/upload/presigned', 2, NULL);
INSERT INTO `bifrost_function` (`code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`) VALUES ('appclientfrequentlist', 'APP我的客户-拜访客户数量下拉框', 0, 783, NULL, NULL, NULL, 'POST', '/app/client/frequent/list', 2, NULL);

-- 流动性优化
INSERT INTO `bifrost_function` (`code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`)
VALUES
    ('liquidityRentIncomeIndexDownload', '租金流入导出', 0, 481, NULL, NULL, NULL, 'POST', '/index/download', 2, NULL),
    ('liquidityRepayIndexDownload', '还本付息导出', 0, 481, NULL, NULL, NULL, 'POST', '/index/download', 2, NULL),
    ('liquidityManageRentIncome', '租金流入', 0, 481, NULL, NULL, NULL, 'POST', '/liquidity/manage/rent/income', 2, NULL),
    ('liquidityManageRepay', '还本付息', 0, 481, NULL, NULL, NULL, 'POST', '/liquidity/manage/repay', 2, NULL);

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