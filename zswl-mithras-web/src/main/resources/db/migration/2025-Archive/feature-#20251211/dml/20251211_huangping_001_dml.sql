-- 客户移交流程
delete from flow_node_cc_config where flow_key = 'ClientTransferFlow';
insert into flow_node_cc_config (flow_key,node_key,object_type,object_ids,create_by,update_by) values('ClientTransferFlow','Activity_0mlc19t','job','assetmanagement',3,3);
insert into flow_node_cc_config (flow_key,node_key,object_type,object_ids,create_by,update_by) values('ClientTransferFlow','Activity_1fw98an','job','assetmanagement',3,3);


-- 合同创建
delete from flow_node_cc_config where flow_key = 'ContractCreateFlow';
insert into flow_node_cc_config (flow_key,node_key,object_type,object_ids,create_by,update_by) values('ContractCreateFlow','userTask_generalManager','user','cailili,renhezhong',3,3);
insert into flow_node_cc_config (flow_key,node_key,object_type,object_ids,create_by,update_by) values('ContractCreateFlow','userTask_chairman','user','cailili,renhezhong',3,3);

-- 合同其他变更
delete from flow_node_cc_config where flow_key = 'ContractModifyFlow';
insert into flow_node_cc_config (flow_key,node_key,object_type,object_ids,create_by,update_by) values('ContractModifyFlow','userTask_generalManager','user','cailili,renhezhong',3,3);
insert into flow_node_cc_config (flow_key,node_key,object_type,object_ids,create_by,update_by) values('ContractModifyFlow','userTask_chairman','user','cailili,renhezhong',3,3);

-- 合同正常结清
delete from flow_node_cc_config where flow_key = 'ContractNormalSettleFlow';
insert into flow_node_cc_config (flow_key,node_key,object_type,object_ids,create_by,update_by) values('ContractNormalSettleFlow','userTask_generalManager','user','cailili,renhezhong',3,3);
insert into flow_node_cc_config (flow_key,node_key,object_type,object_ids,create_by,update_by) values('ContractNormalSettleFlow','userTask_headofyyglb2','user','cailili,renhezhong',3,3);



















-- end