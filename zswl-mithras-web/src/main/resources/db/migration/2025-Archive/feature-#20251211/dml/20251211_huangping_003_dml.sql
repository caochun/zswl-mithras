-- 客户移交流程
delete from flow_node_cc_config where flow_key = 'ClientTransferFlow';
insert into flow_node_cc_config (flow_key,node_key,object_type,object_ids,sort,create_by,update_by) values('ClientTransferFlow','Activity_0mlc19t','job','assetmanagement',1,3,3);
insert into flow_node_cc_config (flow_key,node_key,object_type,object_ids,sort,create_by,update_by) values('ClientTransferFlow','Activity_1fw98an','job','assetmanagement',1,3,3);


-- 合同创建
delete from flow_node_cc_config where flow_key = 'ContractCreateFlow';
insert into flow_node_cc_config (flow_key,node_key,object_type,object_ids,sort,create_by,update_by) values('ContractCreateFlow','userTask_generalManager','user','cailili,renhezhong',1,3,3);
insert into flow_node_cc_config (flow_key,node_key,object_type,object_ids,sort,create_by,update_by) values('ContractCreateFlow','userTask_chairman','user','cailili,renhezhong',1,3,3);

-- 合同其他变更
delete from flow_node_cc_config where flow_key = 'ContractModifyFlow';
insert into flow_node_cc_config (flow_key,node_key,object_type,object_ids,sort,create_by,update_by) values('ContractModifyFlow','userTask_generalManager','user','cailili,renhezhong',1,3,3);
insert into flow_node_cc_config (flow_key,node_key,object_type,object_ids,sort,create_by,update_by) values('ContractModifyFlow','userTask_chairman','user','cailili,renhezhong',1,3,3);

-- 合同正常结清
-- 合同正常结清
delete from flow_node_cc_config where flow_key = 'ContractNormalSettleFlow';
insert into flow_node_cc_config (flow_key,node_key,object_type,object_ids,sort,create_by,update_by) values('ContractNormalSettleFlow','userTask_generalManager','user','cailili,renhezhong',2,3,3);
insert into flow_node_cc_config (flow_key,node_key,object_type,object_ids,sort,create_by,update_by) values('ContractNormalSettleFlow','userTask_headofyyglb2','user','cailili,renhezhong',2,3,3);
-- 为总经理generalmanager、首席风险官chiefriskofficer、财务总监financialdirector、业务分管领导leaderincharge、法律合规部（资产管理部负责人）headoflegalcompliance、资产经理assetmanagement、蔡丽丽、任核众
insert into flow_node_cc_config (flow_key,node_key,object_type,object_ids,sort,create_by,update_by) values('ContractNormalSettleFlow','userTask_generalManager','job','generalmanager,chiefriskofficer,financialdirector,leaderincharge,headoflegalcompliance,assetmanagement',1,3,3);
insert into flow_node_cc_config (flow_key,node_key,object_type,object_ids,sort,create_by,update_by) values('ContractNormalSettleFlow','userTask_headofyyglb2','job','generalmanager,chiefriskofficer,financialdirector,leaderincharge,headoflegalcompliance,assetmanagement',1,3,3);


















-- end