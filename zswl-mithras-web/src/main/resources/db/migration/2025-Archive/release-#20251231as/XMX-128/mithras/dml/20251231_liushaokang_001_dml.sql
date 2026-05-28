-- 资产五级分类初分按钮权限修改

-- 删除初分按钮的原来权限
delete from bifrost_role_menu_function where function_id in (select id from bifrost_function where code ='assetclassifyManualInitialDivision');

-- 删除王志鹏对应的角色
delete from gruul_user_org_role where
user_id = (select id from bifrost_user where account ='wangzhipeng')
and org_id = (select id from bifrost_org where code ='FXGLB_YWPS')
and role_id = (select id from bifrost_role where code ='SDCF');

-- 删除手动初分的角色
delete from bifrost_role where code = 'SDCF';

-- 增加一条手动初分的角色
insert into bifrost_role (code, name, type, org_id)
values
    ('SDCF', '手动初分', 'default', (select id from bifrost_org where code ='FXGLB_YWPS'));

-- 增加一条王志鹏对应的角色
insert into gruul_user_org_role (user_id, org_id, role_id)
values
    ((select id from bifrost_user where account ='wangzhipeng'), (select id from bifrost_org where code ='FXGLB_YWPS'), (select id from bifrost_role where code ='SDCF'));

-- 增加手动初分的角色的权限
insert into mithras_pre.bifrost_role_menu_function (role_id, menu_id, function_id, custom_tree_id)
values(
(select id from mithras_pre.bifrost_role where code ='SDCF'),
(select menu_id from mithras_pre.bifrost_function where code ='assetclassifyManualInitialDivision'),
(select id from mithras_pre.bifrost_function where code ='assetclassifyManualInitialDivision'),
(select id from mithras_pre.bifrost_custom_tree where code ='afterLeaseManager'));