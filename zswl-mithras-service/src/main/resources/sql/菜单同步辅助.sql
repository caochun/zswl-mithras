-- 生成新增function的脚本，改下a.path like；查看类型：
select concat(
               'insert into bifrost_function (code, name, sort_no, menu_id, method, path, type, group_id) select',
               concat("'", a.code, "',"), concat("'", a.name, "',"), concat(a.sort_no, ','), concat('a.id,'),
               concat("'", a.method, "',"), concat("'", a.path, "',"), concat(a.type, ','), concat('b.id '),
               concat("from bifrost_menu a, gruul_function_group b where a.name='", b.name, "' "),
               concat("and b.group_describe='", c.group_describe, "' "),
               ';'
           )
from bifrost_function a
         join bifrost_menu b on a.menu_id = b.id
         join gruul_function_group c on a.menu_id = c.menu_id
where a.path like '%/process/modify/remark%'
  and c.name = '查看'
  and a.type = 1
order by a.menu_id;
-- 生成新增function的脚本；编辑类型：
select concat(
               'insert into bifrost_function (code, name, sort_no, menu_id, method, path, type, group_id) select',
               concat("'", a.code, "',"), concat("'", a.name, "',"), concat(a.sort_no, ','), concat('a.id,'),
               concat("'", a.method, "',"), concat("'", a.path, "',"), concat(a.type, ','), concat('b.id '),
               concat("from bifrost_menu a, gruul_function_group b where a.name='", b.name, "' "),
               concat("and b.group_describe='", c.group_describe, "' "),
               ';'
           )
from bifrost_function a
         join bifrost_menu b on a.menu_id = b.id
         join gruul_function_group c on a.menu_id = c.menu_id
where a.path like '%/process/modify/remark%'
  and c.name = '编辑'
  and a.type = 2
order by a.menu_id;

-- 看下别的功能有哪些角色配了，新功能业务配一下这些角色
select *
from bifrost_role
where id IN (select role_id
             from bifrost_role_menu_function
             where function_id IN (select id from bifrost_function where code = 'filelistversioncompare'));