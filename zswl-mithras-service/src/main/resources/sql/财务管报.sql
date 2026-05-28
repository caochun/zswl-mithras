
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
VALUES ('投放收益率', 'http://10.158.11.178/page/qc472f9631aa643f1a776a68', 'newCaiwu', 100, '财务报表' );

INSERT INTO `management_report` (`report_name`, `report_url`, `report_type`, `sort_num`, report_type_name )
VALUES ('项目情况财务表', 'http://10.158.11.178/page/mff08ad604b89476586ffc1a', 'newCaiwu', 200, '财务报表' );
INSERT INTO `management_report` (`report_name`, `report_url`, `report_type`, `sort_num`, report_type_name )
VALUES ('当月还款情况表', 'http://10.158.11.178/page/q8af8419a76dc484db9cacd4', 'newCaiwu', 300, '财务报表' );
INSERT INTO `management_report` (`report_name`, `report_url`, `report_type`, `sort_num`, report_type_name )
VALUES ('逾期情况表', 'http://10.158.11.178/page/g394fa365aad346ab9c5ddee', 'newCaiwu', 400 , '财务报表');
INSERT INTO `management_report` (`report_name`, `report_url`, `report_type`, `sort_num`, report_type_name )
VALUES ('租金回笼表', 'http://10.158.11.178/page/t9be39521ca63498ebeef764', 'newCaiwu', 500, '财务报表' );

update bifrost_system_config set config_value = '{
	"user": {
		"cailili": "yunying",
		"tianfeiyan": "yunying",
		"liuxuhao": "yunying",
		"chenliu": "yunying",
		"gexiaoqing": "yunying",
		"admin": "yunying,capital,finance,riskcontrol",
		"readonly": "yunying,capital,finance,caiwu,riskcontrol,funds,financebottom,newCaiwu",
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
		"22": "capital,yunying,finance,funds",
		"24": "yunying"
	}
}' where config_key = 'managementReportPermission';

update bifrost_menu set type = 0 where code = 'reportinternal';

insert into bifrost_menu(code, level, sort_no, path, name)
values ('reportFinancial', 3, 0, '/report/financial', '财务管报');
insert into bifrost_custom_tree_menu_ref(custom_tree_id, menu_id)
select a.id, b.id
from bifrost_custom_tree a,
     bifrost_menu b
where a.name = '管理报表'
  and b.name = '财务管报';

-- update management_report set report_type_name = '运营报表' where report_type = 'yunying';
-- update management_report set report_type_name = '资金报表' where report_type = 'funds';
-- update management_report set report_type_name = '融资报表' where report_type = 'capital';
-- update management_report set report_type_name = '风控报表' where report_type = 'riskcontrol';
-- update management_report set report_type_name = '财务报表' where report_type = 'caiwu';
-- update management_report set report_type_name = '融资管理报表' where report_type = 'finance';

INSERT INTO bifrost_function (code, name, sort_no, menu_id, create_by, update_by, en_name, method, path, type, group_id)
VALUES ('managementReportGroupList', '管理报表分组列表', 0, (select id from bifrost_menu where code = 'reportFinancial'), null, null, null, 'GET',
        '/management/report/group/list', 2, null);








update bifrost_system_config set config_value = '{
	"user": {
		"cailili": "yunying",
		"tianfeiyan": "yunying",
		"liuxuhao": "yunying",
		"chenliu": "yunying",
		"gexiaoqing": "yunying",
		"admin": "yunying,capital,finance,riskcontrol",
		"readonly": "yunying,capital,finance,caiwu,riskcontrol,funds,financebottom,newCaiwu,caiwuAndAssetManagement",
		"dushunshun": "yunying,capital,finance,caiwu,riskcontrol",
		"luxiaojia": "yunying,capital,finance,caiwu,riskcontrol,funds",
		"luye": "yunying,capital,finance,caiwu,riskcontrol,funds",
		"kongyan": "yunying,capital,finance,caiwu,riskcontrol",
		"yaohongfeng": "yunying,capital,finance,caiwu",
		"wangjun": "yunying,capital,finance,caiwu",
		"zhoufei": "yunying,capital",
		"hedong": "yunying,capital",
        "wangshanshan": "caiwuAndAssetManagement",
        "wangzhipeng": "caiwuAndAssetManagement",
        "chenhujun": "caiwuAndAssetManagement",
		"wujie": "yuying,capital"
	},
	"org": {
		"2": "yunying,capital,finance",
		"3": "riskcontrol",
		"13": "capital,finance,caiwu,financebottom,newCaiwu,caiwuAndAssetManagement",
		"22": "capital,yunying,finance,funds",
		"24": "yunying"
	}
}' where config_key = 'managementReportPermission';

update management_report set report_type = 'caiwuAndAssetManagement' where id in (51, 52, 53);

alter table contract_receipt add column org_scale varchar (50) default null comment '借据创建时取自客户管理的【企业规模】';
alter table contract_receipt_lib add column org_scale varchar (50) default null comment '借据创建时取自客户管理的【企业规模】';

alter table contract_receipt add column xirr double default null comment 'xirr';
alter table contract_receipt_lib add column xirr double default null comment 'xirr';

update contract_receipt ct
set ct.org_scale = (select org_scale
                    from corp_commerce_info
                    where client_id = (select client_id
                                       from contract_base_info
                                       where id = ct.contract_id));



