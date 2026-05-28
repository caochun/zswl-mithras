insert into bifrost_function (code, name, sort_no, menu_id, method, path, type)
select'flowexecutionrejectAll','审批流一键拒绝',0,a.id,'POST','/flow/execution/rejectAll',2 from bifrost_menu a where a.name='流程查询';
insert into bifrost_function (code, name, sort_no, menu_id, method, path, type)
select'flowexecutionpassAll','审批流一键通过',0,a.id,'POST','/flow/execution/passAll',2 from bifrost_menu a where a.name='流程查询';
insert into bifrost_function (code, name, sort_no, menu_id, method, path, type)
select'flowexecutiontransfer','审批流一键转办',0,a.id,'POST','/flow/execution/transfer',2 from bifrost_menu a where a.name='流程查询';
insert into bifrost_function (code, name, sort_no, menu_id, method, path, type)
select'flowexecutionjump','审批流一键跳转',0,a.id,'POST','/flow/execution/jump',2 from bifrost_menu a where a.name='流程查询';
