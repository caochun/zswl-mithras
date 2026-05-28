-- 合同正常结清
delete from flow_node_cc_config where flow_key = 'ContractNormalSettleFlow';
insert into flow_node_cc_config (flow_key,node_key,object_type,object_ids,create_by,update_by) values('ContractNormalSettleFlow','userTask_generalManager','user','cailili,renhezhong',3,3);
insert into flow_node_cc_config (flow_key,node_key,object_type,object_ids,create_by,update_by) values('ContractNormalSettleFlow','userTask_headofyyglb2','user','cailili,renhezhong',3,3);
insert into flow_node_cc_config (flow_key,node_key,object_type,object_ids,create_by,update_by) values('ContractNormalSettleFlow','userTask_generalManager','job','assetmanagement,headoflegalcompliance,financialdirector,leaderincharge,chiefriskofficer,generalmanager',3,3);
insert into flow_node_cc_config (flow_key,node_key,object_type,object_ids,create_by,update_by) values('ContractNormalSettleFlow','userTask_headofyyglb2','job','assetmanagement,headoflegalcompliance,financialdirector,leaderincharge,chiefriskofficer,generalmanager',3,3);



















-- end