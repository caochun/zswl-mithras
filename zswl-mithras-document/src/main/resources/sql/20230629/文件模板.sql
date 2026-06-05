--还需要将模板在文件模板管理处上传。

-- 添加菜单
insert into bifrost_menu(code, level, sort_no, path, name)
values ('baseDatatemplate', 3, 0, '/baseData/template', '文件模板管理');
insert into bifrost_custom_tree_menu_ref(custom_tree_id, menu_id)
select a.id, b.id
from bifrost_custom_tree a,
     bifrost_menu b
where a.name = '基础数据设置'
  and b.name = '文件模板管理';

-- 添加功能分组
insert into gruul_function_group(sort_no, code, name, group_describe, menu_id)
select 0, 'baseDatatemplate-Read', '文件模板管理-查看', '文件模板管理-查看', a.id
from bifrost_menu a
where a.name = '文件模板管理';
insert into gruul_function_group(sort_no, code, name, group_describe, menu_id)
select 1, 'baseDatatemplate-Write', '文件模板管理-编辑', '文件模板管理-编辑', a.id
from bifrost_menu a
where a.name = '文件模板管理';

--添加功能
insert into bifrost_function (code, name, sort_no, menu_id, method, path, type, group_id)
select 'filetemplatetypelist',
       '模板类型列表',
       0,
       a.id,
       'POST',
       '/file/template/type/list',
       1,
       b.id
from bifrost_menu a,
     gruul_function_group b
where a.name = '文件模板管理'
  and b.group_describe = '文件模板管理-查看';
insert into bifrost_function (code, name, sort_no, menu_id, method, path, type, group_id)
select 'filetemplatelist',
       '模板文件列表',
       0,
       a.id,
       'POST',
       '/file/template/list',
       1,
       b.id
from bifrost_menu a,
     gruul_function_group b
where a.name = '文件模板管理'
  and b.group_describe = '文件模板管理-查看';
insert into bifrost_function (code, name, sort_no, menu_id, method, path, type, group_id)
select 'filetemplatehistorylist',
       '模板文件历史记录',
       0,
       a.id,
       'POST',
       '/file/template/history/list',
       1,
       b.id
from bifrost_menu a,
     gruul_function_group b
where a.name = '文件模板管理'
  and b.group_describe = '文件模板管理-查看';

insert into bifrost_function (code, name, sort_no, menu_id, method, path, type, group_id)
select 'filetemplatetypeadd',
       '新增模板类型',
       0,
       a.id,
       'POST',
       '/file/template/type/add',
       2,
       b.id
from bifrost_menu a,
     gruul_function_group b
where a.name = '文件模板管理'
  and b.group_describe = '文件模板管理-编辑';
insert into bifrost_function (code, name, sort_no, menu_id, method, path, type, group_id)
select 'filetemplatetyperemove',
       '删除模板类型',
       0,
       a.id,
       'POST',
       '/file/template/type/remove',
       2,
       b.id
from bifrost_menu a,
     gruul_function_group b
where a.name = '文件模板管理'
  and b.group_describe = '文件模板管理-编辑';
insert into bifrost_function (code, name, sort_no, menu_id, method, path, type, group_id)
select 'filetemplateadd',
       '新增模板文件',
       0,
       a.id,
       'POST',
       '/file/template/add',
       2,
       b.id
from bifrost_menu a,
     gruul_function_group b
where a.name = '文件模板管理'
  and b.group_describe = '文件模板管理-编辑';
insert into bifrost_function (code, name, sort_no, menu_id, method, path, type, group_id)
select 'filetemplatereplace',
       '替换模板文件',
       0,
       a.id,
       'POST',
       '/file/template/replace',
       2,
       b.id
from bifrost_menu a,
     gruul_function_group b
where a.name = '文件模板管理'
  and b.group_describe = '文件模板管理-编辑';
insert into bifrost_function (code, name, sort_no, menu_id, method, path, type, group_id)
select 'filetemplatehistoryrollback',
       '回滚模板文件历史记录',
       0,
       a.id,
       'POST',
       '/file/template/history/rollback',
       2,
       b.id
from bifrost_menu a,
     gruul_function_group b
where a.name = '文件模板管理'
  and b.group_describe = '文件模板管理-编辑';

--还需要将模板在文件模板管理处上传。

