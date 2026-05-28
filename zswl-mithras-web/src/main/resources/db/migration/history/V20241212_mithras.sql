
alter table management_report add column report_type_name varchar (50) default null comment '管报类型名称';
-- 财务报表低表数据
INSERT INTO `management_report` (`report_name`, `report_url`, `report_type`, `sort_num`, report_type_name)
VALUES ('客户主题表', 'http://10.158.11.178/page/r72c086546c154b46a06066f', 'financebottom', 100, '财务底表');

INSERT INTO `management_report` (`report_name`, `report_url`, `report_type`, `sort_num`, report_type_name)
VALUES ('借据主题表', 'http://10.158.11.178/page/nae0524c7a26b4d5792b47f9', 'financebottom', 200, '财务底表' );

INSERT INTO `management_report` (`report_name`, `report_url`, `report_type`, `sort_num`, report_type_name )
VALUES ('现金流主题表', 'http://10.158.11.178/page/p2db3939e1b884bf8b038ce3', 'financebottom', 300, '财务底表' );

INSERT INTO `management_report` (`report_name`, `report_url`, `report_type`, `sort_num`, report_type_name )
VALUES ('核销主题表', 'http://10.158.11.178/page/efeadda61d6904e63b6f210c', 'financebottom', 400, '财务底表' );

INSERT INTO `management_report` (`report_name`, `report_url`, `report_type`, `sort_num`, report_type_name )
VALUES ('财务主题表', 'http://10.158.11.178/page/na56e85b78a7c45d59176eb5', 'financebottom', 500, '财务底表' );

-- 财务报表数据

INSERT INTO `management_report` (`report_name`, `report_url`, `report_type`, `sort_num`, report_type_name )
VALUES ('投放收益率', 'http://10.158.11.178/page/qc472f9631aa643f1a776a68', 'caiwu', 100, '财务报表' );

INSERT INTO `management_report` (`report_name`, `report_url`, `report_type`, `sort_num`, report_type_name )
VALUES ('项目情况财务表', 'http://10.158.11.178/page/mff08ad604b89476586ffc1a', 'caiwu', 200, '财务报表' );
INSERT INTO `management_report` (`report_name`, `report_url`, `report_type`, `sort_num`, report_type_name )
VALUES ('当月还款情况表', 'http://10.158.11.178/page/q8af8419a76dc484db9cacd4', 'caiwu', 300, '财务报表' );
INSERT INTO `management_report` (`report_name`, `report_url`, `report_type`, `sort_num`, report_type_name )
VALUES ('逾期情况表', 'http://10.158.11.178/page/g394fa365aad346ab9c5ddee', 'caiwu', 400 , '财务报表');
INSERT INTO `management_report` (`report_name`, `report_url`, `report_type`, `sort_num`, report_type_name )
VALUES ('租金回笼表', 'http://10.158.11.178/page/t9be39521ca63498ebeef764', 'caiwu', 500, '财务报表' );

update bifrost_system_config set config_value = '{
	"user": {
		"cailili": "yunying",
		"tianfeiyan": "yunying",
		"liuxuhao": "yunying",
		"chenliu": "yunying",
		"gexiaoqing": "yunying",
		"admin": "yunying,capital,finance,riskcontrol",
		"readonly": "yunying,capital,finance,caiwu,riskcontrol,funds,financebottom",
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
		"13": "capital,finance,caiwu,financebottom",
		"22": "capital,yunying,finance,funds",
		"24": "yunying"
	}
}' where config_key = 'managementReportPermission';



update management_report set report_type_name = '运营报表' where report_type = 'yunying';
update management_report set report_type_name = '资金报表' where report_type = 'funds';
update management_report set report_type_name = '融资报表' where report_type = 'capital';
update management_report set report_type_name = '风控报表' where report_type = 'riskcontrol';
update management_report set report_type_name = '财务报表' where report_type = 'caiwu';
update management_report set report_type_name = '融资管理报表' where report_type = 'finance';

INSERT INTO bifrost_function (code, name, sort_no, menu_id, create_by, update_by, en_name, method, path, type, group_id)
VALUES ('managementReportGroupList', '管理报表分组列表', 0, 218, null, null, null, 'GET',
        '/management/report/group/list', 2, null);


-- 保融接口调用记录表
CREATE TABLE `fund_financial_system_call_record` (
      `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'Id',
      `date` datetime NOT NULL COMMENT '数据时点',
      `batch_number` varchar(255) NOT NULL COMMENT '批次号',
      `status` varchar(255) NOT NULL COMMENT '状态',
      `type` varchar(255) NOT NULL COMMENT '推送类型',
      `count` int(10) NOT NULL COMMENT '推送数量',
      `query` json DEFAULT NULL COMMENT '请求参数',
      `result` json DEFAULT NULL COMMENT '返回体',
      `system_error_info` json DEFAULT NULL COMMENT '系统内校验的错误信息',
      `create_by` bigint(20) DEFAULT NULL COMMENT '创建人、发起人',
      `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
      `update_by` bigint(20) DEFAULT NULL COMMENT '最后更新人id',
      `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
      `deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '逻辑删除，0-未删除，1-已删除',
      PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8mb4;


