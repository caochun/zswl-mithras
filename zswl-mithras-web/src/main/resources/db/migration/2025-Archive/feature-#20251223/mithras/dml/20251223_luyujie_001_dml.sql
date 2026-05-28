-- 风险策略模块未实现动态监测，停用模块
-- 删除风险策略菜单对应权限
delete from bifrost_org_menu_function where menu_id = (select id from bifrost_menu where name = '风险策略' and code = 'riskriskStrategy');

delete from bifrost_role_menu_function where menu_id = (select id from bifrost_menu where name = '风险策略' and code = 'riskriskStrategy');

-- 删除风险策略菜单
delete from bifrost_menu where name = '风险策略' and code = 'riskriskStrategy';


















-- end