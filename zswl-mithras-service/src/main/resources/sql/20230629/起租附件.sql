-- 添加接口权限

insert into bifrost_function (code, name, sort_no, menu_id, method, path, type, group_id)
select 'contractStartRentFileDownload',
       '合同起租-材料下载',
       0,
       a.id,
       'GET',
       '/file/download',
       1,
       b.id
from bifrost_menu a,
     gruul_function_group b
where a.name = '合同管理'
  and b.group_describe = '合同管理-查看';
insert into bifrost_function (code, name, sort_no, menu_id, method, path, type, group_id)
select 'contractStartRentFileBatchDownload',
       '合同起租-材料批量下载',
       0,
       a.id,
       'GET',
       '/file/batch/download',
       1,
       b.id
from bifrost_menu a,
     gruul_function_group b
where a.name = '合同管理'
  and b.group_describe = '合同管理-查看';

insert into bifrost_function (code, name, sort_no, menu_id, method, path, type, group_id)
select 'contractflowstartRentupload',
       '合同起租-材料上传',
       0,
       a.id,
       'POST',
       '/contract/flow/startRent/upload',
       2,
       b.id
from bifrost_menu a,
     gruul_function_group b
where a.name = '合同管理'
  and b.group_describe = '合同管理-编辑';
insert into bifrost_function (code, name, sort_no, menu_id, method, path, type, group_id)
select 'contractStartRentFileBatchRemove',
       '合同起租-材料删除',
       0,
       a.id,
       'POST',
       '/file/batch/remove',
       2,
       b.id
from bifrost_menu a,
     gruul_function_group b
where a.name = '合同管理'
  and b.group_describe = '合同管理-编辑';

