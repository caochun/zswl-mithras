

-- ============= 来自文件：20250206_linlili_001_dml.sql ============= 

-- 间融浮动利率存量数据处理
update fund_financing_plan ffp set lpr_arrange_mode = 'MONTH_DECEMBER',lpr_adjustment_day = 'CARRY_INTEREST_DAY'
where ffp.interest_rate_type = 'FLOAT' and ffp.lpr_adjustment_day in ('NATURE_DAY','INTEREST_SETTLE_DAY')
and exists (select 1 from fund_financing_base_info t where t.id = ffp.financing_id );


-- ============= 来自文件：20250206_liushaokang_001_dml.sql ============= 

-- 删除系统配置表中的资产五级分类特殊客户
delete from bifrost_system_config where config_key = 'express_clients';

-- 系统配置表中插入资产五级分类特殊客户
INSERT INTO
bifrost_system_config (config_key, config_value, created_by, updated_by, description, status, `type`)
VALUES('express_clients', '["广东禄宇航运有限公司","福建鑫宏兴航运有限公司","上海鼎衡航运科技有限公司"]', 'admin', 'admin', '资产五级分类特殊客户列表', 1, 'Json');

-- 删除资产五级分类风险因子表中新版数据
delete from asset_classify_client_risk_factor_template where risk_factor_type IN ('NON_SHIPPING','OPERATION_LEASE');

-- 资产五级分类风险因子表中插入新版数据
INSERT INTO asset_classify_client_risk_factor_template (type, risk_factor, has_risk, risk_factor_type)
VALUES
	('租金回收', '租金逾期30天（含）以内', 0, 'NON_SHIPPING'),
	('租金回收', '租金逾期30天以上，90天（含）以内', 0, 'NON_SHIPPING'),
	('租金回收', '租金逾期90天以上，180天（含）以内', 0, 'NON_SHIPPING'),
	('租金回收', '租金逾期180天以上，360天（含）以内', 0, 'NON_SHIPPING'),
	('租金回收', '租金逾期360天以上', 0, 'NON_SHIPPING'),
	('租金回收', '租赁期内已完成展延期', 0, 'NON_SHIPPING'),
	('租金回收', '展延期流程中', 0, 'NON_SHIPPING'),
	('外部影响', '宏观经济变化、行业周期、国家宏观调控政策等对债务人经营已经产生明显不利影响，债务人的偿债能力出现明显下滑', 0, 'NON_SHIPPING'),
	('外部影响', '债务人在其他金融机构发生实质逾期（包括但不限于贷款逾期或划为不良资产或债务重组）且无充分理由的', 0, 'NON_SHIPPING'),
	('外部影响', '债务人有重大负面信息（包括发生责任事故/法律纠纷/违规行为等导致重大赔偿，或因自然灾害遭受重大损失等），已影响对债务人的资质评判或不利于到期债务及利息的偿还', 0, 'NON_SHIPPING'),
	('外部影响', '债务人(或实质性担保人）/债务人的法定代表人/实际控制人(或实质性担保人的法定代表人/实际控制人)涉及重大案件，对债务人的正常经营活动造成重大影响', 0, 'NON_SHIPPING'),
	('外部影响', '债务人遭受重大自然灾害或意外事故，损失巨大且不能获得保险补偿，确实无力偿还部分或全部租金', 0, 'NON_SHIPPING'),
	('债务人', '债务人关键财务数据较上一年同期出现较大幅度下降或出现流动性不足，且该不利变化对企业经营和偿债能力造成直接的重大负面影响', 0, 'NON_SHIPPING'),
	('债务人', '债务人的主要领导人（包括实际控制人）、核心管理层、主要股东、核心子公司等发生了重大不利变化，对偿还到期债务及利息产生明显不利影响', 0, 'NON_SHIPPING'),
	('债务人', '债务人重组、改制、分立、兼并等组织形式改变，导致业务性质及经营范围发生重大变化，对到期债务及利息的偿还可能产生不利的影响', 0, 'NON_SHIPPING'),
	('债务人', '债务人已投资项目或在建工程项目出现了重大不利于到期债务及利息偿付的调整（如已投项目未达到预期、建设工期明显延长、概算金额大幅调整的）', 0, 'NON_SHIPPING'),
	('债务人', '债务人有重大负面信息（包括发生责任事故/法律纠纷/违规行为等导致重大赔偿，或因自然灾害遭受重大损失等），已影响对债务人的资质评判或不利于到期债务及利息的偿还', 0, 'NON_SHIPPING'),
	('债务人', '债务人连续2期（债务人或保证人编制报表的正常频率）无正当理由拒绝提供其财务报表及租后管理制度要求的必要资料', 0, 'NON_SHIPPING'),
	('债务人', '债务人因经营出现重大亏损或发生其他重大负面信息，已严重影响了到期债务及利息的足额偿付', 0, 'NON_SHIPPING'),
	('债务人', '债务人的会计师出具的审计报告意见，为无法表示意见或否定意见的', 0, 'NON_SHIPPING'),
	('债务人', '债务人主体为境内外上市公司，股票被作为ST、退市或相同情况处理的', 0, 'NON_SHIPPING'),
	('债务人', '债务人利用兼并、重组、分立等形式恶意逃废债务且到期债务已经逾期的', 0, 'NON_SHIPPING'),
	('债务人', '出租人已向法院提起诉讼追偿', 0, 'NON_SHIPPING'),
	('债务人', '债务人出现全面停产或半停产状况，租金支付存在重大困难', 0, 'NON_SHIPPING'),
	('债务人', '我司直租项目处于停建状态且开工可能性极低', 0, 'NON_SHIPPING'),
	('债务人', '债务人的债务已经重组，但仍然不能足额偿还，且还款状况进一步恶化', 0, 'NON_SHIPPING'),
	('债务人', '债务人已经依法宣告破产、关闭、解散，或终止法人资格，经法定清偿后，仍不能还清租赁本息', 0, 'NON_SHIPPING'),
	('债务人', '债务人虽未依法终止法人资格，但生产经营活动已经停止，名存实亡，复工无望，经确认无法还清租赁本息', 0, 'NON_SHIPPING'),
	('债务人', '债务人（债务人如为自然人）死亡，或依照相关法律的规定，宣告失踪或死亡，经法定程序对其财产与遗产进行清偿并对担保人进行追偿后，仍不能回收的资产', 0, 'NON_SHIPPING'),
	('债务人', '诉诸法律，经法院对债务人强制执行，债务人无财产可执行，法院裁定终结后，仍无法还清租赁本息', 0, 'NON_SHIPPING'),
	('租赁物', '因租赁物已经处于长期闲置状态或存在灭失、重大减值风险，导致债务人还款能力显著降低的，已影响风险防范的有效性', 0, 'NON_SHIPPING'),
	('租赁物', '债务人不得不寻求变卖核心资产、处置租赁物或拍卖抵质押品等措施，用来偿还到期债务及利息的', 0, 'NON_SHIPPING'),
	('租赁物', '租赁物被债务人处置或设置了其他权益或实施了重复融资行为，已严重危及租赁物安全', 0, 'NON_SHIPPING'),
	('担保人', '实质担保人连续2期（债务人或保证人编制报表的正常频率）无正当理由拒绝提供其财务报表及租后管理制度要求的必要资料', 0, 'NON_SHIPPING'),
	('担保人', '实质担保主体为境内外上市公司，股票被作为ST、退市或相同情况处理的', 0, 'NON_SHIPPING'),
	('担保人', '担保人利用兼并、重组、分立等形式恶意逃废债务且到期债务已经逾期的', 0, 'NON_SHIPPING'),
	('担保人', '实质性担保人已经依法宣告破产、关闭、解散，或终止法人资格，经法定清偿后，仍不能还清租赁本息', 0, 'NON_SHIPPING'),
	('担保人', '承担主要还款来源的实质性担保人因财务状况出现严重问题，造成其担保能力严重下降或抵/质押品毁损、灭失致使担保能力严重下降', 0, 'NON_SHIPPING'),
	('担保人', '实质性担保人由于亏损、停产、虽然未破产也未吊销执照，但企业早已关停或名存实亡，担保能力丧失', 0, 'NON_SHIPPING'),
	('担保人', '诉诸法律，经法院对实质性担保人强制执行，实质性担保人无财产可执行，法院裁定终结后，仍无法还清租赁本息', 0, 'NON_SHIPPING'),
	('其他', '', 0, 'NON_SHIPPING'),
	('承租人', '承租人还款意愿差，不与出租人积极合作', 0, 'OPERATION_LEASE'),
	('承租人', '承租人恶意拖欠租金', 0, 'OPERATION_LEASE'),
	('租赁物', '设备全年闲置期超过运行期，且设备非处于技改和维修状态', 0, 'OPERATION_LEASE'),
	('租赁物', '租赁资产市场价值下跌，并可能对租金收入水平造成重大不利影响', 0, 'OPERATION_LEASE'),
	('租赁物', '租赁资产外部经营环境发生明显恶化，市场需求有下降趋势，且对租赁资产产生重大不利影响', 0, 'OPERATION_LEASE'),
	('租赁物', '租赁资产所处的经营环境恶化，需求大幅萎缩，不容易出租', 0, 'OPERATION_LEASE'),
	('租赁物', '租赁物产品与技术陈旧，不适应市场需求', 0, 'OPERATION_LEASE'),
	('租赁物', '租赁资产经常处于闲置状态，且设备非处于技改和维修状态', 0, 'OPERATION_LEASE'),
	('租赁物', '经营租赁物市场价值已远低于账面净值', 0, 'OPERATION_LEASE'),
	('租赁物', '经营租赁物损坏，维修费用高昂，又无法获得赔偿', 0, 'OPERATION_LEASE'),
	('租赁物', '承租人恶意霸占、转移或藏匿租赁物，出租人不得不通过司法程序进行追索', 0, 'OPERATION_LEASE'),
	('租赁物', '经营租赁物已没有市场价值，无法再对外出租，也无法再产生经济收益', 0, 'OPERATION_LEASE'),
	('租赁物', '在采取一切可能的措施和必要的法律程序后，仍无法收回的经营租赁资产', 0, 'OPERATION_LEASE');


-- ============= 来自文件：20250206_hengtx_001_dml.sql ============= 

set @menu_id = (select menu_id from bifrost_function where path = '/payment/check/apply/amount');
set @function_id = (select id from bifrost_function where path = '/payment/check/apply/amount');
delete from bifrost_role_menu_function where function_id = @function_id and menu_id =@menu_id;
INSERT INTO bifrost_role_menu_function (role_id, menu_id, function_id, custom_tree_id)
SELECT id,@menu_id,@function_id,8
FROM bifrost_role;


-- ============= 来自文件：20250206_linlili_002_dml.sql ============= 

delete from bifrost_org_menu_function where function_id = (select id from bifrost_function where code ='fundTransferaccountcurrentdaily');
delete from bifrost_role_menu_function where function_id = (select id from bifrost_function where code ='fundTransferaccountcurrentdaily');
delete from bifrost_function where code ='fundTransferaccountcurrentdaily';


INSERT INTO bifrost_function(code,name,sort_no,menu_id,method,path,type)
SELECT 'fundTransferaccountcurrentdaily','监管户待转资金当日账户情况展示',0,b.id,'POST','/fundTransfer/account/current/daily',2
FROM bifrost_menu b where b.name = '流动性管理' ;

INSERT INTO bifrost_org_menu_function(org_id, menu_id, function_id, custom_tree_id)
SELECT bomf.org_id, bomf.menu_id, t2.id, bomf.custom_tree_id
FROM bifrost_org_menu_function bomf,
     bifrost_function t,
     bifrost_function t2
where bomf.function_id = t.id and t.code ='fundTransfercurrentdaily' and t2.code ='fundTransferaccountcurrentdaily';

INSERT INTO bifrost_role_menu_function(role_id, menu_id, function_id, custom_tree_id)
SELECT bomf.role_id, bomf.menu_id, t2.id, bomf.custom_tree_id
FROM bifrost_role_menu_function bomf,
     bifrost_function t,
     bifrost_function t2
where bomf.function_id = t.id and t.code ='fundTransfercurrentdaily' and t2.code ='fundTransferaccountcurrentdaily';


-- ============= 来自文件：20260206_luyujie_001_dml.sql ============= 

-- 合同起租-系统自动发起
delete from flow_node_cc_config where flow_key = 'ContractStartRentAutoFlow';
insert into flow_node_cc_config (flow_key,node_key,object_type,object_ids,create_by,update_by) values('ContractStartRentAutoFlow','userTask_financialmanager','user','cailili,renhezhong',3,3);

-- 合同起租
delete from flow_node_cc_config where flow_key = 'ContractStartRentFlow';
insert into flow_node_cc_config (flow_key,node_key,object_type,object_ids,create_by,update_by) values('ContractStartRentFlow','userTask_financeManager','user','cailili,renhezhong',3,3);


-- ============= 来自文件：20260206_luyujie_001_dml.sql ============= 

-- 新增功能接口-客户批量移交
delete from bifrost_org_menu_function where function_id = (select id from bifrost_function where code ='clientnewTransfermodifyBatch');
delete from bifrost_role_menu_function where function_id = (select id from bifrost_function where code ='clientnewTransfermodifyBatch');
delete from bifrost_function where code = 'clientnewTransfermodifyBatch';
-- 客户管理
set @menu_id = (select id from bifrost_menu where code = 'QX0104');
INSERT INTO bifrost_function ( code, name, sort_no, menu_id, method, path, type)
VALUES ('clientnewTransfermodifyBatch', '新的批量提交转移编辑用户负责的客户', 0, @menu_id , 'POST',
        '/client/newTransfer/modifyBatch', 1);

set @custom_tree_id = (select custom_tree_id from bifrost_custom_tree_menu_ref where menu_id =@menu_id);
-- 部门添加接口(浙江浙商融资租赁有限公司、浙江业务部、管理员、浙商金控、信息科技部)
insert into bifrost_org_menu_function (org_id, menu_id, function_id, create_by, update_by, custom_tree_id)
values  ((select id from bifrost_org where code ='ZSZL'), @menu_id , (select id from bifrost_function where code ='clientnewTransfermodifyBatch'), null, null, @custom_tree_id),
        ((select id from bifrost_org where code ='JCSSYWB'), @menu_id , (select id from bifrost_function where code ='clientnewTransfermodifyBatch'), null, null, @custom_tree_id),
        ((select id from bifrost_org where code ='ADMIN'), @menu_id , (select id from bifrost_function where code ='clientnewTransfermodifyBatch'), null, null, @custom_tree_id),
        ((select id from bifrost_org where code ='ZSJK'), @menu_id , (select id from bifrost_function where code ='clientnewTransfermodifyBatch'), null, null, @custom_tree_id),
        ((select id from bifrost_org where code ='XXKJB'), @menu_id , (select id from bifrost_function where code ='clientnewTransfermodifyBatch'), null, null, @custom_tree_id);
-- 角色添加接口（项目经理、超级管理员）（项目经理额外加了我收到的菜单）
insert into bifrost_role_menu_function (role_id, menu_id, function_id, create_by, update_by, custom_tree_id)
values  ( (select id from bifrost_role where code ='XMJL'),  @menu_id, (select id from bifrost_function where code ='clientnewTransfermodifyBatch'), null, null, @custom_tree_id),
        ( (select id from bifrost_role where code ='ADMIN_SUPERADMIN'),  @menu_id, (select id from bifrost_function where code ='clientnewTransfermodifyBatch'), null, null, @custom_tree_id),
        ( (select id from bifrost_role where code ='XMJL'),  (select id from bifrost_menu where code = 'QX0108'), (select id from bifrost_function where code ='clientnewTransfermodifyBatch'), null, null, @custom_tree_id);


-- ============= 来自文件：20250206_hengtx_001_dml.sql ============= 

-- 新增功能接口
delete from bifrost_org_menu_function where function_id = (select id from bifrost_function where code ='materialsprojreviewcomments');
delete from bifrost_role_menu_function where function_id = (select id from bifrost_function where code ='materialsprojreviewcomments');
delete from bifrost_function where code = 'materialsprojreviewcomments';
delete from bifrost_org_menu_function where function_id = (select id from bifrost_function where code ='projreviewmaterialcommentscheck');
delete from bifrost_role_menu_function where function_id = (select id from bifrost_function where code ='projreviewmaterialcommentscheck');
delete from bifrost_function where code = 'projreviewmaterialcommentscheck';

set @menu_id = (select id from bifrost_menu where code = 'QX0110');
INSERT INTO bifrost_function ( code, name, sort_no, menu_id, method, path, type)
VALUES ('materialsprojreviewcomments', '项目评审资料清单-审核意见-修改', 0, @menu_id , 'POST',
        '/materials/proj/review/comments', 1),
       ('projreviewmaterialcommentscheck', '校验材料评审是否提交', 0, @menu_id , 'POST',
        '/proj/review/material/comments/check', 1);;
set @org_id = (select id from bifrost_org where code ='FXGLB_YWPS');
set @custom_tree_id = (select custom_tree_id from bifrost_custom_tree_menu_ref where menu_id =@menu_id);
-- 部门添加接口
insert into bifrost_org_menu_function (org_id, menu_id, function_id, create_by, update_by, custom_tree_id)
values  (@org_id, @menu_id , (select id from bifrost_function where code ='materialsprojreviewcomments'), null, null, @custom_tree_id),
        (@org_id, @menu_id , (select id from bifrost_function where code ='projreviewmaterialcommentscheck'), null, null, @custom_tree_id);
set @role_id = (select id from bifrost_role where code ='FKJL');
-- 角色添加接口
insert into bifrost_role_menu_function (role_id, menu_id, function_id, create_by, update_by, custom_tree_id)
values  ( @role_id,  @menu_id, (select id from bifrost_function where code ='materialsprojreviewcomments'), null, null, @custom_tree_id),
        ( @role_id,  @menu_id, (select id from bifrost_function where code ='projreviewmaterialcommentscheck'), null, null, @custom_tree_id);


-- ============= 来自文件：20250206_hengtx_001_dml.sql ============= 

delete from general_dictionary where code = 'Jurydirector';
delete from gruul_user_org_job where job_code = 'Jurydirector';
INSERT INTO general_dictionary ( dict_key, dict_desc, code, display, sort) VALUES ('job', '岗位类型', 'Jurydirector', '评审会主任', 10);
INSERT INTO gruul_user_org_job (user_id, org_id, job_code) VALUES ((select id from bifrost_user where account ='liyan'), 5, 'Jurydirector');


-- ============= 来自文件：20250206_hengtx_001_dml.sql ============= 

-- 删除重复生成的计划
delete
from common_process_prepare
where id in (select id
             from (select id
                   from common_process_prepare
                   where common_process_prepare.status = 'WAITING_PEND'
                     and id not in (SELECT MIN(id) AS min_id
                                    FROM common_process_prepare
                                    where common_process_prepare.status = 'WAITING_PEND'
                                    GROUP BY business_id)) as a);
-- 待更新
update new_after_lease_check_plan_client set next_check_flag = 0 where id in(
    select id from (
                       select max(plan_id),c.id from (
                                                         select * from new_after_lease_check_plan_client where plan_id in (
                                                             select id from new_after_lease_check_plan_base where plan_status ='FINISH' and plan_type ='COMMONLY') and next_deadline is not null) c

                       where c.client_id not in (

                           select distinct client_id from new_after_lease_check_plan_client where client_id in (
                               select client_id from new_after_lease_check_plan_client where plan_id in (
                                   select max(plan_id) from (
                                                                select * from new_after_lease_check_plan_client where plan_id in(
                                                                    select id from new_after_lease_check_plan_base where plan_status ='FINISH' and plan_type ='COMMONLY') and next_deadline is not null) c group by client_id ))
                                                                                              and approval_status  in('UN_SUBMIT','UNDER_APPROVAL') and plan_id not in(1, 2, 3, 4, 981)
                       )
                       group by client_id) d);


-- ============= 来自文件：20250206_huliangdong_001_dml.sql ============= 



-- 模板类型
delete from file_authentication_config where file_type= '保证金冲抵（退还）申请模版';
INSERT INTO file_authentication_config (file_name, file_type, owner_type, owner_post, owner_id, create_by, update_by, create_time, update_time)
VALUES(NULL, '保证金冲抵（退还）申请模版', 1, 'admin', NULL, 125, 125, '2025-10-14 09:17:43', '2025-10-14 09:17:43');

-- 需要手动导入模板文件 ：file_template  附件表 ： materials_list



set @menuID01 = (select id from bifrost_menu where code = 'QX0118');
-- 授权

set @funCode01 = 'contractdepostcheck';
set @funCode02 = 'contractdepostdepostInfo';
set @funCode03 = 'contractdepostrentList';
set @funCode04 = 'contractdepostrentAdd';
set @funCode05 = 'contractdepostrentDel';
set @funCode06 = 'contractdepostgetByContractId';
set @funCode07 = 'contractdepostgetByCollectionId';
set @funCode08 = 'contractdepostdownloadtemplete';
set @funCode09 = 'contractdepostdepostSave';
set @funCode10 = 'contractdepostsubmit';
set @funCode11 = 'filebatchdownload';
set @funCode12 = 'filematerialsupload';
set @funCode13 = 'filebatchremove';
set @funCode14 = 'contarctDeposiTFileListGroup';
set @funCode15 = 'contractDepostNoticeCommit';

-- init   删除菜单下这些功能点及对应授权
delete from bifrost_org_menu_function where function_id  in (select id from bifrost_function where menu_id = @menuID01 and code =  @funCode01);
delete from bifrost_org_menu_function where function_id  in (select id from bifrost_function where menu_id = @menuID01 and code =  @funCode02);
delete from bifrost_org_menu_function where function_id  in (select id from bifrost_function where menu_id = @menuID01 and code =  @funCode03);
delete from bifrost_org_menu_function where function_id  in (select id from bifrost_function where menu_id = @menuID01 and code =  @funCode04);
delete from bifrost_org_menu_function where function_id  in (select id from bifrost_function where menu_id = @menuID01 and code =  @funCode05);
delete from bifrost_org_menu_function where function_id  in (select id from bifrost_function where menu_id = @menuID01 and code =  @funCode06);
delete from bifrost_org_menu_function where function_id  in (select id from bifrost_function where menu_id = @menuID01 and code =  @funCode07);
delete from bifrost_org_menu_function where function_id  in (select id from bifrost_function where menu_id = @menuID01 and code =  @funCode08);
delete from bifrost_org_menu_function where function_id  in (select id from bifrost_function where menu_id = @menuID01 and code =  @funCode09);
delete from bifrost_org_menu_function where function_id  in (select id from bifrost_function where menu_id = @menuID01 and code =  @funCode10);
delete from bifrost_org_menu_function where function_id  in (select id from bifrost_function where menu_id = @menuID01 and code =  @funCode11);
delete from bifrost_org_menu_function where function_id  in (select id from bifrost_function where menu_id = @menuID01 and code =  @funCode12);
delete from bifrost_org_menu_function where function_id  in (select id from bifrost_function where menu_id = @menuID01 and code =  @funCode13);
delete from bifrost_org_menu_function where function_id  in (select id from bifrost_function where menu_id = @menuID01 and code =  @funCode14);
delete from bifrost_org_menu_function where function_id  in (select id from bifrost_function where menu_id = @menuID01 and code =  @funCode15);

delete from bifrost_role_menu_function where function_id in (select id from bifrost_function where menu_id = @menuID01 and code =  @funCode01);
delete from bifrost_role_menu_function where function_id in (select id from bifrost_function where menu_id = @menuID01 and code =  @funCode02);
delete from bifrost_role_menu_function where function_id in (select id from bifrost_function where menu_id = @menuID01 and code =  @funCode03);
delete from bifrost_role_menu_function where function_id in (select id from bifrost_function where menu_id = @menuID01 and code =  @funCode04);
delete from bifrost_role_menu_function where function_id in (select id from bifrost_function where menu_id = @menuID01 and code =  @funCode05);
delete from bifrost_role_menu_function where function_id in (select id from bifrost_function where menu_id = @menuID01 and code =  @funCode06);
delete from bifrost_role_menu_function where function_id in (select id from bifrost_function where menu_id = @menuID01 and code =  @funCode07);
delete from bifrost_role_menu_function where function_id in (select id from bifrost_function where menu_id = @menuID01 and code =  @funCode08);
delete from bifrost_role_menu_function where function_id in (select id from bifrost_function where menu_id = @menuID01 and code =  @funCode09);
delete from bifrost_role_menu_function where function_id in (select id from bifrost_function where menu_id = @menuID01 and code =  @funCode10);
delete from bifrost_role_menu_function where function_id in (select id from bifrost_function where menu_id = @menuID01 and code =  @funCode11);
delete from bifrost_role_menu_function where function_id in (select id from bifrost_function where menu_id = @menuID01 and code =  @funCode12);
delete from bifrost_role_menu_function where function_id in (select id from bifrost_function where menu_id = @menuID01 and code =  @funCode13);
delete from bifrost_role_menu_function where function_id in (select id from bifrost_function where menu_id = @menuID01 and code =  @funCode14);
delete from bifrost_role_menu_function where function_id in (select id from bifrost_function where menu_id = @menuID01 and code =  @funCode15);

delete from bifrost_function where menu_id = @menuID01 and code =  @funCode01;
delete from bifrost_function where menu_id = @menuID01 and code =  @funCode02;
delete from bifrost_function where menu_id = @menuID01 and code =  @funCode03;
delete from bifrost_function where menu_id = @menuID01 and code =  @funCode04;
delete from bifrost_function where menu_id = @menuID01 and code =  @funCode05;
delete from bifrost_function where menu_id = @menuID01 and code =  @funCode06;
delete from bifrost_function where menu_id = @menuID01 and code =  @funCode07;
delete from bifrost_function where menu_id = @menuID01 and code =  @funCode08;
delete from bifrost_function where menu_id = @menuID01 and code =  @funCode09;
delete from bifrost_function where menu_id = @menuID01 and code =  @funCode10;
delete from bifrost_function where menu_id = @menuID01 and code =  @funCode11;
delete from bifrost_function where menu_id = @menuID01 and code =  @funCode12;
delete from bifrost_function where menu_id = @menuID01 and code =  @funCode13;
delete from bifrost_function where menu_id = @menuID01 and code =  @funCode14;
delete from bifrost_function where menu_id = @menuID01 and code =  @funCode15;


-- 开始设置变量并存入功能点对应权限配置

INSERT INTO bifrost_function (gmt_create, gmt_modified, code, name, sort_no, menu_id, create_by, update_by, en_name, `method`, `path`, `type`, group_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', 'contractdepostcheck', '抵扣租金校验', 0, @menuID01, NULL, NULL, NULL, 'POST', '/contract/depost/check', 1, NULL);
INSERT INTO bifrost_function (gmt_create, gmt_modified, code, name, sort_no, menu_id, create_by, update_by, en_name, `method`, `path`, `type`, group_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', 'contractdepostdepostInfo', '抵扣租金信息查看', 0, @menuID01, NULL, NULL, NULL, 'POST', '/contract/depost/depostInfo', 1, NULL);
INSERT INTO bifrost_function (gmt_create, gmt_modified, code, name, sort_no, menu_id, create_by, update_by, en_name, `method`, `path`, `type`, group_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', 'contractdepostrentList', '抵扣租金信息列表', 0, @menuID01, NULL, NULL, NULL, 'POST', '/contract/depost/rentList', 1, NULL);
INSERT INTO bifrost_function (gmt_create, gmt_modified, code, name, sort_no, menu_id, create_by, update_by, en_name, `method`, `path`, `type`, group_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', 'contractdepostrentAdd', '抵扣租金信息新增', 0, @menuID01, NULL, NULL, NULL, 'POST', '/contract/depost/rentAdd', 1, NULL);
INSERT INTO bifrost_function (gmt_create, gmt_modified, code, name, sort_no, menu_id, create_by, update_by, en_name, `method`, `path`, `type`, group_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', 'contractdepostrentDel', '抵扣租金信息删除', 0, @menuID01, NULL, NULL, NULL, 'POST', '/contract/depost/rentDel', 1, NULL);
INSERT INTO bifrost_function (gmt_create, gmt_modified, code, name, sort_no, menu_id, create_by, update_by, en_name, `method`, `path`, `type`, group_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', 'contractdepostgetByContractId', '抵扣租金信息关联付款信息', 0, @menuID01, NULL, NULL, NULL, 'POST', '/contract/depost/getByContractId', 1, NULL);
INSERT INTO bifrost_function (gmt_create, gmt_modified, code, name, sort_no, menu_id, create_by, update_by, en_name, `method`, `path`, `type`, group_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', 'contractdepostgetByCollectionId', '抵扣租金信息关联现金流信息', 0, @menuID01, NULL, NULL, NULL, 'POST', '/contract/depost/getByCollectionId', 1, NULL);
INSERT INTO bifrost_function (gmt_create, gmt_modified, code, name, sort_no, menu_id, create_by, update_by, en_name, `method`, `path`, `type`, group_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', 'contractdepostdownloadtemplete', '抵扣租金信息模板下载', 0, @menuID01, NULL, NULL, NULL, 'GET', '/contract/depost/downloadtemplete', 1, NULL);
INSERT INTO bifrost_function (gmt_create, gmt_modified, code, name, sort_no, menu_id, create_by, update_by, en_name, `method`, `path`, `type`, group_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', 'contractdepostdepostSave', '抵扣租金信息保存', 0, @menuID01, NULL, NULL, NULL, 'POST', '/contract/depost/depostSave', 1, NULL);
INSERT INTO bifrost_function (gmt_create, gmt_modified, code, name, sort_no, menu_id, create_by, update_by, en_name, `method`, `path`, `type`, group_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', 'contractdepostsubmit', '抵扣租金信息提交审批', 0, @menuID01, NULL, NULL, NULL, 'POST', '/contract/depost/submit', 1, NULL);
INSERT INTO bifrost_function (gmt_create, gmt_modified, code, name, sort_no, menu_id, create_by, update_by, en_name, `method`, `path`, `type`, group_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', 'filebatchdownload', '保证金退抵批量下载', 0, @menuID01, NULL, NULL, NULL, 'GET', '/file/download', 1, NULL);
INSERT INTO bifrost_function (gmt_create, gmt_modified, code, name, sort_no, menu_id, create_by, update_by, en_name, `method`, `path`, `type`, group_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', 'filematerialsupload', '保证金退抵上传', 0, @menuID01, NULL, NULL, NULL, 'POST', '/file/upload', 1, NULL);
INSERT INTO bifrost_function (gmt_create, gmt_modified, code, name, sort_no, menu_id, create_by, update_by, en_name, `method`, `path`, `type`, group_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', 'filebatchremove', '保证金退抵删除', 0, @menuID01, NULL, NULL, NULL, 'POST', '/file/batch/remove', 1, NULL);
INSERT INTO bifrost_function (gmt_create, gmt_modified, code, name, sort_no, menu_id, create_by, update_by, en_name, `method`, `path`, `type`, group_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', 'contarctDeposiTFileListGroup', '保证金退抵资料清单', 0, @menuID01, NULL, NULL, NULL, 'POST', '/file/list', 1, NULL);
INSERT INTO bifrost_function (gmt_create, gmt_modified, code, name, sort_no, menu_id, create_by, update_by, en_name, `method`, `path`, `type`, group_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', 'contractDepostNoticeCommit', '保证金退回通知确认', 0, @menuID01, NULL, NULL, NULL, 'POST', '/contract/depost/noticeCommit', 1, NULL);


set @funID01 = (select id from bifrost_function where code = @funCode01);
set @funID02 = (select id from bifrost_function where code = @funCode02);
set @funID03 = (select id from bifrost_function where code = @funCode03);
set @funID04 = (select id from bifrost_function where code = @funCode04);
set @funID05 = (select id from bifrost_function where code = @funCode05);
set @funID06 = (select id from bifrost_function where code = @funCode06);
set @funID07 = (select id from bifrost_function where code = @funCode07);
set @funID08 = (select id from bifrost_function where code = @funCode08);
set @funID09 = (select id from bifrost_function where code = @funCode09);
set @funID10 = (select id from bifrost_function where code = @funCode10);
set @funID11 = (select id from bifrost_function where code = @funCode11);
set @funID12 = (select id from bifrost_function where code = @funCode12);
set @funID13 = (select id from bifrost_function where code = @funCode13);
set @funID14 = (select id from bifrost_function where code = @funCode14);
set @funID15 = (select id from bifrost_function where code = @funCode15);

set @orgID01 = (select id from bifrost_org where code = 'ZSZL');
set @orgID02 = (select id from bifrost_org where code = 'LDC');
set @orgID03 = (select id from bifrost_org where code = 'FXGLB_YWPS');
set @orgID04 = (select id from bifrost_org where code = 'FXGLWYH');
set @orgID05 = (select id from bifrost_org where code = 'XMPSWYH');
set @orgID06 = (select id from bifrost_org where code = 'JSSYB');
set @orgID07 = (select id from bifrost_org where code = 'HGJCYWB');
set @orgID08 = (select id from bifrost_org where code = 'JXHJGYWB');
set @orgID09 = (select id from bifrost_org where code = 'JCSSYWB');
set @orgID10 = (select id from bifrost_org where code = 'LLWLTD');
set @orgID11 = (select id from bifrost_org where code = 'XJZZHXJJTD');
set @orgID12 = (select id from bifrost_org where code = 'JTYSYWB');
set @orgID13 = (select id from bifrost_org where code = 'JHCWB');
set @orgID14 = (select id from bifrost_org where code = 'ZHGLB');
set @orgID15 = (select id from bifrost_org where code = 'ADMIN');
set @orgID16 = (select id from bifrost_org where code = 'DSH');
set @orgID17 = (select id from bifrost_org where code = 'YYGLB');
set @orgID18 = (select id from bifrost_org where code = 'GGSY');
set @orgID19 = (select id from bifrost_org where code = 'ZSJK');
set @orgID20 = (select id from bifrost_org where code = 'HYYWB');
set @orgID21 = (select id from bifrost_org where code = 'XNYYWB');
set @orgID22 = (select id from bifrost_org where code = 'SYCYWB');
set @orgID23 = (select id from bifrost_org where code = 'LSCYYWB');
set @orgID24 = (select id from bifrost_org where code = 'XXKJB');

set @roleID01 = (select id from bifrost_role where code = 'ADMIN_SUPERADMIN');
set @roleID02 = (select id from bifrost_role where code = 'CWGL_3');
set @roleID03 = (select id from bifrost_role where code = 'XMJL');
set @roleID04 = (select id from bifrost_role where code = 'GLY');
set @roleID05 = (select id from bifrost_role where code = 'CHAKAN');
set @roleID06 = (select id from bifrost_role where code = 'readAdmin');
set @roleID07 = (select id from bifrost_role where code = 'HTGL');
set @roleID08 = (select id from bifrost_role where code = 'CAIWUZONGJIAN');
set @roleID09 = (select id from bifrost_role where code = 'ZSJL');
set @roleID10 = (select id from bifrost_role where code = 'YYGLB-JBR');

set @roleID12 = (select id from bifrost_role where code = 'TDZ');
	  

INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID01, @menuID01, @funID01, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID02, @menuID01, @funID01, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID03, @menuID01, @funID01, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID04, @menuID01, @funID01, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID05, @menuID01, @funID01, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID06, @menuID01, @funID01, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID07, @menuID01, @funID01, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID08, @menuID01, @funID01, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID09, @menuID01, @funID01, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID10, @menuID01, @funID01, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID11, @menuID01, @funID01, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID12, @menuID01, @funID01, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID13, @menuID01, @funID01, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID14, @menuID01, @funID01, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID15, @menuID01, @funID01, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID16, @menuID01, @funID01, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID17, @menuID01, @funID01, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID18, @menuID01, @funID01, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID19, @menuID01, @funID01, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID20, @menuID01, @funID01, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID21, @menuID01, @funID01, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID22, @menuID01, @funID01, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID23, @menuID01, @funID01, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID24, @menuID01, @funID01, NULL, NULL, 2);

-- INSERT INTO bifrost_role_menu_function (gmt_create, gmt_modified, role_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @roleID01, @menuID01,  @funID01, NULL, NULL, 2);
-- INSERT INTO bifrost_role_menu_function (gmt_create, gmt_modified, role_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @roleID02, @menuID01,  @funID01, NULL, NULL, 2);
-- INSERT INTO bifrost_role_menu_function (gmt_create, gmt_modified, role_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @roleID03, @menuID01,  @funID01, NULL, NULL, 2);
-- INSERT INTO bifrost_role_menu_function (gmt_create, gmt_modified, role_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @roleID04, @menuID01,  @funID01, NULL, NULL, 2);
-- INSERT INTO bifrost_role_menu_function (gmt_create, gmt_modified, role_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @roleID05, @menuID01,  @funID01, NULL, NULL, 2);
-- INSERT INTO bifrost_role_menu_function (gmt_create, gmt_modified, role_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @roleID06, @menuID01,  @funID01, NULL, NULL, 2);
-- INSERT INTO bifrost_role_menu_function (gmt_create, gmt_modified, role_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @roleID07, @menuID01,  @funID01, NULL, NULL, 2);
-- INSERT INTO bifrost_role_menu_function (gmt_create, gmt_modified, role_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @roleID08, @menuID01,  @funID01, NULL, NULL, 2);
-- INSERT INTO bifrost_role_menu_function (gmt_create, gmt_modified, role_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @roleID09, @menuID01,  @funID01, NULL, NULL, 2);
-- INSERT INTO bifrost_role_menu_function (gmt_create, gmt_modified, role_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @roleID10, @menuID01,  @funID01, NULL, NULL, 2);
INSERT INTO bifrost_role_menu_function (gmt_create, gmt_modified, role_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @roleID03, @menuID01,  @funID01, NULL, NULL, 2);
INSERT INTO bifrost_role_menu_function (gmt_create, gmt_modified, role_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @roleID12, @menuID01,  @funID01, NULL, NULL, 2);


INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID01, @menuID01, @funID02, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID02, @menuID01, @funID02, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID03, @menuID01, @funID02, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID04, @menuID01, @funID02, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID05, @menuID01, @funID02, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID06, @menuID01, @funID02, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID07, @menuID01, @funID02, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID08, @menuID01, @funID02, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID09, @menuID01, @funID02, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID10, @menuID01, @funID02, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID11, @menuID01, @funID02, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID12, @menuID01, @funID02, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID13, @menuID01, @funID02, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID14, @menuID01, @funID02, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID15, @menuID01, @funID02, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID16, @menuID01, @funID02, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID17, @menuID01, @funID02, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID18, @menuID01, @funID02, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID19, @menuID01, @funID02, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID20, @menuID01, @funID02, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID21, @menuID01, @funID02, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID22, @menuID01, @funID02, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID23, @menuID01, @funID02, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID24, @menuID01, @funID02, NULL, NULL, 2);

INSERT INTO bifrost_role_menu_function (gmt_create, gmt_modified, role_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @roleID01, @menuID01,  @funID02, NULL, NULL, 2);
INSERT INTO bifrost_role_menu_function (gmt_create, gmt_modified, role_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @roleID02, @menuID01,  @funID02, NULL, NULL, 2);
INSERT INTO bifrost_role_menu_function (gmt_create, gmt_modified, role_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @roleID03, @menuID01,  @funID02, NULL, NULL, 2);
INSERT INTO bifrost_role_menu_function (gmt_create, gmt_modified, role_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @roleID04, @menuID01,  @funID02, NULL, NULL, 2);
INSERT INTO bifrost_role_menu_function (gmt_create, gmt_modified, role_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @roleID05, @menuID01,  @funID02, NULL, NULL, 2);
INSERT INTO bifrost_role_menu_function (gmt_create, gmt_modified, role_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @roleID06, @menuID01,  @funID02, NULL, NULL, 2);
INSERT INTO bifrost_role_menu_function (gmt_create, gmt_modified, role_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @roleID07, @menuID01,  @funID02, NULL, NULL, 2);
INSERT INTO bifrost_role_menu_function (gmt_create, gmt_modified, role_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @roleID08, @menuID01,  @funID02, NULL, NULL, 2);
INSERT INTO bifrost_role_menu_function (gmt_create, gmt_modified, role_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @roleID09, @menuID01,  @funID02, NULL, NULL, 2);
INSERT INTO bifrost_role_menu_function (gmt_create, gmt_modified, role_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @roleID10, @menuID01,  @funID02, NULL, NULL, 2);


INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID01, @menuID01, @funID03, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID02, @menuID01, @funID03, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID03, @menuID01, @funID03, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID04, @menuID01, @funID03, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID05, @menuID01, @funID03, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID06, @menuID01, @funID03, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID07, @menuID01, @funID03, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID08, @menuID01, @funID03, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID09, @menuID01, @funID03, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID10, @menuID01, @funID03, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID11, @menuID01, @funID03, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID12, @menuID01, @funID03, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID13, @menuID01, @funID03, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID14, @menuID01, @funID03, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID15, @menuID01, @funID03, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID16, @menuID01, @funID03, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID17, @menuID01, @funID03, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID18, @menuID01, @funID03, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID19, @menuID01, @funID03, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID20, @menuID01, @funID03, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID21, @menuID01, @funID03, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID22, @menuID01, @funID03, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID23, @menuID01, @funID03, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID24, @menuID01, @funID03, NULL, NULL, 2);

INSERT INTO bifrost_role_menu_function (gmt_create, gmt_modified, role_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @roleID01, @menuID01,  @funID03, NULL, NULL, 2);
INSERT INTO bifrost_role_menu_function (gmt_create, gmt_modified, role_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @roleID02, @menuID01,  @funID03, NULL, NULL, 2);
INSERT INTO bifrost_role_menu_function (gmt_create, gmt_modified, role_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @roleID03, @menuID01,  @funID03, NULL, NULL, 2);
INSERT INTO bifrost_role_menu_function (gmt_create, gmt_modified, role_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @roleID04, @menuID01,  @funID03, NULL, NULL, 2);
INSERT INTO bifrost_role_menu_function (gmt_create, gmt_modified, role_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @roleID05, @menuID01,  @funID03, NULL, NULL, 2);
INSERT INTO bifrost_role_menu_function (gmt_create, gmt_modified, role_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @roleID06, @menuID01,  @funID03, NULL, NULL, 2);
INSERT INTO bifrost_role_menu_function (gmt_create, gmt_modified, role_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @roleID07, @menuID01,  @funID03, NULL, NULL, 2);
INSERT INTO bifrost_role_menu_function (gmt_create, gmt_modified, role_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @roleID08, @menuID01,  @funID03, NULL, NULL, 2);
INSERT INTO bifrost_role_menu_function (gmt_create, gmt_modified, role_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @roleID09, @menuID01,  @funID03, NULL, NULL, 2);
INSERT INTO bifrost_role_menu_function (gmt_create, gmt_modified, role_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @roleID10, @menuID01,  @funID03, NULL, NULL, 2);


INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID01, @menuID01, @funID04, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID02, @menuID01, @funID04, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID03, @menuID01, @funID04, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID04, @menuID01, @funID04, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID05, @menuID01, @funID04, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID06, @menuID01, @funID04, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID07, @menuID01, @funID04, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID08, @menuID01, @funID04, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID09, @menuID01, @funID04, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID10, @menuID01, @funID04, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID11, @menuID01, @funID04, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID12, @menuID01, @funID04, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID13, @menuID01, @funID04, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID14, @menuID01, @funID04, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID15, @menuID01, @funID04, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID16, @menuID01, @funID04, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID17, @menuID01, @funID04, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID18, @menuID01, @funID04, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID19, @menuID01, @funID04, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID20, @menuID01, @funID04, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID21, @menuID01, @funID04, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID22, @menuID01, @funID04, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID23, @menuID01, @funID04, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID24, @menuID01, @funID04, NULL, NULL, 2);

INSERT INTO bifrost_role_menu_function (gmt_create, gmt_modified, role_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @roleID01, @menuID01,  @funID04, NULL, NULL, 2);
INSERT INTO bifrost_role_menu_function (gmt_create, gmt_modified, role_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @roleID02, @menuID01,  @funID04, NULL, NULL, 2);
INSERT INTO bifrost_role_menu_function (gmt_create, gmt_modified, role_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @roleID03, @menuID01,  @funID04, NULL, NULL, 2);
INSERT INTO bifrost_role_menu_function (gmt_create, gmt_modified, role_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @roleID04, @menuID01,  @funID04, NULL, NULL, 2);
INSERT INTO bifrost_role_menu_function (gmt_create, gmt_modified, role_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @roleID05, @menuID01,  @funID04, NULL, NULL, 2);
INSERT INTO bifrost_role_menu_function (gmt_create, gmt_modified, role_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @roleID06, @menuID01,  @funID04, NULL, NULL, 2);
INSERT INTO bifrost_role_menu_function (gmt_create, gmt_modified, role_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @roleID07, @menuID01,  @funID04, NULL, NULL, 2);
INSERT INTO bifrost_role_menu_function (gmt_create, gmt_modified, role_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @roleID08, @menuID01,  @funID04, NULL, NULL, 2);
INSERT INTO bifrost_role_menu_function (gmt_create, gmt_modified, role_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @roleID09, @menuID01,  @funID04, NULL, NULL, 2);
INSERT INTO bifrost_role_menu_function (gmt_create, gmt_modified, role_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @roleID10, @menuID01,  @funID04, NULL, NULL, 2);


INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID01, @menuID01, @funID05, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID02, @menuID01, @funID05, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID03, @menuID01, @funID05, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID04, @menuID01, @funID05, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID05, @menuID01, @funID05, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID06, @menuID01, @funID05, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID07, @menuID01, @funID05, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID08, @menuID01, @funID05, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID09, @menuID01, @funID05, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID10, @menuID01, @funID05, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID11, @menuID01, @funID05, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID12, @menuID01, @funID05, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID13, @menuID01, @funID05, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID14, @menuID01, @funID05, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID15, @menuID01, @funID05, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID16, @menuID01, @funID05, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID17, @menuID01, @funID05, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID18, @menuID01, @funID05, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID19, @menuID01, @funID05, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID20, @menuID01, @funID05, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID21, @menuID01, @funID05, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID22, @menuID01, @funID05, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID23, @menuID01, @funID05, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID24, @menuID01, @funID05, NULL, NULL, 2);

INSERT INTO bifrost_role_menu_function (gmt_create, gmt_modified, role_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @roleID01, @menuID01,  @funID05, NULL, NULL, 2);
INSERT INTO bifrost_role_menu_function (gmt_create, gmt_modified, role_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @roleID02, @menuID01,  @funID05, NULL, NULL, 2);
INSERT INTO bifrost_role_menu_function (gmt_create, gmt_modified, role_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @roleID03, @menuID01,  @funID05, NULL, NULL, 2);
INSERT INTO bifrost_role_menu_function (gmt_create, gmt_modified, role_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @roleID04, @menuID01,  @funID05, NULL, NULL, 2);
INSERT INTO bifrost_role_menu_function (gmt_create, gmt_modified, role_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @roleID05, @menuID01,  @funID05, NULL, NULL, 2);
INSERT INTO bifrost_role_menu_function (gmt_create, gmt_modified, role_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @roleID06, @menuID01,  @funID05, NULL, NULL, 2);
INSERT INTO bifrost_role_menu_function (gmt_create, gmt_modified, role_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @roleID07, @menuID01,  @funID05, NULL, NULL, 2);
INSERT INTO bifrost_role_menu_function (gmt_create, gmt_modified, role_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @roleID08, @menuID01,  @funID05, NULL, NULL, 2);
INSERT INTO bifrost_role_menu_function (gmt_create, gmt_modified, role_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @roleID09, @menuID01,  @funID05, NULL, NULL, 2);
INSERT INTO bifrost_role_menu_function (gmt_create, gmt_modified, role_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @roleID10, @menuID01,  @funID05, NULL, NULL, 2);


INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID01, @menuID01, @funID06, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID02, @menuID01, @funID06, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID03, @menuID01, @funID06, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID04, @menuID01, @funID06, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID05, @menuID01, @funID06, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID06, @menuID01, @funID06, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID07, @menuID01, @funID06, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID08, @menuID01, @funID06, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID09, @menuID01, @funID06, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID10, @menuID01, @funID06, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID11, @menuID01, @funID06, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID12, @menuID01, @funID06, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID13, @menuID01, @funID06, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID14, @menuID01, @funID06, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID15, @menuID01, @funID06, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID16, @menuID01, @funID06, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID17, @menuID01, @funID06, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID18, @menuID01, @funID06, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID19, @menuID01, @funID06, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID20, @menuID01, @funID06, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID21, @menuID01, @funID06, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID22, @menuID01, @funID06, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID23, @menuID01, @funID06, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID24, @menuID01, @funID06, NULL, NULL, 2);

INSERT INTO bifrost_role_menu_function (gmt_create, gmt_modified, role_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @roleID01, @menuID01,  @funID06, NULL, NULL, 2);
INSERT INTO bifrost_role_menu_function (gmt_create, gmt_modified, role_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @roleID02, @menuID01,  @funID06, NULL, NULL, 2);
INSERT INTO bifrost_role_menu_function (gmt_create, gmt_modified, role_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @roleID03, @menuID01,  @funID06, NULL, NULL, 2);
INSERT INTO bifrost_role_menu_function (gmt_create, gmt_modified, role_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @roleID04, @menuID01,  @funID06, NULL, NULL, 2);
INSERT INTO bifrost_role_menu_function (gmt_create, gmt_modified, role_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @roleID05, @menuID01,  @funID06, NULL, NULL, 2);
INSERT INTO bifrost_role_menu_function (gmt_create, gmt_modified, role_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @roleID06, @menuID01,  @funID06, NULL, NULL, 2);
INSERT INTO bifrost_role_menu_function (gmt_create, gmt_modified, role_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @roleID07, @menuID01,  @funID06, NULL, NULL, 2);
INSERT INTO bifrost_role_menu_function (gmt_create, gmt_modified, role_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @roleID08, @menuID01,  @funID06, NULL, NULL, 2);
INSERT INTO bifrost_role_menu_function (gmt_create, gmt_modified, role_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @roleID09, @menuID01,  @funID06, NULL, NULL, 2);
INSERT INTO bifrost_role_menu_function (gmt_create, gmt_modified, role_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @roleID10, @menuID01,  @funID06, NULL, NULL, 2);


INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID01, @menuID01, @funID07, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID02, @menuID01, @funID07, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID03, @menuID01, @funID07, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID04, @menuID01, @funID07, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID05, @menuID01, @funID07, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID06, @menuID01, @funID07, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID07, @menuID01, @funID07, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID08, @menuID01, @funID07, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID09, @menuID01, @funID07, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID10, @menuID01, @funID07, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID11, @menuID01, @funID07, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID12, @menuID01, @funID07, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID13, @menuID01, @funID07, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID14, @menuID01, @funID07, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID15, @menuID01, @funID07, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID16, @menuID01, @funID07, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID17, @menuID01, @funID07, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID18, @menuID01, @funID07, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID19, @menuID01, @funID07, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID20, @menuID01, @funID07, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID21, @menuID01, @funID07, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID22, @menuID01, @funID07, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID23, @menuID01, @funID07, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID24, @menuID01, @funID07, NULL, NULL, 2);

INSERT INTO bifrost_role_menu_function (gmt_create, gmt_modified, role_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @roleID01, @menuID01,  @funID07, NULL, NULL, 2);
INSERT INTO bifrost_role_menu_function (gmt_create, gmt_modified, role_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @roleID02, @menuID01,  @funID07, NULL, NULL, 2);
INSERT INTO bifrost_role_menu_function (gmt_create, gmt_modified, role_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @roleID03, @menuID01,  @funID07, NULL, NULL, 2);
INSERT INTO bifrost_role_menu_function (gmt_create, gmt_modified, role_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @roleID04, @menuID01,  @funID07, NULL, NULL, 2);
INSERT INTO bifrost_role_menu_function (gmt_create, gmt_modified, role_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @roleID05, @menuID01,  @funID07, NULL, NULL, 2);
INSERT INTO bifrost_role_menu_function (gmt_create, gmt_modified, role_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @roleID06, @menuID01,  @funID07, NULL, NULL, 2);
INSERT INTO bifrost_role_menu_function (gmt_create, gmt_modified, role_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @roleID07, @menuID01,  @funID07, NULL, NULL, 2);
INSERT INTO bifrost_role_menu_function (gmt_create, gmt_modified, role_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @roleID08, @menuID01,  @funID07, NULL, NULL, 2);
INSERT INTO bifrost_role_menu_function (gmt_create, gmt_modified, role_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @roleID09, @menuID01,  @funID07, NULL, NULL, 2);
INSERT INTO bifrost_role_menu_function (gmt_create, gmt_modified, role_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @roleID10, @menuID01,  @funID07, NULL, NULL, 2);


INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID01, @menuID01, @funID08, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID02, @menuID01, @funID08, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID03, @menuID01, @funID08, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID04, @menuID01, @funID08, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID05, @menuID01, @funID08, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID06, @menuID01, @funID08, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID07, @menuID01, @funID08, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID08, @menuID01, @funID08, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID09, @menuID01, @funID08, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID10, @menuID01, @funID08, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID11, @menuID01, @funID08, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID12, @menuID01, @funID08, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID13, @menuID01, @funID08, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID14, @menuID01, @funID08, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID15, @menuID01, @funID08, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID16, @menuID01, @funID08, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID17, @menuID01, @funID08, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID18, @menuID01, @funID08, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID19, @menuID01, @funID08, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID20, @menuID01, @funID08, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID21, @menuID01, @funID08, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID22, @menuID01, @funID08, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID23, @menuID01, @funID08, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID24, @menuID01, @funID08, NULL, NULL, 2);

INSERT INTO bifrost_role_menu_function (gmt_create, gmt_modified, role_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @roleID01, @menuID01,  @funID08, NULL, NULL, 2);
INSERT INTO bifrost_role_menu_function (gmt_create, gmt_modified, role_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @roleID02, @menuID01,  @funID08, NULL, NULL, 2);
INSERT INTO bifrost_role_menu_function (gmt_create, gmt_modified, role_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @roleID03, @menuID01,  @funID08, NULL, NULL, 2);
INSERT INTO bifrost_role_menu_function (gmt_create, gmt_modified, role_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @roleID04, @menuID01,  @funID08, NULL, NULL, 2);
INSERT INTO bifrost_role_menu_function (gmt_create, gmt_modified, role_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @roleID05, @menuID01,  @funID08, NULL, NULL, 2);
INSERT INTO bifrost_role_menu_function (gmt_create, gmt_modified, role_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @roleID06, @menuID01,  @funID08, NULL, NULL, 2);
INSERT INTO bifrost_role_menu_function (gmt_create, gmt_modified, role_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @roleID07, @menuID01,  @funID08, NULL, NULL, 2);
INSERT INTO bifrost_role_menu_function (gmt_create, gmt_modified, role_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @roleID08, @menuID01,  @funID08, NULL, NULL, 2);
INSERT INTO bifrost_role_menu_function (gmt_create, gmt_modified, role_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @roleID09, @menuID01,  @funID08, NULL, NULL, 2);
INSERT INTO bifrost_role_menu_function (gmt_create, gmt_modified, role_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @roleID10, @menuID01,  @funID08, NULL, NULL, 2);


INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID01, @menuID01, @funID09, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID02, @menuID01, @funID09, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID03, @menuID01, @funID09, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID04, @menuID01, @funID09, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID05, @menuID01, @funID09, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID06, @menuID01, @funID09, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID07, @menuID01, @funID09, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID08, @menuID01, @funID09, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID09, @menuID01, @funID09, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID10, @menuID01, @funID09, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID11, @menuID01, @funID09, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID12, @menuID01, @funID09, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID13, @menuID01, @funID09, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID14, @menuID01, @funID09, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID15, @menuID01, @funID09, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID16, @menuID01, @funID09, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID17, @menuID01, @funID09, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID18, @menuID01, @funID09, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID19, @menuID01, @funID09, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID20, @menuID01, @funID09, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID21, @menuID01, @funID09, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID22, @menuID01, @funID09, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID23, @menuID01, @funID09, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID24, @menuID01, @funID09, NULL, NULL, 2);

INSERT INTO bifrost_role_menu_function (gmt_create, gmt_modified, role_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @roleID01, @menuID01,  @funID09, NULL, NULL, 2);
INSERT INTO bifrost_role_menu_function (gmt_create, gmt_modified, role_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @roleID02, @menuID01,  @funID09, NULL, NULL, 2);
INSERT INTO bifrost_role_menu_function (gmt_create, gmt_modified, role_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @roleID03, @menuID01,  @funID09, NULL, NULL, 2);
INSERT INTO bifrost_role_menu_function (gmt_create, gmt_modified, role_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @roleID04, @menuID01,  @funID09, NULL, NULL, 2);
INSERT INTO bifrost_role_menu_function (gmt_create, gmt_modified, role_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @roleID05, @menuID01,  @funID09, NULL, NULL, 2);
INSERT INTO bifrost_role_menu_function (gmt_create, gmt_modified, role_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @roleID06, @menuID01,  @funID09, NULL, NULL, 2);
INSERT INTO bifrost_role_menu_function (gmt_create, gmt_modified, role_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @roleID07, @menuID01,  @funID09, NULL, NULL, 2);
INSERT INTO bifrost_role_menu_function (gmt_create, gmt_modified, role_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @roleID08, @menuID01,  @funID09, NULL, NULL, 2);
INSERT INTO bifrost_role_menu_function (gmt_create, gmt_modified, role_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @roleID09, @menuID01,  @funID09, NULL, NULL, 2);
INSERT INTO bifrost_role_menu_function (gmt_create, gmt_modified, role_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @roleID10, @menuID01,  @funID09, NULL, NULL, 2);


INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID01, @menuID01, @funID10, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID02, @menuID01, @funID10, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID03, @menuID01, @funID10, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID04, @menuID01, @funID10, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID05, @menuID01, @funID10, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID06, @menuID01, @funID10, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID07, @menuID01, @funID10, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID08, @menuID01, @funID10, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID09, @menuID01, @funID10, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID10, @menuID01, @funID10, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID11, @menuID01, @funID10, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID12, @menuID01, @funID10, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID13, @menuID01, @funID10, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID14, @menuID01, @funID10, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID15, @menuID01, @funID10, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID16, @menuID01, @funID10, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID17, @menuID01, @funID10, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID18, @menuID01, @funID10, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID19, @menuID01, @funID10, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID20, @menuID01, @funID10, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID21, @menuID01, @funID10, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID22, @menuID01, @funID10, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID23, @menuID01, @funID10, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID24, @menuID01, @funID10, NULL, NULL, 2);

INSERT INTO bifrost_role_menu_function (gmt_create, gmt_modified, role_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @roleID01, @menuID01,  @funID10, NULL, NULL, 2);
INSERT INTO bifrost_role_menu_function (gmt_create, gmt_modified, role_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @roleID02, @menuID01,  @funID10, NULL, NULL, 2);
INSERT INTO bifrost_role_menu_function (gmt_create, gmt_modified, role_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @roleID03, @menuID01,  @funID10, NULL, NULL, 2);
INSERT INTO bifrost_role_menu_function (gmt_create, gmt_modified, role_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @roleID04, @menuID01,  @funID10, NULL, NULL, 2);
INSERT INTO bifrost_role_menu_function (gmt_create, gmt_modified, role_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @roleID05, @menuID01,  @funID10, NULL, NULL, 2);
INSERT INTO bifrost_role_menu_function (gmt_create, gmt_modified, role_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @roleID06, @menuID01,  @funID10, NULL, NULL, 2);
INSERT INTO bifrost_role_menu_function (gmt_create, gmt_modified, role_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @roleID07, @menuID01,  @funID10, NULL, NULL, 2);
INSERT INTO bifrost_role_menu_function (gmt_create, gmt_modified, role_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @roleID08, @menuID01,  @funID10, NULL, NULL, 2);
INSERT INTO bifrost_role_menu_function (gmt_create, gmt_modified, role_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @roleID09, @menuID01,  @funID10, NULL, NULL, 2);
INSERT INTO bifrost_role_menu_function (gmt_create, gmt_modified, role_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @roleID10, @menuID01,  @funID10, NULL, NULL, 2);


INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID01, @menuID01, @funID11, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID02, @menuID01, @funID11, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID03, @menuID01, @funID11, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID04, @menuID01, @funID11, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID05, @menuID01, @funID11, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID06, @menuID01, @funID11, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID07, @menuID01, @funID11, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID08, @menuID01, @funID11, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID09, @menuID01, @funID11, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID10, @menuID01, @funID11, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID11, @menuID01, @funID11, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID12, @menuID01, @funID11, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID13, @menuID01, @funID11, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID14, @menuID01, @funID11, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID15, @menuID01, @funID11, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID16, @menuID01, @funID11, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID17, @menuID01, @funID11, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID18, @menuID01, @funID11, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID19, @menuID01, @funID11, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID20, @menuID01, @funID11, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID21, @menuID01, @funID11, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID22, @menuID01, @funID11, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID23, @menuID01, @funID11, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID24, @menuID01, @funID11, NULL, NULL, 2);

INSERT INTO bifrost_role_menu_function (gmt_create, gmt_modified, role_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @roleID01, @menuID01,  @funID11, NULL, NULL, 2);
INSERT INTO bifrost_role_menu_function (gmt_create, gmt_modified, role_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @roleID02, @menuID01,  @funID11, NULL, NULL, 2);
INSERT INTO bifrost_role_menu_function (gmt_create, gmt_modified, role_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @roleID03, @menuID01,  @funID11, NULL, NULL, 2);
INSERT INTO bifrost_role_menu_function (gmt_create, gmt_modified, role_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @roleID04, @menuID01,  @funID11, NULL, NULL, 2);
INSERT INTO bifrost_role_menu_function (gmt_create, gmt_modified, role_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @roleID05, @menuID01,  @funID11, NULL, NULL, 2);
INSERT INTO bifrost_role_menu_function (gmt_create, gmt_modified, role_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @roleID06, @menuID01,  @funID11, NULL, NULL, 2);
INSERT INTO bifrost_role_menu_function (gmt_create, gmt_modified, role_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @roleID07, @menuID01,  @funID11, NULL, NULL, 2);
INSERT INTO bifrost_role_menu_function (gmt_create, gmt_modified, role_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @roleID08, @menuID01,  @funID11, NULL, NULL, 2);
INSERT INTO bifrost_role_menu_function (gmt_create, gmt_modified, role_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @roleID09, @menuID01,  @funID11, NULL, NULL, 2);
INSERT INTO bifrost_role_menu_function (gmt_create, gmt_modified, role_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @roleID10, @menuID01,  @funID11, NULL, NULL, 2);


INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID01, @menuID01, @funID12, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID02, @menuID01, @funID12, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID03, @menuID01, @funID12, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID04, @menuID01, @funID12, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID05, @menuID01, @funID12, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID06, @menuID01, @funID12, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID07, @menuID01, @funID12, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID08, @menuID01, @funID12, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID09, @menuID01, @funID12, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID10, @menuID01, @funID12, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID11, @menuID01, @funID12, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID12, @menuID01, @funID12, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID13, @menuID01, @funID12, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID14, @menuID01, @funID12, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID15, @menuID01, @funID12, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID16, @menuID01, @funID12, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID17, @menuID01, @funID12, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID18, @menuID01, @funID12, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID19, @menuID01, @funID12, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID20, @menuID01, @funID12, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID21, @menuID01, @funID12, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID22, @menuID01, @funID12, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID23, @menuID01, @funID12, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID24, @menuID01, @funID12, NULL, NULL, 2);

INSERT INTO bifrost_role_menu_function (gmt_create, gmt_modified, role_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @roleID01, @menuID01,  @funID12, NULL, NULL, 2);
INSERT INTO bifrost_role_menu_function (gmt_create, gmt_modified, role_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @roleID02, @menuID01,  @funID12, NULL, NULL, 2);
INSERT INTO bifrost_role_menu_function (gmt_create, gmt_modified, role_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @roleID03, @menuID01,  @funID12, NULL, NULL, 2);
INSERT INTO bifrost_role_menu_function (gmt_create, gmt_modified, role_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @roleID04, @menuID01,  @funID12, NULL, NULL, 2);
INSERT INTO bifrost_role_menu_function (gmt_create, gmt_modified, role_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @roleID05, @menuID01,  @funID12, NULL, NULL, 2);
INSERT INTO bifrost_role_menu_function (gmt_create, gmt_modified, role_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @roleID06, @menuID01,  @funID12, NULL, NULL, 2);
INSERT INTO bifrost_role_menu_function (gmt_create, gmt_modified, role_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @roleID07, @menuID01,  @funID12, NULL, NULL, 2);
INSERT INTO bifrost_role_menu_function (gmt_create, gmt_modified, role_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @roleID08, @menuID01,  @funID12, NULL, NULL, 2);
INSERT INTO bifrost_role_menu_function (gmt_create, gmt_modified, role_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @roleID09, @menuID01,  @funID12, NULL, NULL, 2);
INSERT INTO bifrost_role_menu_function (gmt_create, gmt_modified, role_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @roleID10, @menuID01,  @funID12, NULL, NULL, 2);


INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID01, @menuID01, @funID13, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID02, @menuID01, @funID13, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID03, @menuID01, @funID13, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID04, @menuID01, @funID13, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID05, @menuID01, @funID13, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID06, @menuID01, @funID13, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID07, @menuID01, @funID13, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID08, @menuID01, @funID13, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID09, @menuID01, @funID13, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID10, @menuID01, @funID13, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID11, @menuID01, @funID13, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID12, @menuID01, @funID13, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID13, @menuID01, @funID13, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID14, @menuID01, @funID13, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID15, @menuID01, @funID13, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID16, @menuID01, @funID13, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID17, @menuID01, @funID13, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID18, @menuID01, @funID13, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID19, @menuID01, @funID13, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID20, @menuID01, @funID13, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID21, @menuID01, @funID13, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID22, @menuID01, @funID13, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID23, @menuID01, @funID13, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID24, @menuID01, @funID13, NULL, NULL, 2);

INSERT INTO bifrost_role_menu_function (gmt_create, gmt_modified, role_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @roleID01, @menuID01,  @funID13, NULL, NULL, 2);
INSERT INTO bifrost_role_menu_function (gmt_create, gmt_modified, role_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @roleID02, @menuID01,  @funID13, NULL, NULL, 2);
INSERT INTO bifrost_role_menu_function (gmt_create, gmt_modified, role_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @roleID03, @menuID01,  @funID13, NULL, NULL, 2);
INSERT INTO bifrost_role_menu_function (gmt_create, gmt_modified, role_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @roleID04, @menuID01,  @funID13, NULL, NULL, 2);
INSERT INTO bifrost_role_menu_function (gmt_create, gmt_modified, role_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @roleID05, @menuID01,  @funID13, NULL, NULL, 2);
INSERT INTO bifrost_role_menu_function (gmt_create, gmt_modified, role_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @roleID06, @menuID01,  @funID13, NULL, NULL, 2);
INSERT INTO bifrost_role_menu_function (gmt_create, gmt_modified, role_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @roleID07, @menuID01,  @funID13, NULL, NULL, 2);
INSERT INTO bifrost_role_menu_function (gmt_create, gmt_modified, role_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @roleID08, @menuID01,  @funID13, NULL, NULL, 2);
INSERT INTO bifrost_role_menu_function (gmt_create, gmt_modified, role_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @roleID09, @menuID01,  @funID13, NULL, NULL, 2);
INSERT INTO bifrost_role_menu_function (gmt_create, gmt_modified, role_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @roleID10, @menuID01,  @funID13, NULL, NULL, 2);


INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID01, @menuID01, @funID14, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID02, @menuID01, @funID14, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID03, @menuID01, @funID14, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID04, @menuID01, @funID14, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID05, @menuID01, @funID14, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID06, @menuID01, @funID14, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID07, @menuID01, @funID14, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID08, @menuID01, @funID14, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID09, @menuID01, @funID14, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID10, @menuID01, @funID14, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID11, @menuID01, @funID14, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID12, @menuID01, @funID14, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID13, @menuID01, @funID14, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID14, @menuID01, @funID14, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID15, @menuID01, @funID14, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID16, @menuID01, @funID14, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID17, @menuID01, @funID14, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID18, @menuID01, @funID14, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID19, @menuID01, @funID14, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID20, @menuID01, @funID14, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID21, @menuID01, @funID14, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID22, @menuID01, @funID14, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID23, @menuID01, @funID14, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID24, @menuID01, @funID14, NULL, NULL, 2);

INSERT INTO bifrost_role_menu_function (gmt_create, gmt_modified, role_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @roleID01, @menuID01,  @funID14, NULL, NULL, 2);
INSERT INTO bifrost_role_menu_function (gmt_create, gmt_modified, role_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @roleID02, @menuID01,  @funID14, NULL, NULL, 2);
INSERT INTO bifrost_role_menu_function (gmt_create, gmt_modified, role_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @roleID03, @menuID01,  @funID14, NULL, NULL, 2);
INSERT INTO bifrost_role_menu_function (gmt_create, gmt_modified, role_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @roleID04, @menuID01,  @funID14, NULL, NULL, 2);
INSERT INTO bifrost_role_menu_function (gmt_create, gmt_modified, role_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @roleID05, @menuID01,  @funID14, NULL, NULL, 2);
INSERT INTO bifrost_role_menu_function (gmt_create, gmt_modified, role_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @roleID06, @menuID01,  @funID14, NULL, NULL, 2);
INSERT INTO bifrost_role_menu_function (gmt_create, gmt_modified, role_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @roleID07, @menuID01,  @funID14, NULL, NULL, 2);
INSERT INTO bifrost_role_menu_function (gmt_create, gmt_modified, role_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @roleID08, @menuID01,  @funID14, NULL, NULL, 2);
INSERT INTO bifrost_role_menu_function (gmt_create, gmt_modified, role_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @roleID09, @menuID01,  @funID14, NULL, NULL, 2);
INSERT INTO bifrost_role_menu_function (gmt_create, gmt_modified, role_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @roleID10, @menuID01,  @funID14, NULL, NULL, 2);


INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID01, @menuID01, @funID15, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID02, @menuID01, @funID15, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID03, @menuID01, @funID15, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID04, @menuID01, @funID15, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID05, @menuID01, @funID15, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID06, @menuID01, @funID15, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID07, @menuID01, @funID15, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID08, @menuID01, @funID15, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID09, @menuID01, @funID15, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID10, @menuID01, @funID15, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID11, @menuID01, @funID15, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID12, @menuID01, @funID15, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID13, @menuID01, @funID15, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID14, @menuID01, @funID15, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID15, @menuID01, @funID15, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID16, @menuID01, @funID15, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID17, @menuID01, @funID15, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID18, @menuID01, @funID15, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID19, @menuID01, @funID15, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID20, @menuID01, @funID15, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID21, @menuID01, @funID15, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID22, @menuID01, @funID15, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID23, @menuID01, @funID15, NULL, NULL, 2);
INSERT INTO bifrost_org_menu_function (gmt_create, gmt_modified, org_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @orgID24, @menuID01, @funID15, NULL, NULL, 2);

INSERT INTO bifrost_role_menu_function (gmt_create, gmt_modified, role_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @roleID01, @menuID01,  @funID15, NULL, NULL, 2);
INSERT INTO bifrost_role_menu_function (gmt_create, gmt_modified, role_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @roleID02, @menuID01,  @funID15, NULL, NULL, 2);
INSERT INTO bifrost_role_menu_function (gmt_create, gmt_modified, role_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @roleID03, @menuID01,  @funID15, NULL, NULL, 2);
INSERT INTO bifrost_role_menu_function (gmt_create, gmt_modified, role_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @roleID04, @menuID01,  @funID15, NULL, NULL, 2);
INSERT INTO bifrost_role_menu_function (gmt_create, gmt_modified, role_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @roleID05, @menuID01,  @funID15, NULL, NULL, 2);
INSERT INTO bifrost_role_menu_function (gmt_create, gmt_modified, role_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @roleID06, @menuID01,  @funID15, NULL, NULL, 2);
INSERT INTO bifrost_role_menu_function (gmt_create, gmt_modified, role_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @roleID07, @menuID01,  @funID15, NULL, NULL, 2);
INSERT INTO bifrost_role_menu_function (gmt_create, gmt_modified, role_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @roleID08, @menuID01,  @funID15, NULL, NULL, 2);
INSERT INTO bifrost_role_menu_function (gmt_create, gmt_modified, role_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @roleID09, @menuID01,  @funID15, NULL, NULL, 2);
INSERT INTO bifrost_role_menu_function (gmt_create, gmt_modified, role_id, menu_id, function_id, create_by, update_by, custom_tree_id) VALUES('2025-01-01 01:01:01', '2025-01-01 01:01:01', @roleID10, @menuID01,  @funID15, NULL, NULL, 2);


-- ============= 来自文件：20250206_linlili_003_dml.sql ============= 

update filing_materials set object_type = 'PROJECT',object_id = contract_id where filing_type ='BUSINESS_MATERIALS' and object_type is null;
update filing_first_level_config set require_flag = 0 where require_flag is null;
update bifrost_function set method = 'POST' where `path` ='/filingMaterial/batchDownload' and CODE ='filingFileBatchDownload';

delete from filing_first_level_config where filing_materials_config_id in (select id from filing_materials_config where business_type = 'FUND_FILING' and filing_type  in ('FUND_DIRECT_FINANCING','FUND_FINANCING'));
delete from filing_materials_config where business_type = 'FUND_FILING' and filing_type  in ('FUND_DIRECT_FINANCING','FUND_FINANCING');

INSERT INTO filing_materials_config (filing_type, business_type, enable_flag, remark) VALUES('FUND_DIRECT_FINANCING', 'FUND_FILING', 1, '资金直融');
INSERT INTO filing_materials_config (filing_type, business_type, enable_flag, remark) VALUES('FUND_FINANCING', 'FUND_FILING', 1, '资金间融');

INSERT INTO filing_first_level_config (filing_materials_config_id, dir_code, dir_name, fixed_flag, condition_key, sort_code, enable_flag,require_flag) VALUES((select id from filing_materials_config fmc where filing_type ='FUND_DIRECT_FINANCING' and business_type ='FUND_FILING'), 'RECORD', '备案材料', 1, NULL, 1, 1,1);
INSERT INTO filing_first_level_config (filing_materials_config_id, dir_code, dir_name, fixed_flag, condition_key, sort_code, enable_flag,require_flag) VALUES((select id from filing_materials_config fmc where filing_type ='FUND_DIRECT_FINANCING' and business_type ='FUND_FILING'), 'AGENCY', '中介机构材料', 1, NULL, 2, 1,1);
INSERT INTO filing_first_level_config (filing_materials_config_id, dir_code, dir_name, fixed_flag, condition_key, sort_code, enable_flag,require_flag) VALUES((select id from filing_materials_config fmc where filing_type ='FUND_DIRECT_FINANCING' and business_type ='FUND_FILING'), 'OTHER', '其他', 1, NULL, 99, 1,0);

INSERT INTO filing_first_level_config (filing_materials_config_id, dir_code, dir_name, fixed_flag, condition_key, sort_code, enable_flag,require_flag) VALUES((select id from filing_materials_config fmc where filing_type ='FUND_FINANCING' and business_type ='FUND_FILING'), 'LOAN_CONTRACT', '借款合同', 1, NULL, 1, 1,1);
INSERT INTO filing_first_level_config (filing_materials_config_id, dir_code, dir_name, fixed_flag, condition_key, sort_code, enable_flag,require_flag) VALUES((select id from filing_materials_config fmc where filing_type ='FUND_FINANCING' and business_type ='FUND_FILING'), 'LOAN_RECEIPT', '借款借据', 1, NULL, 2, 1,0);
INSERT INTO filing_first_level_config (filing_materials_config_id, dir_code, dir_name, fixed_flag, condition_key, sort_code, enable_flag,require_flag) VALUES((select id from filing_materials_config fmc where filing_type ='FUND_FINANCING' and business_type ='FUND_FILING'), 'GUARANTEE_CONTRACT', '担保合同', 1, NULL,13, 1,0);
INSERT INTO filing_first_level_config (filing_materials_config_id, dir_code, dir_name, fixed_flag, condition_key, sort_code, enable_flag,require_flag) VALUES((select id from filing_materials_config fmc where filing_type ='FUND_FINANCING' and business_type ='FUND_FILING'), 'OTHER', '其他', 1, NULL,99, 1,0);

delete from bifrost_role_menu_function where function_id in (select id from bifrost_function where code in ('fundFilingMaterialsBatchDownload','fundFilingMaterialsGetOperationsDirDict','fundFilingMaterialsDownload',
'fundFilingFileUpload','fundFilingFileBatchRemove','fundFilingCheckFile','fundFilingFileListGroup'));
delete from bifrost_org_menu_function where function_id in (select id from bifrost_function where code in ('fundFilingMaterialsBatchDownload','fundFilingMaterialsGetOperationsDirDict','fundFilingMaterialsDownload',
'fundFilingFileUpload','fundFilingFileBatchRemove','fundFilingCheckFile','fundFilingFileListGroup'));
delete from bifrost_function where code in ('fundFilingMaterialsBatchDownload','fundFilingMaterialsGetOperationsDirDict','fundFilingMaterialsDownload',
'fundFilingFileUpload','fundFilingFileBatchRemove','fundFilingCheckFile','fundFilingFileListGroup');


INSERT INTO bifrost_function(code,name,sort_no,menu_id,method,path,type,group_id)
SELECT 'fundFilingMaterialsBatchDownload','资金资料归档-批量下载',0,b.id,'POST','/fund/filingMaterial/batchDownload',2,c.id
FROM bifrost_menu b, gruul_function_group c where b.name = '我收到的' and c.group_describe='我收到的-编辑';
INSERT INTO bifrost_function(code,name,sort_no,menu_id,method,path,type,group_id)
SELECT 'fundFilingMaterialsGetOperationsDirDict','资金资料归档-获取归档资料一级目录',0,b.id,'POST','/fund/filingMaterial/getOperationsDirDict',1,c.id
FROM bifrost_menu b, gruul_function_group c where b.name = '我收到的' and c.group_describe='我收到的-查看';
INSERT INTO bifrost_function(code,name,sort_no,menu_id,method,path,type,group_id)
SELECT 'fundFilingCheckFile','资金资料归档-文件提交校验',0,b.id,'POST','/fund/filingMaterial/checkFile',2,c.id
FROM bifrost_menu b, gruul_function_group c where b.name = '我收到的' and c.group_describe='我收到的-查看';

INSERT INTO bifrost_function(code,name,sort_no,menu_id,method,path,type,group_id)
SELECT 'fundFilingMaterialsDownload','资金资料归档-单文件下载',0,b.id,'GET','/fund/filingMaterial/download',2,c.id
FROM bifrost_menu b, gruul_function_group c where b.name = '我收到的' and c.group_describe='我收到的-编辑';
INSERT INTO bifrost_function(code,name,sort_no,menu_id,method,path,type,group_id)
SELECT 'fundFilingFileUpload','资金资料归档-文件上传',0,b.id,'POST','/file/upload',2,c.id
FROM bifrost_menu b, gruul_function_group c where b.name = '我收到的' and c.group_describe='我收到的-编辑';
INSERT INTO bifrost_function(code,name,sort_no,menu_id,method,path,type,group_id)
SELECT 'fundFilingFileBatchRemove','资金资料归档-文件移除',0,b.id,'POST','/file/batch/remove',2,c.id
FROM bifrost_menu b, gruul_function_group c where b.name = '我收到的' and c.group_describe='我收到的-编辑';
INSERT INTO bifrost_function(code,name,sort_no,menu_id,method,path,type,group_id)
SELECT 'fundFilingFileListGroup','资金资料归档-获取文件分组',0,b.id,'POST','/file/list/group',1,c.id
FROM bifrost_menu b, gruul_function_group c where b.name = '我收到的' and c.group_describe='我收到的-编辑';

INSERT INTO bifrost_org_menu_function(org_id, menu_id, function_id, custom_tree_id)
SELECT org.id, menu.id, function.id, tree.id
FROM bifrost_org org,
     bifrost_function function,
     bifrost_menu menu,
     bifrost_custom_tree tree
where org.name in ('浙江浙商融资租赁有限公司','资金管理部')
  and function.code in ('fundFilingMaterialsBatchDownload','fundFilingMaterialsGetOperationsDirDict','fundFilingMaterialsDownload',
'fundFilingFileUpload','fundFilingFileBatchRemove','fundFilingCheckFile','fundFilingFileListGroup')
  and menu.name = '我收到的'
  and tree.name = '我的流程';



INSERT INTO bifrost_role_menu_function(role_id, menu_id, function_id, custom_tree_id)
SELECT role.id, menu.id, function.id, tree.id
FROM bifrost_role role,
     bifrost_function function,
     bifrost_menu menu,
     bifrost_custom_tree tree
WHERE role.code IN ('ZJGLB-ZJGL')
  and function.code in ('fundFilingMaterialsBatchDownload','fundFilingMaterialsGetOperationsDirDict','fundFilingMaterialsDownload',
'fundFilingFileUpload','fundFilingFileBatchRemove','fundFilingCheckFile','fundFilingFileListGroup')
  and menu.name = '我收到的'
  and tree.name = '我的流程';

INSERT INTO bifrost_role_menu_function(role_id, menu_id, function_id, custom_tree_id)
SELECT role.id, menu.id, function.id, tree.id
FROM bifrost_role role,
     bifrost_function function,
     bifrost_menu menu,
     bifrost_custom_tree tree
WHERE role.code IN ('ADMIN_SUPERADMIN','CHAKAN','COMPREHENSIVE_MANAGEMENT','readAdmin','GLY')
  and function.code in ('fundFilingMaterialsBatchDownload','fundFilingMaterialsGetOperationsDirDict','fundFilingMaterialsDownload','fundFilingCheckFile','fundFilingFileListGroup')
  and menu.name = '我收到的'
  and tree.name = '我的流程';

-- 直融资料下载重写接口
delete from bifrost_org_menu_function where function_id in (select id from bifrost_function where code in ('fundDirectFinancingBatchDownload','fundDirectFinancingDownload'));
delete from bifrost_role_menu_function where function_id in (select id from bifrost_function where code in ('fundDirectFinancingBatchDownload','fundDirectFinancingDownload'));
delete from bifrost_function where code in ('fundDirectFinancingBatchDownload','fundDirectFinancingDownload');

INSERT INTO bifrost_function(code,name,sort_no,menu_id,method,path,type,group_id)
SELECT 'fundDirectFinancingBatchDownload','直融-批量下载',0,b.id,'POST','/fund/direct/financing/batchDownload',2,c.id
FROM bifrost_menu b, gruul_function_group c where b.name = '直融管理' and c.group_describe='直接融资-编辑';
INSERT INTO bifrost_function(code,name,sort_no,menu_id,method,path,type,group_id)
SELECT 'fundDirectFinancingDownload','直融-下载',0,b.id,'GET','/fund/direct/financing/download',2,c.id
FROM bifrost_menu b, gruul_function_group c where b.name = '直融管理' and c.group_describe='直接融资-编辑';


INSERT INTO bifrost_org_menu_function(org_id, menu_id, function_id, custom_tree_id)
SELECT bomf.org_id, bomf.menu_id, t2.id, bomf.custom_tree_id
FROM bifrost_org_menu_function bomf,
     bifrost_function t,
     bifrost_function t2
where bomf.function_id = t.id and t.code ='fundDirectFinancingFileDownload' and t2.code  in ('fundDirectFinancingDownload','fundDirectFinancingBatchDownload');

INSERT INTO bifrost_role_menu_function(role_id, menu_id, function_id, custom_tree_id)
SELECT bomf.role_id, bomf.menu_id, t2.id, bomf.custom_tree_id
FROM bifrost_role_menu_function bomf,
     bifrost_function t,
     bifrost_function t2
where bomf.function_id = t.id and t.code ='fundDirectFinancingFileDownload' and t2.code in ('fundDirectFinancingDownload','fundDirectFinancingBatchDownload');


-- 间融资料下载重写接口
delete from bifrost_org_menu_function where function_id in (select id from bifrost_function where code in ('fundFinancingBatchDownload','fundFinancingDownload'));
delete from bifrost_role_menu_function where function_id in (select id from bifrost_function where code in ('fundFinancingBatchDownload','fundFinancingDownload'));
delete from bifrost_function where code in ('fundFinancingBatchDownload','fundFinancingDownload');

INSERT INTO bifrost_function(code,name,sort_no,menu_id,method,path,type,group_id)
SELECT 'fundFinancingBatchDownload','间融管理-批量下载',0,b.id,'POST','/fund/financing/baseinfo/batchDownload',2,c.id
FROM bifrost_menu b, gruul_function_group c where b.name = '间融管理' and c.group_describe='融资管理-编辑';
INSERT INTO bifrost_function(code,name,sort_no,menu_id,method,path,type,group_id)
SELECT 'fundFinancingDownload','间融管理-下载',0,b.id,'GET','/fund/financing/baseinfo/download',2,c.id
FROM bifrost_menu b, gruul_function_group c where b.name = '间融管理' and c.group_describe='融资管理-编辑';


INSERT INTO bifrost_org_menu_function(org_id, menu_id, function_id, custom_tree_id)
SELECT bomf.org_id, bomf.menu_id, t2.id, bomf.custom_tree_id
FROM bifrost_org_menu_function bomf,
     bifrost_function t,
     bifrost_function t2
where bomf.function_id = t.id and t.code ='fundFinancingFileBatchDownload' and t2.code  in ('fundFinancingDownload','fundFinancingBatchDownload');

INSERT INTO bifrost_role_menu_function(role_id, menu_id, function_id, custom_tree_id)
SELECT bomf.role_id, bomf.menu_id, t2.id, bomf.custom_tree_id
FROM bifrost_role_menu_function bomf,
     bifrost_function t,
     bifrost_function t2
where bomf.function_id = t.id and t.code ='fundFinancingFileBatchDownload' and t2.code in ('fundFinancingDownload','fundFinancingBatchDownload');


-- ============= 来自文件：20250206_linlili_004_dml.sql ============= 

-- 租后检查
delete from filing_first_level_config where filing_materials_config_id in (select id from filing_materials_config where business_type = 'AFTER_LEASING_FILING' and filing_type  in ('SITE','OFFSITE'));
delete from filing_materials_config where business_type = 'AFTER_LEASING_FILING' and filing_type  in ('SITE','OFFSITE');

INSERT INTO filing_materials_config (filing_type, business_type, enable_flag, remark) VALUES('SITE', 'AFTER_LEASING_FILING', 1, '租后现场检查');
INSERT INTO filing_materials_config (filing_type, business_type, enable_flag, remark) VALUES('OFFSITE', 'AFTER_LEASING_FILING', 1, '租后非现场检查');

INSERT INTO filing_first_level_config (filing_materials_config_id, dir_code, dir_name, fixed_flag, condition_key, sort_code, enable_flag,require_flag) VALUES((select id from filing_materials_config fmc where filing_type ='SITE' and business_type ='AFTER_LEASING_FILING'), 'BASIC_INFORMATION', '租后管理资料清单', 1, NULL, 1, 1,0);
INSERT INTO filing_first_level_config (filing_materials_config_id, dir_code, dir_name, fixed_flag, condition_key, sort_code, enable_flag,require_flag) VALUES((select id from filing_materials_config fmc where filing_type ='SITE' and business_type ='AFTER_LEASING_FILING'), 'ZXYQCWBB', '最新一期财务报表', 1, NULL, 5, 1,0);
INSERT INTO filing_first_level_config (filing_materials_config_id, dir_code, dir_name, fixed_flag, condition_key, sort_code, enable_flag,require_flag) VALUES((select id from filing_materials_config fmc where filing_type ='SITE' and business_type ='AFTER_LEASING_FILING'), 'FRQYZXBG', '法人企业征信报告', 1, NULL, 9, 1,0);
INSERT INTO filing_first_level_config (filing_materials_config_id, dir_code, dir_name, fixed_flag, condition_key, sort_code, enable_flag,require_flag) VALUES((select id from filing_materials_config fmc where filing_type ='SITE' and business_type ='AFTER_LEASING_FILING'), 'ZLWZP', '租赁物照片', 1, NULL, 13, 1,0);
INSERT INTO filing_first_level_config (filing_materials_config_id, dir_code, dir_name, fixed_flag, condition_key, sort_code, enable_flag,require_flag) VALUES((select id from filing_materials_config fmc where filing_type ='SITE' and business_type ='AFTER_LEASING_FILING'), 'KHHY', '与客户工作人员厂区（办公区）合影', 1, NULL, 20, 1,0);
INSERT INTO filing_first_level_config (filing_materials_config_id, dir_code, dir_name, fixed_flag, condition_key, sort_code, enable_flag,require_flag) VALUES((select id from filing_materials_config fmc where filing_type ='SITE' and business_type ='AFTER_LEASING_FILING'), 'ZZSNSSBB', '增值税纳税申报表', 1, NULL, 25, 1,0);
INSERT INTO filing_first_level_config (filing_materials_config_id, dir_code, dir_name, fixed_flag, condition_key, sort_code, enable_flag,require_flag) VALUES((select id from filing_materials_config fmc where filing_type ='SITE' and business_type ='AFTER_LEASING_FILING'), 'RZKSYPZ_MXB_JHB', '融资款使用凭证或相关银行流水、融资明细表/还款计划表', 1, NULL, 27, 1,0);
INSERT INTO filing_first_level_config (filing_materials_config_id, dir_code, dir_name, fixed_flag, condition_key, sort_code, enable_flag,require_flag) VALUES((select id from filing_materials_config fmc where filing_type ='SITE' and business_type ='AFTER_LEASING_FILING'), 'SCBB', '生产型企业生产报表', 1, NULL, 30, 1,0);
INSERT INTO filing_first_level_config (filing_materials_config_id, dir_code, dir_name, fixed_flag, condition_key, sort_code, enable_flag,require_flag) VALUES((select id from filing_materials_config fmc where filing_type ='SITE' and business_type ='AFTER_LEASING_FILING'), 'DFPZHMX', '生产型企业电费凭证或明细', 1, NULL, 34, 1,0);
INSERT INTO filing_first_level_config (filing_materials_config_id, dir_code, dir_name, fixed_flag, condition_key, sort_code, enable_flag,require_flag) VALUES((select id from filing_materials_config fmc where filing_type ='SITE' and business_type ='AFTER_LEASING_FILING'), 'GXZL', '主体资格及章程变更等更新资料', 1, NULL, 37, 1,0);
INSERT INTO filing_first_level_config (filing_materials_config_id, dir_code, dir_name, fixed_flag, condition_key, sort_code, enable_flag,require_flag) VALUES((select id from filing_materials_config fmc where filing_type ='SITE' and business_type ='AFTER_LEASING_FILING'), 'ZRRZXBG', '自然人担保人征信报告', 1, NULL, 40, 1,0);
INSERT INTO filing_first_level_config (filing_materials_config_id, dir_code, dir_name, fixed_flag, condition_key, sort_code, enable_flag,require_flag) VALUES((select id from filing_materials_config fmc where filing_type ='SITE' and business_type ='AFTER_LEASING_FILING'), 'OTHER', '其他资料', 1, NULL, 45, 1,0);
INSERT INTO filing_first_level_config (filing_materials_config_id, dir_code, dir_name, fixed_flag, condition_key, sort_code, enable_flag,require_flag) VALUES((select id from filing_materials_config fmc where filing_type ='SITE' and business_type ='AFTER_LEASING_FILING'), 'ANALYSIS_REPORT', '租后检查分析报告', 1, NULL, 50, 1,0);

INSERT INTO filing_first_level_config (filing_materials_config_id, dir_code, dir_name, fixed_flag, condition_key, sort_code, enable_flag,require_flag) VALUES((select id from filing_materials_config fmc where filing_type ='OFFSITE' and business_type ='AFTER_LEASING_FILING'), 'BASIC_INFORMATION', '租后管理资料清单', 1, NULL, 1, 1,0);
INSERT INTO filing_first_level_config (filing_materials_config_id, dir_code, dir_name, fixed_flag, condition_key, sort_code, enable_flag,require_flag) VALUES((select id from filing_materials_config fmc where filing_type ='OFFSITE' and business_type ='AFTER_LEASING_FILING'), 'ZXYQCWBB', '最新一期财务报表', 1, NULL, 5, 1,0);
INSERT INTO filing_first_level_config (filing_materials_config_id, dir_code, dir_name, fixed_flag, condition_key, sort_code, enable_flag,require_flag) VALUES((select id from filing_materials_config fmc where filing_type ='OFFSITE' and business_type ='AFTER_LEASING_FILING'), 'FRQYZXBG', '法人企业征信报告', 1, NULL, 9, 1,0);
INSERT INTO filing_first_level_config (filing_materials_config_id, dir_code, dir_name, fixed_flag, condition_key, sort_code, enable_flag,require_flag) VALUES((select id from filing_materials_config fmc where filing_type ='OFFSITE' and business_type ='AFTER_LEASING_FILING'), 'ZLWZP', '租赁物照片', 1, NULL, 13, 1,0);
INSERT INTO filing_first_level_config (filing_materials_config_id, dir_code, dir_name, fixed_flag, condition_key, sort_code, enable_flag,require_flag) VALUES((select id from filing_materials_config fmc where filing_type ='OFFSITE' and business_type ='AFTER_LEASING_FILING'), 'KHHY', '与客户工作人员厂区（办公区）合影', 1, NULL, 20, 1,0);
INSERT INTO filing_first_level_config (filing_materials_config_id, dir_code, dir_name, fixed_flag, condition_key, sort_code, enable_flag,require_flag) VALUES((select id from filing_materials_config fmc where filing_type ='OFFSITE' and business_type ='AFTER_LEASING_FILING'), 'ZZSNSSBB', '增值税纳税申报表', 1, NULL, 25, 1,0);
INSERT INTO filing_first_level_config (filing_materials_config_id, dir_code, dir_name, fixed_flag, condition_key, sort_code, enable_flag,require_flag) VALUES((select id from filing_materials_config fmc where filing_type ='OFFSITE' and business_type ='AFTER_LEASING_FILING'), 'RZKSYPZ_MXB_JHB', '融资款使用凭证或相关银行流水、融资明细表/还款计划表', 1, NULL, 27, 1,0);
INSERT INTO filing_first_level_config (filing_materials_config_id, dir_code, dir_name, fixed_flag, condition_key, sort_code, enable_flag,require_flag) VALUES((select id from filing_materials_config fmc where filing_type ='OFFSITE' and business_type ='AFTER_LEASING_FILING'), 'SCBB', '生产型企业生产报表', 1, NULL, 30, 1,0);
INSERT INTO filing_first_level_config (filing_materials_config_id, dir_code, dir_name, fixed_flag, condition_key, sort_code, enable_flag,require_flag) VALUES((select id from filing_materials_config fmc where filing_type ='OFFSITE' and business_type ='AFTER_LEASING_FILING'), 'DFPZHMX', '生产型企业电费凭证或明细', 1, NULL, 34, 1,0);
INSERT INTO filing_first_level_config (filing_materials_config_id, dir_code, dir_name, fixed_flag, condition_key, sort_code, enable_flag,require_flag) VALUES((select id from filing_materials_config fmc where filing_type ='OFFSITE' and business_type ='AFTER_LEASING_FILING'), 'GXZL', '主体资格及章程变更等更新资料', 1, NULL, 37, 1,0);
INSERT INTO filing_first_level_config (filing_materials_config_id, dir_code, dir_name, fixed_flag, condition_key, sort_code, enable_flag,require_flag) VALUES((select id from filing_materials_config fmc where filing_type ='OFFSITE' and business_type ='AFTER_LEASING_FILING'), 'ZRRZXBG', '自然人担保人征信报告', 1, NULL, 40, 1,0);
INSERT INTO filing_first_level_config (filing_materials_config_id, dir_code, dir_name, fixed_flag, condition_key, sort_code, enable_flag,require_flag) VALUES((select id from filing_materials_config fmc where filing_type ='OFFSITE' and business_type ='AFTER_LEASING_FILING'), 'OTHER', '其他资料', 1, NULL, 45, 1,0);
INSERT INTO filing_first_level_config (filing_materials_config_id, dir_code, dir_name, fixed_flag, condition_key, sort_code, enable_flag,require_flag) VALUES((select id from filing_materials_config fmc where filing_type ='OFFSITE' and business_type ='AFTER_LEASING_FILING'), 'ANALYSIS_REPORT', '租后检查分析报告', 1, NULL, 50, 1,0);


delete from bifrost_role_menu_function where function_id in (select id from bifrost_function where code in ('afterFilingMaterialsBatchDownload','afterFilingMaterialsGetOperationsDirDict','afterLeasingFilingFileDownload',
'afterLeasingFilingFileUpload','afterFilingCheckRemove','afterLeasingFilingFileListGroup'));
delete from bifrost_org_menu_function where function_id in (select id from bifrost_function where code in ('afterFilingMaterialsBatchDownload','afterFilingMaterialsGetOperationsDirDict','afterLeasingFilingFileDownload',
'afterLeasingFilingFileUpload','afterFilingCheckRemove'));
delete from bifrost_function where code in ('afterFilingMaterialsBatchDownload','afterFilingMaterialsGetOperationsDirDict','afterLeasingFilingFileDownload',
'afterLeasingFilingFileUpload','afterFilingCheckRemove','afterLeasingFilingFileListGroup');


INSERT INTO bifrost_function(code,name,sort_no,menu_id,method,path,type,group_id)
SELECT 'afterFilingMaterialsBatchDownload','租后资料归档-批量下载',0,b.id,'POST','/after/filingMaterial/batchDownload',2,c.id
FROM bifrost_menu b, gruul_function_group c where b.name = '我收到的' and c.group_describe='我收到的-编辑';
INSERT INTO bifrost_function(code,name,sort_no,menu_id,method,path,type,group_id)
SELECT 'afterFilingMaterialsGetOperationsDirDict','租后资料归档-获取归档资料一级目录',0,b.id,'POST','/after/filingMaterial/getOperationsDirDict',1,c.id
FROM bifrost_menu b, gruul_function_group c where b.name = '我收到的' and c.group_describe='我收到的-查看';
INSERT INTO bifrost_function(code,name,sort_no,menu_id,method,path,type,group_id)
SELECT 'afterFilingCheckRemove','租后资料归档-文件删除',0,b.id,'POST','/after/filingMaterial/file/remove',2,c.id
FROM bifrost_menu b, gruul_function_group c where b.name = '我收到的' and c.group_describe='我收到的-编辑';

INSERT INTO bifrost_function(code,name,sort_no,menu_id,method,path,type,group_id)
SELECT 'afterLeasingFilingFileDownload','租后资料归档-单文件下载',0,b.id,'GET','/file/download',2,c.id
FROM bifrost_menu b, gruul_function_group c where b.name = '我收到的' and c.group_describe='我收到的-编辑';
INSERT INTO bifrost_function(code,name,sort_no,menu_id,method,path,type,group_id)
SELECT 'afterLeasingFilingFileUpload','租后资料归档-文件上传',0,b.id,'POST','/file/upload',2,c.id
FROM bifrost_menu b, gruul_function_group c where b.name = '我收到的' and c.group_describe='我收到的-编辑';
INSERT INTO bifrost_function(code,name,sort_no,menu_id,method,path,type,group_id)
SELECT 'afterLeasingFilingFileListGroup','租后资料归档-获取文件分组',0,b.id,'POST','/file/list/group',1,c.id
FROM bifrost_menu b, gruul_function_group c where b.name = '我收到的' and c.group_describe='我收到的-查看';



INSERT INTO bifrost_org_menu_function(org_id, menu_id, function_id, custom_tree_id)
SELECT org.id, menu.id, function.id, tree.id
FROM bifrost_org org,
     bifrost_function function,
     bifrost_menu menu,
     bifrost_custom_tree tree
where org.code in ('ZSZL','YYGLB','FLHGB_ZCBQ')
  and function.code in ('afterFilingMaterialsBatchDownload','afterFilingMaterialsGetOperationsDirDict','afterLeasingFilingFileDownload',
'afterLeasingFilingFileDownload','afterFilingCheckRemove','afterLeasingFilingFileListGroup')
  and menu.name = '我收到的'
  and tree.name = '我的流程';



INSERT INTO bifrost_role_menu_function(role_id, menu_id, function_id, custom_tree_id)
SELECT role.id, menu.id, function.id, tree.id
FROM bifrost_role role,
     bifrost_function function,
     bifrost_menu menu,
     bifrost_custom_tree tree
WHERE role.code IN ('ZCGL','YYGLB-TYCK','YYGLB-JBR','YYGLB_YYJL')
  and function.code in ('afterFilingMaterialsBatchDownload','afterFilingMaterialsGetOperationsDirDict','afterLeasingFilingFileDownload',
'afterLeasingFilingFileUpload','afterFilingCheckRemove','afterLeasingFilingFileListGroup')
  and menu.name = '我收到的'
  and tree.name = '我的流程';

INSERT INTO bifrost_role_menu_function(role_id, menu_id, function_id, custom_tree_id)
SELECT role.id, menu.id, function.id, tree.id
FROM bifrost_role role,
     bifrost_function function,
     bifrost_menu menu,
     bifrost_custom_tree tree
WHERE role.code IN ('ADMIN_SUPERADMIN','CHAKAN','COMPREHENSIVE_MANAGEMENT','readAdmin','GLY')
  and function.code in ('afterFilingMaterialsBatchDownload','afterFilingMaterialsGetOperationsDirDict','afterLeasingFilingFileDownload','afterLeasingFilingFileListGroup')
  and menu.name = '我收到的'
  and tree.name = '我的流程';


-- ============= 来自文件：20250206_linlili_005_dml.sql ============= 

-- 项目资料归档首岗提交时间
update
	filing_materials t
set
	t.first_commit_date = (
	select or2.gmt_create from operate_record or2 where or2.process_instance_id = t.flow_Id and or2.type ='QD')
where
	t.filing_type = 'BUSINESS_MATERIALS'
	and exists (
	select
		1
	from
		operate_record or3
	where or3.process_instance_id = t.flow_Id and or3.type ='QD');

-- 待办推送时间 保留时分秒
update
	filing_materials t
set
	t.start_date = t.create_time
where
	t.filing_type = 'BUSINESS_MATERIALS';

update filing_materials fm set dept_id = (select biz_dept_id  from contract_base_info cbi where cbi.id = fm.contract_id )
where filing_type = 'BUSINESS_MATERIALS'
and exists(select 1 from contract_base_info cbi where cbi.id = fm.contract_id );


update filing_materials t set t.user_Id = (select proj_sponsor_user_id  from contract_base_info cbi where cbi.id = t.contract_id)
where filing_type = 'BUSINESS_MATERIALS';




-- ============= 来自文件：20250206_linlili_006_dml.sql ============= 

delete from gruul_user_org_role where role_id = (select id from bifrost_role where code  = 'FLHGB-ZCJL');
delete from bifrost_role where code  = 'FLHGB-ZCJL';

INSERT INTO bifrost_role (code, name, `type`, org_id) VALUES('FLHGB-ZCJL', '资产经理', 'default', (select id from bifrost_org  where CODE ='FLHGB_ZCBQ'));

INSERT INTO gruul_user_org_role(user_id, org_id, role_Id)
SELECT guoj.user_id, bo.id, br.id
FROM gruul_user_org_job guoj,
     bifrost_org bo,
     bifrost_role br
WHERE guoj.job_code ='assetmanagement'
  and bo.code = 'FLHGB_ZCBQ' and guoj.org_id = bo.id
  and br.code = 'FLHGB-ZCJL';

delete from bifrost_custom_tree_menu_ref where menu_id = (select id from bifrost_menu where code ='archivesOtherFilingMaterials');
delete from gruul_function_group where menu_id =(select id from bifrost_menu where code ='archivesOtherFilingMaterials');
delete from bifrost_menu where code ='archivesOtherFilingMaterials';

INSERT INTO bifrost_menu (code, `level`, sort_no, `type`, `path`, parent_id, icon, name) VALUES('archivesOtherFilingMaterials', 3, 0, '0', '/archives/otherFilingMaterials', NULL, NULL, '其他资料归档');
INSERT INTO bifrost_custom_tree_menu_ref (custom_tree_id, menu_id, sort_no) VALUES( ( select id from bifrost_custom_tree bct where code ='archivesmanage'), (select id from bifrost_menu where code ='archivesOtherFilingMaterials'), 3);
INSERT INTO gruul_function_group ( sort_no, code, name, group_describe, menu_id) VALUES( 1, 'otherFilingMaterials-Read', '其他资料归档', '其他资料归档-查看', (select id from bifrost_menu where code ='archivesOtherFilingMaterials'));
INSERT INTO gruul_function_group ( sort_no, code, name, group_describe, menu_id) VALUES( 2, 'otherFilingMaterials-Edit', '其他资料归档', '其他资料归档-编辑', (select id from bifrost_menu where code ='archivesOtherFilingMaterials'));
    
delete from filing_first_level_config where filing_materials_config_id in (select id from filing_materials_config where business_type = 'OTHER_FILING' and filing_type  ='OTHER');
delete from filing_materials_config where business_type = 'OTHER_FILING' and filing_type  ='OTHER';

INSERT INTO filing_materials_config (filing_type, business_type, enable_flag, remark) VALUES('OTHER', 'OTHER_FILING', 1, '其他资料归档');

INSERT INTO filing_first_level_config (filing_materials_config_id, dir_code, dir_name, fixed_flag, condition_key, sort_code, enable_flag,require_flag) VALUES((select id from filing_materials_config fmc where filing_type ='OTHER' and business_type ='OTHER_FILING'), 'BASIC_INFORMATION', '其他资料清单', 1, NULL, 1, 1,0);
INSERT INTO filing_first_level_config (filing_materials_config_id, dir_code, dir_name, fixed_flag, condition_key, sort_code, enable_flag,require_flag) VALUES((select id from filing_materials_config fmc where filing_type ='OTHER' and business_type ='OTHER_FILING'), 'OTHER_MATERIALS', '其他文件', 1, NULL, 99, 1,0);

delete from bifrost_role_menu_function where function_id in (select id from bifrost_function
where code in ('otherFilingMaterialsBatchDownload','otherFilingMaterialsGetOperationsDirDict','otherFilingCheckRemove',
'otherFilingSelectConfirm','otherFilingCancel','otherFilingCommit','otherFilingFileDownload','otherFilingFileUpload',
'otherFilingFileListGroup','otherFilingFileRemove','otherFilingGetReviewProject','otherFilingMaterialsList','otherFilingGetMaterialsDesc',
'otherFilingSaveMaterialsDesc','otherFilingMaterialsExport','otherFilingCheckMaterialsDesc'));
delete from bifrost_org_menu_function where function_id in (select id from bifrost_function where code in ('otherFilingMaterialsBatchDownload','otherFilingMaterialsGetOperationsDirDict','otherFilingCheckRemove',
'otherFilingSelectConfirm','otherFilingCancel','otherFilingCommit','otherFilingFileDownload','otherFilingFileUpload',
'otherFilingFileListGroup','otherFilingFileRemove','otherFilingGetReviewProject','otherFilingMaterialsList','otherFilingGetMaterialsDesc',
'otherFilingSaveMaterialsDesc','otherFilingMaterialsExport','otherFilingCheckMaterialsDesc'));
delete from bifrost_function where code in ('otherFilingMaterialsBatchDownload','otherFilingMaterialsGetOperationsDirDict','otherFilingCheckRemove',
'otherFilingSelectConfirm','otherFilingCancel','otherFilingCommit','otherFilingFileDownload','otherFilingFileUpload',
'otherFilingFileListGroup','otherFilingFileRemove','otherFilingGetReviewProject','otherFilingMaterialsList','otherFilingGetMaterialsDesc',
'otherFilingSaveMaterialsDesc','otherFilingMaterialsExport','otherFilingCheckMaterialsDesc');


INSERT INTO bifrost_function(code,name,sort_no,menu_id,method,path,type,group_id)
SELECT 'otherFilingMaterialsBatchDownload','其他资料归档-批量下载',0,b.id,'POST','/other/filingMaterial/batchDownload',2,c.id
FROM bifrost_menu b, gruul_function_group c where b.name = '其他资料归档' and c.group_describe='其他资料归档-编辑';
INSERT INTO bifrost_function(code,name,sort_no,menu_id,method,path,type,group_id)
SELECT 'otherFilingMaterialsGetOperationsDirDict','其他资料归档-获取归档资料一级目录',0,b.id,'POST','/other/filingMaterial/getOperationsDirDict',1,c.id
FROM bifrost_menu b, gruul_function_group c where b.name = '其他资料归档' and c.group_describe='其他资料归档-查看';
INSERT INTO bifrost_function(code,name,sort_no,menu_id,method,path,type,group_id)
SELECT 'otherFilingCheckRemove','其他资料归档-文件删除',0,b.id,'POST','/other/filingMaterial/file/remove',2,c.id
FROM bifrost_menu b, gruul_function_group c where b.name = '其他资料归档' and c.group_describe='其他资料归档-编辑';
INSERT INTO bifrost_function(code,name,sort_no,menu_id,method,path,type,group_id)
SELECT 'otherFilingSelectConfirm','其他资料归档-项目确认',0,b.id,'GET','/other/filingMaterial/select/confirm',2,c.id
FROM bifrost_menu b, gruul_function_group c where b.name = '其他资料归档' and c.group_describe='其他资料归档-编辑';
INSERT INTO bifrost_function(code,name,sort_no,menu_id,method,path,type,group_id)
SELECT 'otherFilingCancel','其他资料归档-取消操作',0,b.id,'GET','/other/filingMaterial/cancel',2,c.id
FROM bifrost_menu b, gruul_function_group c where b.name = '其他资料归档' and c.group_describe='其他资料归档-编辑';
INSERT INTO bifrost_function(code,name,sort_no,menu_id,method,path,type,group_id)
SELECT 'otherFilingCommit','其他资料归档-提交审批',0,b.id,'POST','/other/filingMaterial/commit',2,c.id
FROM bifrost_menu b, gruul_function_group c where b.name = '其他资料归档' and c.group_describe='其他资料归档-编辑';
INSERT INTO bifrost_function(code,name,sort_no,menu_id,method,path,type,group_id)
SELECT 'otherFilingGetReviewProject','其他资料归档-获取项目评审项目',0,b.id,'POST','/other/filingMaterial/getReviewProject',2,c.id
FROM bifrost_menu b, gruul_function_group c where b.name = '其他资料归档' and c.group_describe='其他资料归档-编辑';

INSERT INTO bifrost_function(code,name,sort_no,menu_id,method,path,type,group_id)
SELECT 'otherFilingFileDownload','其他资料归档-单文件下载',0,b.id,'GET','/file/download',2,c.id
FROM bifrost_menu b, gruul_function_group c where b.name = '其他资料归档' and c.group_describe='其他资料归档-编辑';
INSERT INTO bifrost_function(code,name,sort_no,menu_id,method,path,type,group_id)
SELECT 'otherFilingFileUpload','其他资料归档-文件上传',0,b.id,'POST','/file/upload',2,c.id
FROM bifrost_menu b, gruul_function_group c where b.name = '其他资料归档' and c.group_describe='其他资料归档-编辑';
INSERT INTO bifrost_function(code,name,sort_no,menu_id,method,path,type,group_id)
SELECT 'otherFilingFileListGroup','其他资料归档-获取文件分组',0,b.id,'POST','/file/list/group',1,c.id
FROM bifrost_menu b, gruul_function_group c where b.name = '其他资料归档' and c.group_describe='其他资料归档-查看';
INSERT INTO bifrost_function(code,name,sort_no,menu_id,method,path,type,group_id)
SELECT 'otherFilingFileRemove','其他资料归档-非基础资料文件删除',0,b.id,'POST','/file/remove',1,c.id
FROM bifrost_menu b, gruul_function_group c where b.name = '其他资料归档' and c.group_describe='其他资料归档-查看';
INSERT INTO bifrost_function(code,name,sort_no,menu_id,method,path,type,group_id)
SELECT 'otherFilingGetMaterialsDesc','其他资料归档-获取资料类型',0,b.id,'POST','/other/filingMaterial/getMaterialsDesc',2,c.id
FROM bifrost_menu b, gruul_function_group c where b.name = '其他资料归档' and c.group_describe='其他资料归档-查看';
INSERT INTO bifrost_function(code,name,sort_no,menu_id,method,path,type,group_id)
SELECT 'otherFilingSaveMaterialsDesc','其他资料归档-获取资料类型',0,b.id,'POST','/other/filingMaterial/saveMaterialsDesc',1,c.id
FROM bifrost_menu b, gruul_function_group c where b.name = '其他资料归档' and c.group_describe='其他资料归档-编辑';
INSERT INTO bifrost_function(code,name,sort_no,menu_id,method,path,type,group_id)
SELECT 'otherFilingCheckMaterialsDesc','其他资料归档-校验资料类型',0,b.id,'POST','/other/filingMaterial/checkMaterialsDesc',1,c.id
FROM bifrost_menu b, gruul_function_group c where b.name = '其他资料归档' and c.group_describe='其他资料归档-查看';

INSERT INTO bifrost_function(code,name,sort_no,menu_id,method,path,type,group_id)
SELECT 'otherFilingMaterialsList','其他资料归档台账-列表展示',0,b.id,'POST','/other/filingMaterial/list',2,c.id
FROM bifrost_menu b, gruul_function_group c where b.name = '其他资料归档' and c.group_describe='其他资料归档-查看';

INSERT INTO bifrost_function(code,name,sort_no,menu_id,method,path,type,group_id)
SELECT 'otherFilingMaterialsExport','其他资料归档台账-批量导出',0,b.id,'POST','/other/filingMaterial/export',2,c.id
FROM bifrost_menu b, gruul_function_group c where b.name = '其他资料归档' and c.group_describe='其他资料归档-查看';

INSERT INTO bifrost_org_menu_function(org_id, menu_id, function_id, custom_tree_id)
SELECT org.id, menu.id, function.id, tree.id
FROM bifrost_org org,
     bifrost_function function,
     bifrost_menu menu,
     bifrost_custom_tree tree
where org.code in ('ZSZL','YYGLB','FLHGB_ZCBQ','YYGLB','JSSYB','HGJCYWB','JXHJGYWB','JCSSYWB','LLWLTD','XJZZHXJJTD','JTYSYWB','GGSY','HYYWB','XNYYWB','SYCYWB','LSCYYWB','XMPSWYH')
  and function.code in ('otherFilingMaterialsBatchDownload','otherFilingMaterialsGetOperationsDirDict','otherFilingCheckRemove',
'otherFilingSelectConfirm','otherFilingCancel','otherFilingCommit','otherFilingFileDownload','otherFilingFileUpload','otherFilingFileListGroup',
'otherFilingFileRemove','otherFilingGetReviewProject','otherFilingMaterialsList','otherFilingGetMaterialsDesc',
'otherFilingSaveMaterialsDesc','otherFilingMaterialsExport','otherFilingCheckMaterialsDesc')
  and menu.name = '其他资料归档'
  and tree.name = '档案管理';



INSERT INTO bifrost_role_menu_function(role_id, menu_id, function_id, custom_tree_id)
SELECT role.id, menu.id, function.id, tree.id
FROM bifrost_role role,
     bifrost_function function,
     bifrost_menu menu,
     bifrost_custom_tree tree
WHERE role.code IN ('XMJL','FLHBG-FWJL','YYGLB_YYJL','FLHGB-ZCJL','PSHMS')
  and function.code in ('otherFilingMaterialsBatchDownload','otherFilingMaterialsGetOperationsDirDict','otherFilingCheckRemove',
'otherFilingSelectConfirm','otherFilingCancel','otherFilingCommit','otherFilingFileDownload','otherFilingFileUpload','otherFilingFileListGroup',
'otherFilingFileRemove','otherFilingGetReviewProject','otherFilingMaterialsList','otherFilingGetMaterialsDesc',
'otherFilingMaterialsExport','otherFilingCheckMaterialsDesc')
  and menu.name = '其他资料归档'
  and tree.name = '档案管理';

INSERT INTO bifrost_role_menu_function(role_id, menu_id, function_id, custom_tree_id)
SELECT role.id, menu.id, function.id, tree.id
FROM bifrost_role role,
     bifrost_function function,
     bifrost_menu menu,
     bifrost_custom_tree tree
WHERE role.code IN ('FLHGB-FZR','YYGLB-TYCK','YYGLB-JBR')
  and function.code in ('otherFilingMaterialsBatchDownload','otherFilingMaterialsGetOperationsDirDict','otherFilingCheckRemove',
  'otherFilingCancel','otherFilingFileDownload','otherFilingFileUpload','otherFilingFileListGroup',
'otherFilingFileRemove','otherFilingGetReviewProject','otherFilingMaterialsList','otherFilingGetMaterialsDesc',
'otherFilingSaveMaterialsDesc','otherFilingMaterialsExport','otherFilingCheckMaterialsDesc')
  and menu.name = '其他资料归档'
  and tree.name = '档案管理';

INSERT INTO bifrost_role_menu_function(role_id, menu_id, function_id, custom_tree_id)
SELECT role.id, menu.id, function.id, tree.id
FROM bifrost_role role,
     bifrost_function function,
     bifrost_menu menu,
     bifrost_custom_tree tree
WHERE role.code IN ('ADMIN_SUPERADMIN','CHAKAN','COMPREHENSIVE_MANAGEMENT','readAdmin','GLY')
  and function.code in ('otherFilingGetReviewProject','otherFilingMaterialsBatchDownload','otherFilingMaterialsGetOperationsDirDict','otherFilingFileDownload','otherFilingFileListGroup','otherFilingMaterialsList','otherFilingGetMaterialsDesc','otherFilingMaterialsExport','otherFilingCheckMaterialsDesc')
  and menu.name = '其他资料归档'
  and tree.name = '档案管理';


-- ============= 来自文件：20260206_gxy_002_dml.sql ============= 

-- 新增档案管理台账菜单
set @menuCode = 'archivesmanagement';
set @treeName = '档案管理';
set @menuId = (select id from bifrost_menu where code = @menuCode);

-- 可重复执行
delete from bifrost_custom_tree_menu_ref where menu_id = @menuId;
delete from bifrost_org_menu_function  where menu_id = @menuId;
delete from bifrost_menu where code = @menuCode;

-- 插入菜单
INSERT INTO bifrost_menu (code, level, sort_no, type, path, parent_id, icon, name, en_name, target, create_by, update_by)
VALUES (@menuCode, 3, 0, null, '/archives/management', null, null, '档案管理', null, '_self', null, null);

set @menuId = (select id from bifrost_menu where code = @menuCode);

-- 插入菜单关联
set @treeId = (select id from bifrost_custom_tree where name = @treeName);
INSERT INTO bifrost_custom_tree_menu_ref (custom_tree_id, menu_id, sort_no) VALUES (@treeId, @menuId, 0);

-- 插入接口分组（按操作分查看/编辑）
set @groupCode1 = 'archivesmanagement-Read';
set @groupCode2 = 'archivesmanagement-Write';
delete from gruul_function_group where code = @groupCode1;
delete from gruul_function_group where code = @groupCode2;
INSERT INTO gruul_function_group (sort_no, code, name, group_describe, menu_id) VALUES ( 0, @groupCode1, '档案管理-查看', '档案管理-查看', @menuId);
set @groupId1 = (select id from gruul_function_group where code = @groupCode1);
INSERT INTO gruul_function_group (sort_no, code, name, group_describe, menu_id) VALUES ( 0, @groupCode2, '档案管理-编辑', '档案管理-编辑', @menuId);
set @groupId2 = (select id from gruul_function_group where code = @groupCode2);

-- 插入功能接口
set @fun1 = 'fundSideArchivedMaterialsQuery';
delete from bifrost_function where code = @fun1;
insert into bifrost_function (code, name, sort_no, menu_id, method, path, type,group_id)
values  (@fun1, '资金端归档资料-列表查询', 0, @menuId, 'POST','/documentManagementLedger/fund/list/query', 2,@groupId1);
set @funId1 = (select id from bifrost_function where code =@fun1);

set @fun2 = 'fundSideArchivedMaterialsBatchDownload';
delete from bifrost_function where code = @fun2;
insert into bifrost_function (code, name, sort_no, menu_id, method, path, type,group_id)
values  (@fun2, '资金端归档资料-批量下载', 0, @menuId, 'POST','/documentManagementLedger/fund/batch/download', 1,@groupId2);
set @funId2 = (select id from bifrost_function where code =@fun2);

set @fun3 = 'fundSideArchivedMaterialsDownloadRecordsQuery';
delete from bifrost_function where code = @fun3;
insert into bifrost_function (code, name, sort_no, menu_id, method, path, type,group_id)
values  (@fun3, '资金端归档资料-下载记录查询', 0, @menuId, 'POST','/documentManagementLedger/fund/download/records/query', 2,@groupId1);
set @funId3 = (select id from bifrost_function where code =@fun3);

set @fun4 = 'projSideArchivedDocumentQuery';
delete from bifrost_function where code = @fun4;
insert into bifrost_function (code, name, sort_no, menu_id, method, path, type,group_id)
values  (@fun4, '项目端归档资料-列表查询', 0, @menuId, 'POST','/documentManagementLedger/proj/list/query', 2,@groupId1);
set @funId4 = (select id from bifrost_function where code =@fun4);

set @fun5 = 'materialsdger-proj-clientList';
delete from bifrost_function where code = @fun5;
insert into bifrost_function (code, name, sort_no, menu_id, method, path, type,group_id)
values  (@fun5, '归档资料台账-项目端-客户查询', 0, @menuId, 'POST','/client/list', 2,@groupId1);
set @funId5 = (select id from bifrost_function where code =@fun5);

set @orgId1 = (select id from bifrost_org where code = 'ZSZL');
set @orgId2 = (select id from bifrost_org where code = 'LDC');
set @orgId3 = (select id from bifrost_org where code = 'ZJGLB');
set @orgId4 = (select id from bifrost_org where code = 'YYGLB');
-- 给公司和部门添加权限
delete from bifrost_org_menu_function where menu_id = @menuId;
insert into bifrost_org_menu_function (org_id, menu_id, function_id,  custom_tree_id)
values  ( @orgId1, @menuId, null,  @treeId),
        ( @orgId1, @menuId, @funId1, @treeId),
        ( @orgId1, @menuId, @funId2, @treeId),
        ( @orgId1, @menuId, @funId3, @treeId),
        ( @orgId1, @menuId, @funId4, @treeId),
        -- 领导层
        ( @orgId2, @menuId, null,  @treeId),
        ( @orgId2, @menuId, @funId1, @treeId),
        ( @orgId2, @menuId, @funId2, @treeId),
        ( @orgId2, @menuId, @funId3, @treeId),
        ( @orgId2, @menuId, @funId4, @treeId),
        -- 资金管理部
        ( @orgId3, @menuId, null,  @treeId),
        ( @orgId3, @menuId, @funId1, @treeId),
        ( @orgId3, @menuId, @funId2, @treeId),
        ( @orgId3, @menuId, @funId3, @treeId),
        -- 运营管理部
        ( @orgId4, @menuId, null,  @treeId),
        ( @orgId4, @menuId, @funId4, @treeId),
        ( @orgId4, @menuId, @funId5, @treeId);


-- 插入角色菜单功能
delete from bifrost_role_menu_function where menu_id = @menuId;
insert into bifrost_role_menu_function (role_id, menu_id, function_id, custom_tree_id)
values
    ((select id from bifrost_role where code ='ADMIN_SUPERADMIN'),@menuId, @funId1,  @treeId), -- 超级管理员
    ((select id from bifrost_role where code ='ADMIN_SUPERADMIN'),@menuId, @funId4,  @treeId),
    ((select id from bifrost_role where code ='ADMIN_SUPERADMIN'),@menuId, @funId5,  @treeId),
    ((select id from bifrost_role where code ='XMJL'),@menuId, null,  @treeId), -- 项目经理
    ((select id from bifrost_role where code ='FLHGB-ZCJL'),@menuId, null,  @treeId), -- 资产经理
    ((select id from bifrost_role where code ='FLHBG-FWJL'),@menuId, null,  @treeId), -- 法律合规部-法务经理
    ((select id from bifrost_role where code ='YYGLB-TYCK'),@menuId, null,  @treeId), -- 运营管理部-通用查看
    ((select id from bifrost_role where code ='YYGLB-TYCK'),@menuId, @funId4,  @treeId),
    ((select id from bifrost_role where code ='YYGLB-TYCK'),@menuId, @funId5,  @treeId),
    ((select id from bifrost_role where code ='ZJGLB-ZJGL'),@menuId, @funId1,  @treeId), -- 资金管理部
    ((select id from bifrost_role where code ='ZJGLB-ZJGL'),@menuId, @funId2,  @treeId),
    ((select id from bifrost_role where code ='ZJGLB-ZJGL'),@menuId, @funId3,  @treeId);

-- 隐藏现有档案管理、归档任务模板管理二级菜单
delete from bifrost_role_menu_function where menu_id in (select id from bifrost_menu where code in ('archivestask','archivesmanage'));



-- ============= 来自文件：20250206_linlili_007_dml.sql ============= 

delete from gruul_user_org_role where role_id = (select id from bifrost_role where code  = 'FLHGB-ZCJL');
delete from bifrost_role where code  = 'FLHGB-ZCJL';

INSERT INTO bifrost_role (code, name, `type`, org_id) VALUES('FLHGB-ZCJL', '资产经理', 'default', (select id from bifrost_org  where CODE ='FLHGB_ZCBQ'));

INSERT INTO gruul_user_org_role(user_id, org_id, role_Id)
SELECT guoj.user_id, bo.id, br.id
FROM gruul_user_org_job guoj,
     bifrost_org bo,
     bifrost_role br
WHERE guoj.job_code ='assetmanagement'
  and bo.code = 'FLHGB_ZCBQ' and guoj.org_id = bo.id
  and br.code = 'FLHGB-ZCJL';
-- 租后检查
delete from filing_first_level_config where filing_materials_config_id in (select id from filing_materials_config where business_type = 'AFTER_LEASING_FILING' and filing_type  in ('SITE','OFFSITE'));
delete from filing_materials_config where business_type = 'AFTER_LEASING_FILING' and filing_type  in ('SITE','OFFSITE');

INSERT INTO filing_materials_config (filing_type, business_type, enable_flag, remark) VALUES('SITE', 'AFTER_LEASING_FILING', 1, '租后现场检查');
INSERT INTO filing_materials_config (filing_type, business_type, enable_flag, remark) VALUES('OFFSITE', 'AFTER_LEASING_FILING', 1, '租后非现场检查');

INSERT INTO filing_first_level_config (filing_materials_config_id, dir_code, dir_name, fixed_flag, condition_key, sort_code, enable_flag,require_flag) VALUES((select id from filing_materials_config fmc where filing_type ='SITE' and business_type ='AFTER_LEASING_FILING'), 'BASIC_INFORMATION', '租后管理资料清单', 1, NULL, 1, 1,0);
INSERT INTO filing_first_level_config (filing_materials_config_id, dir_code, dir_name, fixed_flag, condition_key, sort_code, enable_flag,require_flag) VALUES((select id from filing_materials_config fmc where filing_type ='SITE' and business_type ='AFTER_LEASING_FILING'), 'ZXYQCWBB', '最新一期财务报表', 1, NULL, 5, 1,0);
INSERT INTO filing_first_level_config (filing_materials_config_id, dir_code, dir_name, fixed_flag, condition_key, sort_code, enable_flag,require_flag) VALUES((select id from filing_materials_config fmc where filing_type ='SITE' and business_type ='AFTER_LEASING_FILING'), 'FRQYZXBG', '法人企业征信报告', 1, NULL, 9, 1,0);
INSERT INTO filing_first_level_config (filing_materials_config_id, dir_code, dir_name, fixed_flag, condition_key, sort_code, enable_flag,require_flag) VALUES((select id from filing_materials_config fmc where filing_type ='SITE' and business_type ='AFTER_LEASING_FILING'), 'ZLWZP', '租赁物照片', 1, NULL, 13, 1,0);
INSERT INTO filing_first_level_config (filing_materials_config_id, dir_code, dir_name, fixed_flag, condition_key, sort_code, enable_flag,require_flag) VALUES((select id from filing_materials_config fmc where filing_type ='SITE' and business_type ='AFTER_LEASING_FILING'), 'KHHY', '与客户工作人员厂区（办公区）合影', 1, NULL, 20, 1,0);
INSERT INTO filing_first_level_config (filing_materials_config_id, dir_code, dir_name, fixed_flag, condition_key, sort_code, enable_flag,require_flag) VALUES((select id from filing_materials_config fmc where filing_type ='SITE' and business_type ='AFTER_LEASING_FILING'), 'ZZSNSSBB', '增值税纳税申报表', 1, NULL, 25, 1,0);
INSERT INTO filing_first_level_config (filing_materials_config_id, dir_code, dir_name, fixed_flag, condition_key, sort_code, enable_flag,require_flag) VALUES((select id from filing_materials_config fmc where filing_type ='SITE' and business_type ='AFTER_LEASING_FILING'), 'RZKSYPZ_MXB_JHB', '融资款使用凭证或相关银行流水、融资明细表/还款计划表', 1, NULL, 27, 1,0);
INSERT INTO filing_first_level_config (filing_materials_config_id, dir_code, dir_name, fixed_flag, condition_key, sort_code, enable_flag,require_flag) VALUES((select id from filing_materials_config fmc where filing_type ='SITE' and business_type ='AFTER_LEASING_FILING'), 'SCBB', '生产型企业生产报表', 1, NULL, 30, 1,0);
INSERT INTO filing_first_level_config (filing_materials_config_id, dir_code, dir_name, fixed_flag, condition_key, sort_code, enable_flag,require_flag) VALUES((select id from filing_materials_config fmc where filing_type ='SITE' and business_type ='AFTER_LEASING_FILING'), 'DFPZHMX', '生产型企业电费凭证或明细', 1, NULL, 34, 1,0);
INSERT INTO filing_first_level_config (filing_materials_config_id, dir_code, dir_name, fixed_flag, condition_key, sort_code, enable_flag,require_flag) VALUES((select id from filing_materials_config fmc where filing_type ='SITE' and business_type ='AFTER_LEASING_FILING'), 'GXZL', '主体资格及章程变更等更新资料', 1, NULL, 37, 1,0);
INSERT INTO filing_first_level_config (filing_materials_config_id, dir_code, dir_name, fixed_flag, condition_key, sort_code, enable_flag,require_flag) VALUES((select id from filing_materials_config fmc where filing_type ='SITE' and business_type ='AFTER_LEASING_FILING'), 'ZRRZXBG', '自然人担保人征信报告', 1, NULL, 40, 1,0);
INSERT INTO filing_first_level_config (filing_materials_config_id, dir_code, dir_name, fixed_flag, condition_key, sort_code, enable_flag,require_flag) VALUES((select id from filing_materials_config fmc where filing_type ='SITE' and business_type ='AFTER_LEASING_FILING'), 'OTHER', '其他资料', 1, NULL, 45, 1,0);
INSERT INTO filing_first_level_config (filing_materials_config_id, dir_code, dir_name, fixed_flag, condition_key, sort_code, enable_flag,require_flag) VALUES((select id from filing_materials_config fmc where filing_type ='SITE' and business_type ='AFTER_LEASING_FILING'), 'ANALYSIS_REPORT', '租后检查分析报告', 1, NULL, 50, 1,0);

INSERT INTO filing_first_level_config (filing_materials_config_id, dir_code, dir_name, fixed_flag, condition_key, sort_code, enable_flag,require_flag) VALUES((select id from filing_materials_config fmc where filing_type ='OFFSITE' and business_type ='AFTER_LEASING_FILING'), 'BASIC_INFORMATION', '租后管理资料清单', 1, NULL, 1, 1,0);
INSERT INTO filing_first_level_config (filing_materials_config_id, dir_code, dir_name, fixed_flag, condition_key, sort_code, enable_flag,require_flag) VALUES((select id from filing_materials_config fmc where filing_type ='OFFSITE' and business_type ='AFTER_LEASING_FILING'), 'ZXYQCWBB', '最新一期财务报表', 1, NULL, 5, 1,0);
INSERT INTO filing_first_level_config (filing_materials_config_id, dir_code, dir_name, fixed_flag, condition_key, sort_code, enable_flag,require_flag) VALUES((select id from filing_materials_config fmc where filing_type ='OFFSITE' and business_type ='AFTER_LEASING_FILING'), 'FRQYZXBG', '法人企业征信报告', 1, NULL, 9, 1,0);
INSERT INTO filing_first_level_config (filing_materials_config_id, dir_code, dir_name, fixed_flag, condition_key, sort_code, enable_flag,require_flag) VALUES((select id from filing_materials_config fmc where filing_type ='OFFSITE' and business_type ='AFTER_LEASING_FILING'), 'ZLWZP', '租赁物照片', 1, NULL, 13, 1,0);
INSERT INTO filing_first_level_config (filing_materials_config_id, dir_code, dir_name, fixed_flag, condition_key, sort_code, enable_flag,require_flag) VALUES((select id from filing_materials_config fmc where filing_type ='OFFSITE' and business_type ='AFTER_LEASING_FILING'), 'KHHY', '与客户工作人员厂区（办公区）合影', 1, NULL, 20, 1,0);
INSERT INTO filing_first_level_config (filing_materials_config_id, dir_code, dir_name, fixed_flag, condition_key, sort_code, enable_flag,require_flag) VALUES((select id from filing_materials_config fmc where filing_type ='OFFSITE' and business_type ='AFTER_LEASING_FILING'), 'ZZSNSSBB', '增值税纳税申报表', 1, NULL, 25, 1,0);
INSERT INTO filing_first_level_config (filing_materials_config_id, dir_code, dir_name, fixed_flag, condition_key, sort_code, enable_flag,require_flag) VALUES((select id from filing_materials_config fmc where filing_type ='OFFSITE' and business_type ='AFTER_LEASING_FILING'), 'RZKSYPZ_MXB_JHB', '融资款使用凭证或相关银行流水、融资明细表/还款计划表', 1, NULL, 27, 1,0);
INSERT INTO filing_first_level_config (filing_materials_config_id, dir_code, dir_name, fixed_flag, condition_key, sort_code, enable_flag,require_flag) VALUES((select id from filing_materials_config fmc where filing_type ='OFFSITE' and business_type ='AFTER_LEASING_FILING'), 'SCBB', '生产型企业生产报表', 1, NULL, 30, 1,0);
INSERT INTO filing_first_level_config (filing_materials_config_id, dir_code, dir_name, fixed_flag, condition_key, sort_code, enable_flag,require_flag) VALUES((select id from filing_materials_config fmc where filing_type ='OFFSITE' and business_type ='AFTER_LEASING_FILING'), 'DFPZHMX', '生产型企业电费凭证或明细', 1, NULL, 34, 1,0);
INSERT INTO filing_first_level_config (filing_materials_config_id, dir_code, dir_name, fixed_flag, condition_key, sort_code, enable_flag,require_flag) VALUES((select id from filing_materials_config fmc where filing_type ='OFFSITE' and business_type ='AFTER_LEASING_FILING'), 'GXZL', '主体资格及章程变更等更新资料', 1, NULL, 37, 1,0);
INSERT INTO filing_first_level_config (filing_materials_config_id, dir_code, dir_name, fixed_flag, condition_key, sort_code, enable_flag,require_flag) VALUES((select id from filing_materials_config fmc where filing_type ='OFFSITE' and business_type ='AFTER_LEASING_FILING'), 'ZRRZXBG', '自然人担保人征信报告', 1, NULL, 40, 1,0);
INSERT INTO filing_first_level_config (filing_materials_config_id, dir_code, dir_name, fixed_flag, condition_key, sort_code, enable_flag,require_flag) VALUES((select id from filing_materials_config fmc where filing_type ='OFFSITE' and business_type ='AFTER_LEASING_FILING'), 'OTHER', '其他资料', 1, NULL, 45, 1,0);
INSERT INTO filing_first_level_config (filing_materials_config_id, dir_code, dir_name, fixed_flag, condition_key, sort_code, enable_flag,require_flag) VALUES((select id from filing_materials_config fmc where filing_type ='OFFSITE' and business_type ='AFTER_LEASING_FILING'), 'ANALYSIS_REPORT', '租后检查分析报告', 1, NULL, 50, 1,0);


delete from bifrost_role_menu_function where function_id in (select id from bifrost_function where code in ('afterFilingMaterialsBatchDownload','afterFilingMaterialsGetOperationsDirDict','afterLeasingFilingFileDownload',
'afterLeasingFilingFileUpload','afterFilingCheckRemove','afterLeasingFilingFileListGroup'));
delete from bifrost_org_menu_function where function_id in (select id from bifrost_function where code in ('afterFilingMaterialsBatchDownload','afterFilingMaterialsGetOperationsDirDict','afterLeasingFilingFileDownload',
'afterLeasingFilingFileUpload','afterFilingCheckRemove'));
delete from bifrost_function where code in ('afterFilingMaterialsBatchDownload','afterFilingMaterialsGetOperationsDirDict','afterLeasingFilingFileDownload',
'afterLeasingFilingFileUpload','afterFilingCheckRemove','afterLeasingFilingFileListGroup');


INSERT INTO bifrost_function(code,name,sort_no,menu_id,method,path,type,group_id)
SELECT 'afterFilingMaterialsBatchDownload','租后资料归档-批量下载',0,b.id,'POST','/after/filingMaterial/batchDownload',2,c.id
FROM bifrost_menu b, gruul_function_group c where b.name = '我收到的' and c.group_describe='我收到的-编辑';
INSERT INTO bifrost_function(code,name,sort_no,menu_id,method,path,type,group_id)
SELECT 'afterFilingMaterialsGetOperationsDirDict','租后资料归档-获取归档资料一级目录',0,b.id,'POST','/after/filingMaterial/getOperationsDirDict',1,c.id
FROM bifrost_menu b, gruul_function_group c where b.name = '我收到的' and c.group_describe='我收到的-查看';
INSERT INTO bifrost_function(code,name,sort_no,menu_id,method,path,type,group_id)
SELECT 'afterFilingCheckRemove','租后资料归档-文件删除',0,b.id,'POST','/after/filingMaterial/file/remove',2,c.id
FROM bifrost_menu b, gruul_function_group c where b.name = '我收到的' and c.group_describe='我收到的-编辑';

INSERT INTO bifrost_function(code,name,sort_no,menu_id,method,path,type,group_id)
SELECT 'afterLeasingFilingFileDownload','租后资料归档-单文件下载',0,b.id,'GET','/file/download',2,c.id
FROM bifrost_menu b, gruul_function_group c where b.name = '我收到的' and c.group_describe='我收到的-编辑';
INSERT INTO bifrost_function(code,name,sort_no,menu_id,method,path,type,group_id)
SELECT 'afterLeasingFilingFileUpload','租后资料归档-文件上传',0,b.id,'POST','/file/upload',2,c.id
FROM bifrost_menu b, gruul_function_group c where b.name = '我收到的' and c.group_describe='我收到的-编辑';
INSERT INTO bifrost_function(code,name,sort_no,menu_id,method,path,type,group_id)
SELECT 'afterLeasingFilingFileListGroup','租后资料归档-获取文件分组',0,b.id,'POST','/file/list/group',1,c.id
FROM bifrost_menu b, gruul_function_group c where b.name = '我收到的' and c.group_describe='我收到的-查看';



INSERT INTO bifrost_org_menu_function(org_id, menu_id, function_id, custom_tree_id)
SELECT org.id, menu.id, function.id, tree.id
FROM bifrost_org org,
     bifrost_function function,
     bifrost_menu menu,
     bifrost_custom_tree tree
where org.code in ('ZSZL','YYGLB','FLHGB_ZCBQ')
  and function.code in ('afterFilingMaterialsBatchDownload','afterFilingMaterialsGetOperationsDirDict','afterLeasingFilingFileDownload',
'afterLeasingFilingFileDownload','afterFilingCheckRemove','afterLeasingFilingFileListGroup')
  and menu.name = '我收到的'
  and tree.name = '我的流程';



INSERT INTO bifrost_role_menu_function(role_id, menu_id, function_id, custom_tree_id)
SELECT role.id, menu.id, function.id, tree.id
FROM bifrost_role role,
     bifrost_function function,
     bifrost_menu menu,
     bifrost_custom_tree tree
WHERE role.code IN ('FLHGB-ZCJL','YYGLB-TYCK','YYGLB-JBR','YYGLB_YYJL')
  and function.code in ('afterFilingMaterialsBatchDownload','afterFilingMaterialsGetOperationsDirDict','afterLeasingFilingFileDownload',
'afterLeasingFilingFileUpload','afterFilingCheckRemove','afterLeasingFilingFileListGroup')
  and menu.name = '我收到的'
  and tree.name = '我的流程';

INSERT INTO bifrost_role_menu_function(role_id, menu_id, function_id, custom_tree_id)
SELECT role.id, menu.id, function.id, tree.id
FROM bifrost_role role,
     bifrost_function function,
     bifrost_menu menu,
     bifrost_custom_tree tree
WHERE role.code IN ('ADMIN_SUPERADMIN','CHAKAN','COMPREHENSIVE_MANAGEMENT','readAdmin','GLY')
  and function.code in ('afterFilingMaterialsBatchDownload','afterFilingMaterialsGetOperationsDirDict','afterLeasingFilingFileDownload','afterLeasingFilingFileListGroup')
  and menu.name = '我收到的'
  and tree.name = '我的流程';


-- ============= 来自文件：20250206_linlili_008_dml.sql ============= 


delete from bifrost_custom_tree_menu_ref where menu_id = (select id from bifrost_menu where code ='archivesOtherFilingMaterials');
delete from gruul_function_group where menu_id =(select id from bifrost_menu where code ='archivesOtherFilingMaterials');
delete from bifrost_menu where code ='archivesOtherFilingMaterials';

INSERT INTO bifrost_menu (code, `level`, sort_no, `type`, `path`, parent_id, icon, name) VALUES('archivesOtherFilingMaterials', 3, 0, '0', '/archives/otherFilingMaterials', NULL, NULL, '其他资料归档');
INSERT INTO bifrost_custom_tree_menu_ref (custom_tree_id, menu_id, sort_no) VALUES( ( select id from bifrost_custom_tree bct where code ='archivesmanage'), (select id from bifrost_menu where code ='archivesOtherFilingMaterials'), 3);
INSERT INTO gruul_function_group ( sort_no, code, name, group_describe, menu_id) VALUES( 1, 'otherFilingMaterials-Read', '其他资料归档', '其他资料归档-查看', (select id from bifrost_menu where code ='archivesOtherFilingMaterials'));
INSERT INTO gruul_function_group ( sort_no, code, name, group_describe, menu_id) VALUES( 2, 'otherFilingMaterials-Edit', '其他资料归档', '其他资料归档-编辑', (select id from bifrost_menu where code ='archivesOtherFilingMaterials'));
    
delete from filing_first_level_config where filing_materials_config_id in (select id from filing_materials_config where business_type = 'OTHER_FILING' and filing_type  ='OTHER');
delete from filing_materials_config where business_type = 'OTHER_FILING' and filing_type  ='OTHER';

INSERT INTO filing_materials_config (filing_type, business_type, enable_flag, remark) VALUES('OTHER', 'OTHER_FILING', 1, '其他资料归档');

INSERT INTO filing_first_level_config (filing_materials_config_id, dir_code, dir_name, fixed_flag, condition_key, sort_code, enable_flag,require_flag) VALUES((select id from filing_materials_config fmc where filing_type ='OTHER' and business_type ='OTHER_FILING'), 'BASIC_INFORMATION', '其他资料清单', 1, NULL, 1, 1,0);
INSERT INTO filing_first_level_config (filing_materials_config_id, dir_code, dir_name, fixed_flag, condition_key, sort_code, enable_flag,require_flag) VALUES((select id from filing_materials_config fmc where filing_type ='OTHER' and business_type ='OTHER_FILING'), 'OTHER_MATERIALS', '其他文件', 1, NULL, 99, 1,0);

delete from bifrost_role_menu_function where function_id in (select id from bifrost_function
where code in ('otherFilingMaterialsBatchDownload','otherFilingMaterialsGetOperationsDirDict','otherFilingCheckRemove',
'otherFilingSelectConfirm','otherFilingCancel','otherFilingCommit','otherFilingFileDownload','otherFilingFileUpload',
'otherFilingFileListGroup','otherFilingFileRemove','otherFilingGetReviewProject','otherFilingMaterialsList','otherFilingGetMaterialsDesc',
'otherFilingSaveMaterialsDesc','otherFilingMaterialsExport','otherFilingCheckMaterialsDesc'));
delete from bifrost_org_menu_function where function_id in (select id from bifrost_function where code in ('otherFilingMaterialsBatchDownload','otherFilingMaterialsGetOperationsDirDict','otherFilingCheckRemove',
'otherFilingSelectConfirm','otherFilingCancel','otherFilingCommit','otherFilingFileDownload','otherFilingFileUpload',
'otherFilingFileListGroup','otherFilingFileRemove','otherFilingGetReviewProject','otherFilingMaterialsList','otherFilingGetMaterialsDesc',
'otherFilingSaveMaterialsDesc','otherFilingMaterialsExport','otherFilingCheckMaterialsDesc'));
delete from bifrost_function where code in ('otherFilingMaterialsBatchDownload','otherFilingMaterialsGetOperationsDirDict','otherFilingCheckRemove',
'otherFilingSelectConfirm','otherFilingCancel','otherFilingCommit','otherFilingFileDownload','otherFilingFileUpload',
'otherFilingFileListGroup','otherFilingFileRemove','otherFilingGetReviewProject','otherFilingMaterialsList','otherFilingGetMaterialsDesc',
'otherFilingSaveMaterialsDesc','otherFilingMaterialsExport','otherFilingCheckMaterialsDesc');


INSERT INTO bifrost_function(code,name,sort_no,menu_id,method,path,type,group_id)
SELECT 'otherFilingMaterialsBatchDownload','其他资料归档-批量下载',0,b.id,'POST','/other/filingMaterial/batchDownload',2,c.id
FROM bifrost_menu b, gruul_function_group c where b.name = '其他资料归档' and c.group_describe='其他资料归档-编辑';
INSERT INTO bifrost_function(code,name,sort_no,menu_id,method,path,type,group_id)
SELECT 'otherFilingMaterialsGetOperationsDirDict','其他资料归档-获取归档资料一级目录',0,b.id,'POST','/other/filingMaterial/getOperationsDirDict',1,c.id
FROM bifrost_menu b, gruul_function_group c where b.name = '其他资料归档' and c.group_describe='其他资料归档-查看';
INSERT INTO bifrost_function(code,name,sort_no,menu_id,method,path,type,group_id)
SELECT 'otherFilingCheckRemove','其他资料归档-文件删除',0,b.id,'POST','/other/filingMaterial/file/remove',2,c.id
FROM bifrost_menu b, gruul_function_group c where b.name = '其他资料归档' and c.group_describe='其他资料归档-编辑';
INSERT INTO bifrost_function(code,name,sort_no,menu_id,method,path,type,group_id)
SELECT 'otherFilingSelectConfirm','其他资料归档-项目确认',0,b.id,'GET','/other/filingMaterial/select/confirm',2,c.id
FROM bifrost_menu b, gruul_function_group c where b.name = '其他资料归档' and c.group_describe='其他资料归档-编辑';
INSERT INTO bifrost_function(code,name,sort_no,menu_id,method,path,type,group_id)
SELECT 'otherFilingCancel','其他资料归档-取消操作',0,b.id,'GET','/other/filingMaterial/cancel',2,c.id
FROM bifrost_menu b, gruul_function_group c where b.name = '其他资料归档' and c.group_describe='其他资料归档-编辑';
INSERT INTO bifrost_function(code,name,sort_no,menu_id,method,path,type,group_id)
SELECT 'otherFilingCommit','其他资料归档-提交审批',0,b.id,'POST','/other/filingMaterial/commit',2,c.id
FROM bifrost_menu b, gruul_function_group c where b.name = '其他资料归档' and c.group_describe='其他资料归档-编辑';
INSERT INTO bifrost_function(code,name,sort_no,menu_id,method,path,type,group_id)
SELECT 'otherFilingGetReviewProject','其他资料归档-获取项目评审项目',0,b.id,'POST','/other/filingMaterial/getReviewProject',2,c.id
FROM bifrost_menu b, gruul_function_group c where b.name = '其他资料归档' and c.group_describe='其他资料归档-编辑';

INSERT INTO bifrost_function(code,name,sort_no,menu_id,method,path,type,group_id)
SELECT 'otherFilingFileDownload','其他资料归档-单文件下载',0,b.id,'GET','/file/download',2,c.id
FROM bifrost_menu b, gruul_function_group c where b.name = '其他资料归档' and c.group_describe='其他资料归档-编辑';
INSERT INTO bifrost_function(code,name,sort_no,menu_id,method,path,type,group_id)
SELECT 'otherFilingFileUpload','其他资料归档-文件上传',0,b.id,'POST','/file/upload',2,c.id
FROM bifrost_menu b, gruul_function_group c where b.name = '其他资料归档' and c.group_describe='其他资料归档-编辑';
INSERT INTO bifrost_function(code,name,sort_no,menu_id,method,path,type,group_id)
SELECT 'otherFilingFileListGroup','其他资料归档-获取文件分组',0,b.id,'POST','/file/list/group',1,c.id
FROM bifrost_menu b, gruul_function_group c where b.name = '其他资料归档' and c.group_describe='其他资料归档-查看';
INSERT INTO bifrost_function(code,name,sort_no,menu_id,method,path,type,group_id)
SELECT 'otherFilingFileRemove','其他资料归档-非基础资料文件删除',0,b.id,'POST','/file/remove',1,c.id
FROM bifrost_menu b, gruul_function_group c where b.name = '其他资料归档' and c.group_describe='其他资料归档-查看';
INSERT INTO bifrost_function(code,name,sort_no,menu_id,method,path,type,group_id)
SELECT 'otherFilingGetMaterialsDesc','其他资料归档-获取资料类型',0,b.id,'POST','/other/filingMaterial/getMaterialsDesc',2,c.id
FROM bifrost_menu b, gruul_function_group c where b.name = '其他资料归档' and c.group_describe='其他资料归档-查看';
INSERT INTO bifrost_function(code,name,sort_no,menu_id,method,path,type,group_id)
SELECT 'otherFilingSaveMaterialsDesc','其他资料归档-获取资料类型',0,b.id,'POST','/other/filingMaterial/saveMaterialsDesc',1,c.id
FROM bifrost_menu b, gruul_function_group c where b.name = '其他资料归档' and c.group_describe='其他资料归档-编辑';
INSERT INTO bifrost_function(code,name,sort_no,menu_id,method,path,type,group_id)
SELECT 'otherFilingCheckMaterialsDesc','其他资料归档-校验资料类型',0,b.id,'POST','/other/filingMaterial/checkMaterialsDesc',1,c.id
FROM bifrost_menu b, gruul_function_group c where b.name = '其他资料归档' and c.group_describe='其他资料归档-查看';

INSERT INTO bifrost_function(code,name,sort_no,menu_id,method,path,type,group_id)
SELECT 'otherFilingMaterialsList','其他资料归档台账-列表展示',0,b.id,'POST','/other/filingMaterial/list',2,c.id
FROM bifrost_menu b, gruul_function_group c where b.name = '其他资料归档' and c.group_describe='其他资料归档-查看';

INSERT INTO bifrost_function(code,name,sort_no,menu_id,method,path,type,group_id)
SELECT 'otherFilingMaterialsExport','其他资料归档台账-批量导出',0,b.id,'POST','/other/filingMaterial/export',2,c.id
FROM bifrost_menu b, gruul_function_group c where b.name = '其他资料归档' and c.group_describe='其他资料归档-查看';

INSERT INTO bifrost_org_menu_function(org_id, menu_id, function_id, custom_tree_id)
SELECT org.id, menu.id, function.id, tree.id
FROM bifrost_org org,
     bifrost_function function,
     bifrost_menu menu,
     bifrost_custom_tree tree
where org.code in ('ZSZL','YYGLB','FLHGB_ZCBQ','YYGLB','JSSYB','HGJCYWB','JXHJGYWB','JCSSYWB','LLWLTD','XJZZHXJJTD','JTYSYWB','GGSY','HYYWB','XNYYWB','SYCYWB','LSCYYWB','XMPSWYH')
  and function.code in ('otherFilingMaterialsBatchDownload','otherFilingMaterialsGetOperationsDirDict','otherFilingCheckRemove',
'otherFilingSelectConfirm','otherFilingCancel','otherFilingCommit','otherFilingFileDownload','otherFilingFileUpload','otherFilingFileListGroup',
'otherFilingFileRemove','otherFilingGetReviewProject','otherFilingMaterialsList','otherFilingGetMaterialsDesc',
'otherFilingSaveMaterialsDesc','otherFilingMaterialsExport','otherFilingCheckMaterialsDesc')
  and menu.name = '其他资料归档'
  and tree.name = '档案管理';



INSERT INTO bifrost_role_menu_function(role_id, menu_id, function_id, custom_tree_id)
SELECT role.id, menu.id, function.id, tree.id
FROM bifrost_role role,
     bifrost_function function,
     bifrost_menu menu,
     bifrost_custom_tree tree
WHERE role.code IN ('XMJL','FLHBG-FWJL','YYGLB_YYJL','FLHGB-ZCJL','PSHMS')
  and function.code in ('otherFilingMaterialsBatchDownload','otherFilingMaterialsGetOperationsDirDict','otherFilingCheckRemove',
'otherFilingSelectConfirm','otherFilingCancel','otherFilingCommit','otherFilingFileDownload','otherFilingFileUpload','otherFilingFileListGroup',
'otherFilingFileRemove','otherFilingGetReviewProject','otherFilingMaterialsList','otherFilingGetMaterialsDesc',
'otherFilingMaterialsExport','otherFilingCheckMaterialsDesc')
  and menu.name = '其他资料归档'
  and tree.name = '档案管理';

INSERT INTO bifrost_role_menu_function(role_id, menu_id, function_id, custom_tree_id)
SELECT role.id, menu.id, function.id, tree.id
FROM bifrost_role role,
     bifrost_function function,
     bifrost_menu menu,
     bifrost_custom_tree tree
WHERE role.code IN ('FLHGB-FZR','YYGLB-TYCK','YYGLB-JBR')
  and function.code in ('otherFilingMaterialsBatchDownload','otherFilingMaterialsGetOperationsDirDict','otherFilingCheckRemove',
  'otherFilingCancel','otherFilingFileDownload','otherFilingFileUpload','otherFilingFileListGroup',
'otherFilingFileRemove','otherFilingGetReviewProject','otherFilingMaterialsList','otherFilingGetMaterialsDesc',
'otherFilingSaveMaterialsDesc','otherFilingMaterialsExport','otherFilingCheckMaterialsDesc')
  and menu.name = '其他资料归档'
  and tree.name = '档案管理';

INSERT INTO bifrost_role_menu_function(role_id, menu_id, function_id, custom_tree_id)
SELECT role.id, menu.id, function.id, tree.id
FROM bifrost_role role,
     bifrost_function function,
     bifrost_menu menu,
     bifrost_custom_tree tree
WHERE role.code IN ('ADMIN_SUPERADMIN','CHAKAN','COMPREHENSIVE_MANAGEMENT','readAdmin','GLY')
  and function.code in ('otherFilingGetReviewProject','otherFilingMaterialsBatchDownload','otherFilingMaterialsGetOperationsDirDict','otherFilingFileDownload','otherFilingFileListGroup','otherFilingMaterialsList','otherFilingGetMaterialsDesc','otherFilingMaterialsExport','otherFilingCheckMaterialsDesc')
  and menu.name = '其他资料归档'
  and tree.name = '档案管理';


-- ============= 来自文件：20250206_liushaokang_001_dml.sql ============= 

-- 资产五级分类新增功能接口
delete from bifrost_org_menu_function where function_id = (select id from bifrost_function where code ='assetclassifyMidQuarterClientList');
delete from bifrost_role_menu_function where function_id = (select id from bifrost_function where code ='assetclassifyMidQuarterClientList');
delete from bifrost_function where code = 'assetclassifyMidQuarterClientList';
delete from bifrost_org_menu_function where function_id = (select id from bifrost_function where code ='assetclassifyMidQuarterDivision');
delete from bifrost_role_menu_function where function_id = (select id from bifrost_function where code ='assetclassifyMidQuarterDivision');
delete from bifrost_function where code = 'assetclassifyMidQuarterDivision';

set @menu_id = (select id from bifrost_menu where code = 'riskLevel5Type');
INSERT INTO bifrost_function ( code, name, sort_no, menu_id, method, path, type)
VALUES ('assetclassifyMidQuarterClientList', '五级分类季中客户列表', 0, @menu_id , 'POST', '/assetclassify/client/clientList', 1),
       ('assetclassifyMidQuarterDivision', '五级分类季中初分', 0, @menu_id , 'POST', '/assetclassify/manual/initial/midQuarterDivision', 2);
set @org_id = (select id from bifrost_org where code ='FXGLB_YWPS');
set @custom_tree_id = (select id from bifrost_custom_tree_menu_ref where menu_id =@menu_id);
-- 部门添加接口
insert into bifrost_org_menu_function (org_id, menu_id, function_id, create_by, update_by, custom_tree_id)
values  (@org_id, @menu_id , (select id from bifrost_function where code ='assetclassifyMidQuarterClientList'), null, null, @custom_tree_id),
        (@org_id, @menu_id , (select id from bifrost_function where code ='assetclassifyMidQuarterDivision'), null, null, @custom_tree_id);
set @role_id = (select id from bifrost_role where code ='SDCF');
-- 角色添加接口
insert into bifrost_role_menu_function (role_id, menu_id, function_id, create_by, update_by, custom_tree_id)
values  ( @role_id,  @menu_id, (select id from bifrost_function where code ='assetclassifyMidQuarterClientList'), null, null, @custom_tree_id),
        ( @role_id,  @menu_id, (select id from bifrost_function where code ='assetclassifyMidQuarterDivision'), null, null, @custom_tree_id);


-- ============= 来自文件：20260206_gxy_003_dml.sql ============= 

-- 新增档案管理台账菜单
set @menuCode = 'archivesmanagement';
set @treeName = '档案管理';
set @menuId = (select id from bifrost_menu where code = @menuCode);

-- 可重复执行
delete from bifrost_custom_tree_menu_ref where menu_id = @menuId;
delete from bifrost_org_menu_function  where menu_id = @menuId;
delete from bifrost_menu where code = @menuCode;

-- 插入菜单
INSERT INTO bifrost_menu (code, level, sort_no, type, path, parent_id, icon, name, en_name, target, create_by, update_by)
VALUES (@menuCode, 3, 0, null, '/archives/management', null, null, '档案管理', null, '_self', null, null);

set @menuId = (select id from bifrost_menu where code = @menuCode);

-- 插入菜单关联
set @treeId = (select id from bifrost_custom_tree where name = @treeName);
INSERT INTO bifrost_custom_tree_menu_ref (custom_tree_id, menu_id, sort_no) VALUES (@treeId, @menuId, 0);

-- 插入接口分组（按操作分查看/编辑）
set @groupCode1 = 'archivesmanagement-Read';
set @groupCode2 = 'archivesmanagement-Write';
delete from gruul_function_group where code = @groupCode1;
delete from gruul_function_group where code = @groupCode2;
INSERT INTO gruul_function_group (sort_no, code, name, group_describe, menu_id) VALUES ( 0, @groupCode1, '档案管理-查看', '档案管理-查看', @menuId);
set @groupId1 = (select id from gruul_function_group where code = @groupCode1);
INSERT INTO gruul_function_group (sort_no, code, name, group_describe, menu_id) VALUES ( 0, @groupCode2, '档案管理-编辑', '档案管理-编辑', @menuId);
set @groupId2 = (select id from gruul_function_group where code = @groupCode2);

-- 插入功能接口
set @fun1 = 'fundSideArchivedMaterialsQuery';
delete from bifrost_function where code = @fun1;
insert into bifrost_function (code, name, sort_no, menu_id, method, path, type,group_id)
values  (@fun1, '资金端归档资料-列表查询', 0, @menuId, 'POST','/documentManagementLedger/fund/list/query', 2,@groupId1);
set @funId1 = (select id from bifrost_function where code =@fun1);

set @fun2 = 'fundSideArchivedMaterialsBatchDownload';
delete from bifrost_function where code = @fun2;
insert into bifrost_function (code, name, sort_no, menu_id, method, path, type,group_id)
values  (@fun2, '资金端归档资料-批量下载', 0, @menuId, 'POST','/documentManagementLedger/fund/batch/download', 1,@groupId2);
set @funId2 = (select id from bifrost_function where code =@fun2);

set @fun3 = 'fundSideArchivedMaterialsDownloadRecordsQuery';
delete from bifrost_function where code = @fun3;
insert into bifrost_function (code, name, sort_no, menu_id, method, path, type,group_id)
values  (@fun3, '资金端归档资料-下载记录查询', 0, @menuId, 'POST','/documentManagementLedger/fund/download/records/query', 2,@groupId1);
set @funId3 = (select id from bifrost_function where code =@fun3);

set @fun4 = 'projSideArchivedDocumentQuery';
delete from bifrost_function where code = @fun4;
insert into bifrost_function (code, name, sort_no, menu_id, method, path, type,group_id)
values  (@fun4, '项目端归档资料-列表查询', 0, @menuId, 'POST','/documentManagementLedger/proj/list/query', 2,@groupId1);
set @funId4 = (select id from bifrost_function where code =@fun4);

set @fun5 = 'materialsdger-proj-clientList';
delete from bifrost_function where code = @fun5;
insert into bifrost_function (code, name, sort_no, menu_id, method, path, type,group_id)
values  (@fun5, '归档资料台账-项目端-客户查询', 0, @menuId, 'POST','/client/list', 2,@groupId1);
set @funId5 = (select id from bifrost_function where code =@fun5);

set @orgId1 = (select id from bifrost_org where code = 'ZSZL');
set @orgId2 = (select id from bifrost_org where code = 'LDC');
set @orgId3 = (select id from bifrost_org where code = 'ZJGLB');
set @orgId4 = (select id from bifrost_org where code = 'YYGLB');
-- 给公司和部门添加权限
delete from bifrost_org_menu_function where menu_id = @menuId;
insert into bifrost_org_menu_function (org_id, menu_id, function_id,  custom_tree_id)
values  ( @orgId1, @menuId, null,  @treeId),
        ( @orgId1, @menuId, @funId1, @treeId),
        ( @orgId1, @menuId, @funId2, @treeId),
        ( @orgId1, @menuId, @funId3, @treeId),
        ( @orgId1, @menuId, @funId4, @treeId),
        -- 领导层
        ( @orgId2, @menuId, null,  @treeId),
        ( @orgId2, @menuId, @funId1, @treeId),
        ( @orgId2, @menuId, @funId2, @treeId),
        ( @orgId2, @menuId, @funId3, @treeId),
        ( @orgId2, @menuId, @funId4, @treeId),
        -- 资金管理部
        ( @orgId3, @menuId, null,  @treeId),
        ( @orgId3, @menuId, @funId1, @treeId),
        ( @orgId3, @menuId, @funId2, @treeId),
        ( @orgId3, @menuId, @funId3, @treeId),
        -- 运营管理部
        ( @orgId4, @menuId, null,  @treeId),
        ( @orgId4, @menuId, @funId4, @treeId),
        ( @orgId4, @menuId, @funId5, @treeId);


-- 插入角色菜单功能
delete from bifrost_role_menu_function where menu_id = @menuId;
insert into bifrost_role_menu_function (role_id, menu_id, function_id, custom_tree_id)
values
    ((select id from bifrost_role where code ='ADMIN_SUPERADMIN'),@menuId, @funId1,  @treeId), -- 超级管理员
    ((select id from bifrost_role where code ='ADMIN_SUPERADMIN'),@menuId, @funId4,  @treeId),
    ((select id from bifrost_role where code ='ADMIN_SUPERADMIN'),@menuId, @funId5,  @treeId),
    ((select id from bifrost_role where code ='XMJL'),@menuId, null,  @treeId), -- 项目经理
    ((select id from bifrost_role where code ='FLHGB-ZCJL'),@menuId, null,  @treeId), -- 资产经理
    ((select id from bifrost_role where code ='FLHBG-FWJL'),@menuId, null,  @treeId), -- 法律合规部-法务经理
    ((select id from bifrost_role where code ='YYGLB-TYCK'),@menuId, null,  @treeId), -- 运营管理部-通用查看
    ((select id from bifrost_role where code ='YYGLB-TYCK'),@menuId, @funId4,  @treeId),
    ((select id from bifrost_role where code ='YYGLB-TYCK'),@menuId, @funId5,  @treeId),
    ((select id from bifrost_role where code ='ZJGLB-ZJGL'),@menuId, @funId1,  @treeId), -- 资金管理部
    ((select id from bifrost_role where code ='ZJGLB-ZJGL'),@menuId, @funId2,  @treeId),
    ((select id from bifrost_role where code ='ZJGLB-ZJGL'),@menuId, @funId3,  @treeId);

-- 隐藏现有档案管理、归档任务模板管理二级菜单
delete from bifrost_role_menu_function where menu_id in (select id from bifrost_menu where code in ('archivestask','archivesmanage'));

-- 给公司和部门添加权限
delete from bifrost_org_menu_function where menu_id = @menuId;
insert into bifrost_org_menu_function (org_id, menu_id, function_id,  custom_tree_id)
values  -- 资金管理部
        ( @orgId3, @menuId, @funId1, @treeId),
        ( @orgId3, @menuId, @funId2, @treeId),
        ( @orgId3, @menuId, @funId3, @treeId),
        -- 运营管理部
        ( @orgId4, @menuId, @funId4, @treeId),
        ( @orgId4, @menuId, @funId5, @treeId);

-- 插入角色菜单功能
delete from bifrost_role_menu_function where menu_id = @menuId;
insert into bifrost_role_menu_function (role_id, menu_id, function_id, custom_tree_id)
values
    ((select id from bifrost_role where code ='ADMIN_SUPERADMIN'),@menuId, @funId1,  @treeId), -- 超级管理员
    ((select id from bifrost_role where code ='ADMIN_SUPERADMIN'),@menuId, @funId2,  @treeId),
    ((select id from bifrost_role where code ='ADMIN_SUPERADMIN'),@menuId, @funId3,  @treeId),
    ((select id from bifrost_role where code ='ADMIN_SUPERADMIN'),@menuId, @funId4,  @treeId),
    ((select id from bifrost_role where code ='ADMIN_SUPERADMIN'),@menuId, @funId5,  @treeId),
    ((select id from bifrost_role where code ='YYGLB-TYCK'),@menuId, @funId4,  @treeId), -- 运营管理部-通用查看
    ((select id from bifrost_role where code ='YYGLB-TYCK'),@menuId, @funId5,  @treeId),
    ((select id from bifrost_role where code ='ZJGLB-ZJGL'),@menuId, @funId1,  @treeId), -- 资金管理部
    ((select id from bifrost_role where code ='ZJGLB-ZJGL'),@menuId, @funId2,  @treeId),
    ((select id from bifrost_role where code ='ZJGLB-ZJGL'),@menuId, @funId3,  @treeId);


-- ============= 来自文件：20250206_linlili_009_dml.sql ============= 


delete from bifrost_role_menu_function where function_id in (select id from bifrost_function
where code in ('otherFilingMaterialsBatchDownload','otherFilingMaterialsGetOperationsDirDict','otherFilingCheckRemove',
'otherFilingSelectConfirm','otherFilingCancel','otherFilingCommit','otherFilingFileDownload','otherFilingFileUpload',
'otherFilingFileListGroup','otherFilingFileRemove','otherFilingGetReviewProject','otherFilingMaterialsList','otherFilingGetMaterialsDesc',
'otherFilingSaveMaterialsDesc','otherFilingMaterialsExport','otherFilingCheckMaterialsDesc'));
delete from bifrost_org_menu_function where function_id in (select id from bifrost_function where code in ('otherFilingMaterialsBatchDownload','otherFilingMaterialsGetOperationsDirDict','otherFilingCheckRemove',
'otherFilingSelectConfirm','otherFilingCancel','otherFilingCommit','otherFilingFileDownload','otherFilingFileUpload',
'otherFilingFileListGroup','otherFilingFileRemove','otherFilingGetReviewProject','otherFilingMaterialsList','otherFilingGetMaterialsDesc',
'otherFilingSaveMaterialsDesc','otherFilingMaterialsExport','otherFilingCheckMaterialsDesc'));
delete from bifrost_function where code in ('otherFilingMaterialsBatchDownload','otherFilingMaterialsGetOperationsDirDict','otherFilingCheckRemove',
'otherFilingSelectConfirm','otherFilingCancel','otherFilingCommit','otherFilingFileDownload','otherFilingFileUpload',
'otherFilingFileListGroup','otherFilingFileRemove','otherFilingGetReviewProject','otherFilingMaterialsList','otherFilingGetMaterialsDesc',
'otherFilingSaveMaterialsDesc','otherFilingMaterialsExport','otherFilingCheckMaterialsDesc');


INSERT INTO bifrost_function(code,name,sort_no,menu_id,method,path,type,group_id)
SELECT 'otherFilingMaterialsBatchDownload','其他资料归档-批量下载',0,b.id,'POST','/other/filingMaterial/batchDownload',2,c.id
FROM bifrost_menu b, gruul_function_group c where b.name = '我收到的' and c.group_describe='我收到的-编辑';
INSERT INTO bifrost_function(code,name,sort_no,menu_id,method,path,type,group_id)
SELECT 'otherFilingMaterialsGetOperationsDirDict','其他资料归档-获取归档资料一级目录',0,b.id,'POST','/other/filingMaterial/getOperationsDirDict',1,c.id
FROM bifrost_menu b, gruul_function_group c where b.name = '我收到的' and c.group_describe='我收到的-查看';
INSERT INTO bifrost_function(code,name,sort_no,menu_id,method,path,type,group_id)
SELECT 'otherFilingCheckRemove','其他资料归档-文件删除',0,b.id,'POST','/other/filingMaterial/file/remove',2,c.id
FROM bifrost_menu b, gruul_function_group c where b.name = '我收到的' and c.group_describe='我收到的-编辑';
INSERT INTO bifrost_function(code,name,sort_no,menu_id,method,path,type,group_id)
SELECT 'otherFilingCancel','其他资料归档-取消操作',0,b.id,'GET','/other/filingMaterial/cancel',2,c.id
FROM bifrost_menu b, gruul_function_group c where b.name = '我收到的' and c.group_describe='我收到的-编辑';
INSERT INTO bifrost_function(code,name,sort_no,menu_id,method,path,type,group_id)
SELECT 'otherFilingCommit','其他资料归档-提交审批',0,b.id,'POST','/other/filingMaterial/commit',2,c.id
FROM bifrost_menu b, gruul_function_group c where b.name = '我收到的' and c.group_describe='我收到的-编辑';

INSERT INTO bifrost_function(code,name,sort_no,menu_id,method,path,type,group_id)
SELECT 'otherFilingFileDownload','其他资料归档-单文件下载',0,b.id,'GET','/file/download',2,c.id
FROM bifrost_menu b, gruul_function_group c where b.name = '我收到的' and c.group_describe='我收到的-编辑';
INSERT INTO bifrost_function(code,name,sort_no,menu_id,method,path,type,group_id)
SELECT 'otherFilingFileUpload','其他资料归档-文件上传',0,b.id,'POST','/file/upload',2,c.id
FROM bifrost_menu b, gruul_function_group c where b.name = '我收到的' and c.group_describe='我收到的-编辑';
INSERT INTO bifrost_function(code,name,sort_no,menu_id,method,path,type,group_id)
SELECT 'otherFilingFileListGroup','其他资料归档-获取文件分组',0,b.id,'POST','/file/list/group',1,c.id
FROM bifrost_menu b, gruul_function_group c where b.name = '我收到的' and c.group_describe='我收到的-查看';
INSERT INTO bifrost_function(code,name,sort_no,menu_id,method,path,type,group_id)
SELECT 'otherFilingFileRemove','其他资料归档-非基础资料文件删除',0,b.id,'POST','/file/remove',1,c.id
FROM bifrost_menu b, gruul_function_group c where b.name = '我收到的' and c.group_describe='我收到的-查看';
INSERT INTO bifrost_function(code,name,sort_no,menu_id,method,path,type,group_id)
SELECT 'otherFilingGetMaterialsDesc','其他资料归档-获取资料类型',0,b.id,'POST','/other/filingMaterial/getMaterialsDesc',2,c.id
FROM bifrost_menu b, gruul_function_group c where b.name = '我收到的' and c.group_describe='我收到的-查看';
INSERT INTO bifrost_function(code,name,sort_no,menu_id,method,path,type,group_id)
SELECT 'otherFilingSaveMaterialsDesc','其他资料归档-获取资料类型',0,b.id,'POST','/other/filingMaterial/saveMaterialsDesc',1,c.id
FROM bifrost_menu b, gruul_function_group c where b.name = '我收到的' and c.group_describe='我收到的-编辑';
INSERT INTO bifrost_function(code,name,sort_no,menu_id,method,path,type,group_id)
SELECT 'otherFilingCheckMaterialsDesc','其他资料归档-校验资料类型',0,b.id,'POST','/other/filingMaterial/checkMaterialsDesc',1,c.id
FROM bifrost_menu b, gruul_function_group c where b.name = '我收到的' and c.group_describe='我收到的-查看';

INSERT INTO bifrost_function(code,name,sort_no,menu_id,method,path,type,group_id)
SELECT 'otherFilingMaterialsList','其他资料归档台账-列表展示',0,b.id,'POST','/other/filingMaterial/list',2,c.id
FROM bifrost_menu b, gruul_function_group c where b.name = '其他资料归档' and c.group_describe='其他资料归档-查看';

INSERT INTO bifrost_function(code,name,sort_no,menu_id,method,path,type,group_id)
SELECT 'otherFilingMaterialsExport','其他资料归档台账-批量导出',0,b.id,'POST','/other/filingMaterial/export',2,c.id
FROM bifrost_menu b, gruul_function_group c where b.name = '其他资料归档' and c.group_describe='其他资料归档-查看';

INSERT INTO bifrost_function(code,name,sort_no,menu_id,method,path,type,group_id)
SELECT 'otherFilingGetReviewProject','其他资料归档-获取项目评审项目',0,b.id,'POST','/other/filingMaterial/getReviewProject',2,c.id
FROM bifrost_menu b, gruul_function_group c where b.name = '其他资料归档' and c.group_describe='其他资料归档-编辑';

INSERT INTO bifrost_function(code,name,sort_no,menu_id,method,path,type,group_id)
SELECT 'otherFilingSelectConfirm','其他资料归档-项目确认',0,b.id,'GET','/other/filingMaterial/select/confirm',2,c.id
FROM bifrost_menu b, gruul_function_group c where b.name = '其他资料归档' and c.group_describe='其他资料归档-编辑';

INSERT INTO bifrost_org_menu_function(org_id, menu_id, function_id, custom_tree_id)
SELECT org.id, menu.id, function.id, tree.id
FROM bifrost_org org,
     bifrost_function function,
     bifrost_menu menu,
     bifrost_custom_tree tree
where org.code in ('ZSZL','YYGLB','FLHGB_ZCBQ','YYGLB','JSSYB','HGJCYWB','JXHJGYWB','JCSSYWB','LLWLTD','XJZZHXJJTD','JTYSYWB','GGSY','HYYWB','XNYYWB','SYCYWB','LSCYYWB','XMPSWYH')
  and function.code in ('otherFilingSelectConfirm','otherFilingGetReviewProject','otherFilingMaterialsList','otherFilingMaterialsExport')
  and menu.name = '其他资料归档'
  and tree.name = '档案管理';

INSERT INTO bifrost_org_menu_function(org_id, menu_id, function_id, custom_tree_id)
SELECT org.id, menu.id, function.id, tree.id
FROM bifrost_org org,
     bifrost_function function,
     bifrost_menu menu,
     bifrost_custom_tree tree
where org.code in ('ZSZL','YYGLB','FLHGB_ZCBQ','YYGLB','JSSYB','HGJCYWB','JXHJGYWB','JCSSYWB','LLWLTD','XJZZHXJJTD','JTYSYWB','GGSY','HYYWB','XNYYWB','SYCYWB','LSCYYWB','XMPSWYH')
  and function.code in ('otherFilingMaterialsBatchDownload','otherFilingMaterialsGetOperationsDirDict','otherFilingCheckRemove',
'otherFilingCancel','otherFilingCommit','otherFilingFileDownload','otherFilingFileUpload','otherFilingFileListGroup',
'otherFilingFileRemove','otherFilingGetMaterialsDesc',
'otherFilingSaveMaterialsDesc','otherFilingCheckMaterialsDesc')
  and menu.name = '我收到的'
  and tree.name = '我的流程';



INSERT INTO bifrost_role_menu_function(role_id, menu_id, function_id, custom_tree_id)
SELECT role.id, menu.id, function.id, tree.id
FROM bifrost_role role,
     bifrost_function function,
     bifrost_menu menu,
     bifrost_custom_tree tree
WHERE role.code IN ('XMJL','FLHBG-FWJL','YYGLB_YYJL','FLHGB-ZCJL','PSHMS')
  and function.code in ('otherFilingSelectConfirm','otherFilingGetReviewProject','otherFilingMaterialsList','otherFilingMaterialsExport')
  and menu.name = '其他资料归档'
  and tree.name = '档案管理';

INSERT INTO bifrost_role_menu_function(role_id, menu_id, function_id, custom_tree_id)
SELECT role.id, menu.id, function.id, tree.id
FROM bifrost_role role,
     bifrost_function function,
     bifrost_menu menu,
     bifrost_custom_tree tree
WHERE role.code IN ('XMJL','FLHBG-FWJL','YYGLB_YYJL','FLHGB-ZCJL','PSHMS')
  and function.code in ('otherFilingMaterialsBatchDownload','otherFilingMaterialsGetOperationsDirDict','otherFilingCheckRemove',
'otherFilingCancel','otherFilingCommit','otherFilingFileDownload','otherFilingFileUpload','otherFilingFileListGroup',
'otherFilingFileRemove','otherFilingGetMaterialsDesc','otherFilingCheckMaterialsDesc')
  and menu.name = '我收到的'
  and tree.name = '我的流程';

INSERT INTO bifrost_role_menu_function(role_id, menu_id, function_id, custom_tree_id)
SELECT role.id, menu.id, function.id, tree.id
FROM bifrost_role role,
     bifrost_function function,
     bifrost_menu menu,
     bifrost_custom_tree tree
WHERE role.code IN ('YYGLB-TYCK','YYGLB-JBR','GDZB_HBD','HGJC_HBD','ZJ_HBD','ZNZZ_HBD',
'JTWL_HBD','GYSY_HBD','HY_HBD','WHJK_HBD','GCJS_HBD','LSCY_HBD')
  and function.code in ('otherFilingGetReviewProject','otherFilingMaterialsList','otherFilingMaterialsExport')
  and menu.name = '其他资料归档'
  and tree.name = '档案管理';


INSERT INTO bifrost_role_menu_function(role_id, menu_id, function_id, custom_tree_id)
SELECT role.id, menu.id, function.id, tree.id
FROM bifrost_role role,
     bifrost_function function,
     bifrost_menu menu,
     bifrost_custom_tree tree
WHERE role.code IN ('FLHGB-FZR','YYGLB-TYCK','YYGLB-JBR')
  and function.code in ('otherFilingMaterialsBatchDownload','otherFilingMaterialsGetOperationsDirDict','otherFilingCheckRemove',
  'otherFilingFileDownload','otherFilingFileUpload','otherFilingFileListGroup',
'otherFilingFileRemove','otherFilingGetMaterialsDesc',
'otherFilingSaveMaterialsDesc','otherFilingCheckMaterialsDesc')
  and menu.name = '我收到的'
  and tree.name = '我的流程';



INSERT INTO bifrost_role_menu_function(role_id, menu_id, function_id, custom_tree_id)
SELECT role.id, menu.id, function.id, tree.id
FROM bifrost_role role,
     bifrost_function function,
     bifrost_menu menu,
     bifrost_custom_tree tree
WHERE role.code IN ('ADMIN_SUPERADMIN','readAdmin','GLY')
  and function.code in ('otherFilingGetReviewProject','otherFilingMaterialsBatchDownload','otherFilingMaterialsGetOperationsDirDict','otherFilingFileDownload','otherFilingFileListGroup','otherFilingMaterialsList','otherFilingGetMaterialsDesc','otherFilingMaterialsExport','otherFilingCheckMaterialsDesc')
  and menu.name = '其他资料归档'
  and tree.name = '档案管理';


delete from bifrost_role_menu_function where
role_id in (select id from bifrost_role where code IN ('CHAKAN','COMPREHENSIVE_MANAGEMENT'))
and function_id in (select id from bifrost_function where code in ('fundFilingMaterialsBatchDownload','fundFilingMaterialsGetOperationsDirDict','fundFilingMaterialsDownload','fundFilingCheckFile','fundFilingFileListGroup'));

delete from bifrost_role_menu_function where
role_id in (select id from bifrost_role where code IN ('CHAKAN','COMPREHENSIVE_MANAGEMENT'))
and function_id in (select id from bifrost_function where code in ('afterFilingMaterialsBatchDownload','afterFilingMaterialsGetOperationsDirDict','afterLeasingFilingFileDownload','afterLeasingFilingFileListGroup'));



-- ============= 来自文件：20260206_luyujie_001_dml.sql ============= 

-- 新增印花税管理台账菜单
set @menuCode = 'stampDuty';
set @treeName = '财务管理';
set @menuId = (select id from bifrost_menu where code = @menuCode);

-- 可重复执行
delete from bifrost_custom_tree_menu_ref where menu_id = @menuId;
delete from bifrost_org_menu_function  where menu_id = @menuId;
delete from bifrost_menu where code = @menuCode;

-- 插入菜单
INSERT INTO bifrost_menu (code, level, sort_no, type, path, parent_id, icon, name, en_name, target, create_by,
                          update_by)
VALUES (@menuCode, 3, 100, null, '/budget/stampDuty', null, null, '印花税管理台账', null, '_self',
        null, null);
set @menuId = (select id from bifrost_menu where code = @menuCode);

-- 插入菜单关联
set @treeId = (select id from bifrost_custom_tree where name = @treeName);
INSERT INTO bifrost_custom_tree_menu_ref (custom_tree_id, menu_id, sort_no) VALUES (@treeId, @menuId, 0);

-- 插入接口分组（按操作分查看/编辑）
set @groupCode = 'stampDuty-Read';
delete from gruul_function_group where code = @groupCode;
INSERT INTO gruul_function_group (sort_no, code, name, group_describe, menu_id) VALUES ( 0, @groupCode, '印花税管理台账-查看', '印花税管理台账-查看', @menuId);
set @groupId = (select id from gruul_function_group where code = @groupCode);

-- 插入功能接口
set @fun1 = 'queryStampDutyList';
delete from bifrost_function where code = @fun1;
insert into bifrost_function (code, name, sort_no, menu_id, method, path, type,group_id)
values  (@fun1, '获取印花税管理台账列表', 0, @menuId, 'POST','/stampDuty/list', 1,@groupId);
set @funId1 = (select id from bifrost_function where code =@fun1);

set @fun2 = 'stampDutyIndexDownload';
delete from bifrost_function where code = @fun2;
INSERT INTO bifrost_function ( code, name, sort_no, menu_id, method, path, type,group_id)
VALUES (@fun2, '印花税管理台账列表导出', 0,  @menuId, 'POST','/stampDuty/export', 1,@groupId);
set @funId2 = (select id from bifrost_function where code =@fun2);

set @fun3 = 'stampDutyAdd';
delete from bifrost_function where code = @fun3;
INSERT INTO bifrost_function ( code, name, sort_no, menu_id, method, path, type,group_id)
VALUES (@fun3, '印花税管理台账新增', 0,  @menuId, 'POST','/stampDuty/add', 1,@groupId);
set @funId3 = (select id from bifrost_function where code =@fun3);

set @fun4 = 'stampDutyTemplateDownload';
delete from bifrost_function where code = @fun4;
INSERT INTO bifrost_function ( code, name, sort_no, menu_id, method, path, type,group_id)
VALUES (@fun4, '印花税管理台账模板下载', 0,  @menuId, 'get','/file/download/template', 1,@groupId);
set @funId4 = (select id from bifrost_function where code =@fun4);

set @fun5 = 'stampDutyImport';
delete from bifrost_function where code = @fun5;
INSERT INTO bifrost_function ( code, name, sort_no, menu_id, method, path, type,group_id)
VALUES (@fun5, '印花税管理导入', 0,  @menuId, 'POST','/stampDuty/import', 1,@groupId);
set @funId5 = (select id from bifrost_function where code =@fun5);

set @fun6 = 'stampDutyDelete';
delete from bifrost_function where code = @fun6;
INSERT INTO bifrost_function ( code, name, sort_no, menu_id, method, path, type,group_id)
VALUES (@fun6, '印花税管理删除', 0,  @menuId, 'POST','/stampDuty/delete', 1,@groupId);
set @funId6 = (select id from bifrost_function where code =@fun6);

set @fun7 = 'queryStampDutyContractList';
delete from bifrost_function where code = @fun7;
INSERT INTO bifrost_function ( code, name, sort_no, menu_id, method, path, type,group_id)
VALUES (@fun7, '印花税合同列表', 0,  @menuId, 'POST','/stampDuty/contract/list', 1,@groupId);
set @funId7 = (select id from bifrost_function where code =@fun7);

set @fun8 = 'stampDutyOrg';
delete from bifrost_function where code = @fun8;
insert into bifrost_function (code, name, sort_no, menu_id, method, path, type,group_id)
values  (@fun8, '获取印花税部门列表', 0, @menuId, 'GET','/stampDuty/orgs', 1,@groupId);
set @funId8 = (select id from bifrost_function where code =@fun8);

-- 查询计划财务部
set @orgId = (select id from bifrost_org where code = 'JHCWB');
-- 给公司和部门添加权限
insert into bifrost_org_menu_function (org_id, menu_id, function_id,  custom_tree_id)
values  -- 计划财务部
        ( @orgId, @menuId, null,  @treeId),
        ( @orgId, @menuId, @funId1, @treeId),
        ( @orgId, @menuId, @funId2, @treeId),
        ( @orgId, @menuId, @funId3, @treeId),
        ( @orgId, @menuId, @funId4, @treeId),
        ( @orgId, @menuId, @funId5, @treeId),
        ( @orgId, @menuId, @funId6, @treeId),
        ( @orgId, @menuId, @funId7, @treeId),
        ( @orgId, @menuId, @funId8, @treeId);

-- 插入角色菜单功能
delete from bifrost_role_menu_function where function_id in (select id from bifrost_function where code in ('queryStampDutyList','stampDutyIndexDownload','stampDutyAdd','stampDutyTemplateDownload','stampDutyImport','stampDutyDelete','queryStampDutyContractList','stampDutyOrg'));

insert into bifrost_role_menu_function (role_id, menu_id, function_id, custom_tree_id)
values
    -- 添加计划财务部
    ((select id from bifrost_role where code ='CWGL_1'),@menuId, @funId1,  @treeId),
    ((select id from bifrost_role where code ='CWGL_2'),@menuId, @funId1,  @treeId),
    ((select id from bifrost_role where code ='CWGL_3'),@menuId, @funId1,  @treeId),
    ((select id from bifrost_role where code ='ZJJL'),@menuId, @funId1,  @treeId),
    ((select id from bifrost_role where code ='CW-CWZJGL'),@menuId, @funId1,  @treeId),
    ((select id from bifrost_role where code ='ZLWLX'),@menuId, @funId1,  @treeId),
    ((select id from bifrost_role where code ='CWGL_1'),@menuId, @funId2,  @treeId),
    ((select id from bifrost_role where code ='CWGL_2'),@menuId, @funId2,  @treeId),
    ((select id from bifrost_role where code ='CWGL_3'),@menuId, @funId2,  @treeId),
    ((select id from bifrost_role where code ='ZJJL'),@menuId, @funId2,  @treeId),
    ((select id from bifrost_role where code ='CW-CWZJGL'),@menuId, @funId2,  @treeId),
    ((select id from bifrost_role where code ='ZLWLX'),@menuId, @funId2,  @treeId),
    ((select id from bifrost_role where code ='CWGL_1'),@menuId, @funId3,  @treeId),
    ((select id from bifrost_role where code ='CWGL_2'),@menuId, @funId3,  @treeId),
    ((select id from bifrost_role where code ='CWGL_3'),@menuId, @funId3,  @treeId),
    ((select id from bifrost_role where code ='ZJJL'),@menuId, @funId3,  @treeId),
    ((select id from bifrost_role where code ='CW-CWZJGL'),@menuId, @funId3,  @treeId),
    ((select id from bifrost_role where code ='ZLWLX'),@menuId, @funId3,  @treeId),
    ((select id from bifrost_role where code ='CWGL_1'),@menuId, @funId4,  @treeId),
    ((select id from bifrost_role where code ='CWGL_2'),@menuId, @funId4,  @treeId),
    ((select id from bifrost_role where code ='CWGL_3'),@menuId, @funId4,  @treeId),
    ((select id from bifrost_role where code ='ZJJL'),@menuId, @funId4,  @treeId),
    ((select id from bifrost_role where code ='CW-CWZJGL'),@menuId, @funId4,  @treeId),
    ((select id from bifrost_role where code ='ZLWLX'),@menuId, @funId4,  @treeId),
    ((select id from bifrost_role where code ='CWGL_1'),@menuId, @funId5,  @treeId),
    ((select id from bifrost_role where code ='CWGL_2'),@menuId, @funId5,  @treeId),
    ((select id from bifrost_role where code ='CWGL_3'),@menuId, @funId5,  @treeId),
    ((select id from bifrost_role where code ='ZJJL'),@menuId, @funId5,  @treeId),
    ((select id from bifrost_role where code ='CW-CWZJGL'),@menuId, @funId5,  @treeId),
    ((select id from bifrost_role where code ='ZLWLX'),@menuId, @funId5,  @treeId),
    ((select id from bifrost_role where code ='CWGL_1'),@menuId, @funId6,  @treeId),
    ((select id from bifrost_role where code ='CWGL_2'),@menuId, @funId6,  @treeId),
    ((select id from bifrost_role where code ='CWGL_3'),@menuId, @funId6,  @treeId),
    ((select id from bifrost_role where code ='ZJJL'),@menuId, @funId6,  @treeId),
    ((select id from bifrost_role where code ='CW-CWZJGL'),@menuId, @funId6,  @treeId),
    ((select id from bifrost_role where code ='ZLWLX'),@menuId, @funId6,  @treeId),
    ((select id from bifrost_role where code ='CWGL_1'),@menuId, @funId7,  @treeId),
    ((select id from bifrost_role where code ='CWGL_2'),@menuId, @funId7,  @treeId),
    ((select id from bifrost_role where code ='CWGL_3'),@menuId, @funId7,  @treeId),
    ((select id from bifrost_role where code ='ZJJL'),@menuId, @funId7,  @treeId),
    ((select id from bifrost_role where code ='CW-CWZJGL'),@menuId, @funId7,  @treeId),
    ((select id from bifrost_role where code ='ZLWLX'),@menuId, @funId7,  @treeId),
    ((select id from bifrost_role where code ='CWGL_1'),@menuId, @funId8,  @treeId),
    ((select id from bifrost_role where code ='CWGL_2'),@menuId, @funId8,  @treeId),
    ((select id from bifrost_role where code ='CWGL_3'),@menuId, @funId8,  @treeId),
    ((select id from bifrost_role where code ='ZJJL'),@menuId, @funId8,  @treeId),
    ((select id from bifrost_role where code ='CW-CWZJGL'),@menuId, @funId8,  @treeId),
    ((select id from bifrost_role where code ='ZLWLX'),@menuId, @funId8,  @treeId),
    -- 添加管理员
    ((select id from bifrost_role where code ='ADMIN_SUPERADMIN'),@menuId, @funId1,  @treeId),
    ((select id from bifrost_role where code ='ADMIN_SUPERADMIN'),@menuId, @funId2,  @treeId),
    ((select id from bifrost_role where code ='ADMIN_SUPERADMIN'),@menuId, @funId8,  @treeId);


-- ============= 来自文件：20250206_linlili_010_dml.sql ============= 

update bifrost_menu set sort_no = 1 where code = 'archivesOtherFilingMaterials' and `path` ='/archives/otherFilingMaterials';
