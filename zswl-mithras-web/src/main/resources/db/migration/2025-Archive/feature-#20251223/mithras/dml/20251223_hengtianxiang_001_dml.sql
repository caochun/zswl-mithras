-- 初始化流程提交时间，用于计算逾期天数
UPDATE new_after_lease_check_plan_client c
SET commit_time = null
where commit_time is not null;

UPDATE new_after_lease_check_plan_client c
SET commit_time = (SELECT START_TIME_
                   FROM ACT_HI_PROCINST
                   WHERE BUSINESS_KEY_ = CAST(c.id AS CHAR)
                     AND PROC_DEF_ID_ LIKE '%NewAfterLeaseCheckReportCommonlyFlow%'
                   ORDER BY START_TIME_ DESC
    LIMIT 1)
WHERE not EXISTS (SELECT 1
    FROM ACT_HI_PROCINST ah
    join act_ru_task au
    on ah.PROC_INST_ID_ = au.PROC_INST_ID_
    WHERE BUSINESS_KEY_ = CAST(c.id AS CHAR)
  AND au.PROC_DEF_ID_ LIKE '%NewAfterLeaseCheckReportCommonlyFlow%'
  and au.TASK_DEF_KEY_ = 'project_manager') AND commit_time IS NULL;

UPDATE new_after_lease_check_plan_client c
SET commit_time = (SELECT START_TIME_
                   FROM ACT_HI_PROCINST
                   WHERE BUSINESS_KEY_ = CAST(c.id AS CHAR)
                     AND PROC_DEF_ID_ LIKE '%NewAfterLeaseCheckReportFlow%'
                   ORDER BY START_TIME_ DESC
    LIMIT 1)
WHERE not EXISTS (SELECT 1
    FROM ACT_HI_PROCINST ah
    join act_ru_task au
    on ah.PROC_INST_ID_ = au.PROC_INST_ID_
    WHERE BUSINESS_KEY_ = CAST(c.id AS CHAR)
  AND au.PROC_DEF_ID_ LIKE '%NewAfterLeaseCheckReportFlow%'
  and au.TASK_DEF_KEY_ = 'project_manager') AND commit_time IS NULL;


-- 新增租后检查管理台账菜单
set @menuCode = 'afterLeasecheckLedger';
set @treeName = '租后管理';
set @menuId = (select id from bifrost_menu where code = @menuCode);

-- 可重复执行
delete from bifrost_custom_tree_menu_ref where menu_id = @menuId;
delete from bifrost_org_menu_function  where menu_id = @menuId;
delete from bifrost_menu where code = @menuCode;

-- 插入菜单
INSERT INTO bifrost_menu (code, level, sort_no, type, path, parent_id, icon, name, en_name, target, create_by,
                          update_by)
VALUES (@menuCode, 3, 0, null, '/afterLease/manageLedger', null, null, '租后检查管理台账', null, '_self',
        null, null);
set @menuId = (select id from bifrost_menu where code = @menuCode);

-- 插入菜单关联
set @treeId = (select id from bifrost_custom_tree where name = @treeName);
INSERT INTO bifrost_custom_tree_menu_ref (custom_tree_id, menu_id, sort_no) VALUES (@treeId, @menuId, 0);

-- 插入接口分组（按操作分查看/编辑）
set @groupCode = 'afterLeasecheckPlanLedger-Read';
delete from gruul_function_group where code = @groupCode;
INSERT INTO gruul_function_group (sort_no, code, name, group_describe, menu_id) VALUES ( 0, @groupCode, '租后检查台账-查看', '租后检查台账-查看', @menuId);
set @groupId = (select id from gruul_function_group where code = @groupCode);

-- 插入功能接口
set @fun1 = 'queryCheckPlanLedgerList';
delete from bifrost_function where code = @fun1;
insert into bifrost_function (code, name, sort_no, menu_id, method, path, type,group_id)
values  (@fun1, '获取检查计划台账列表', 0, @menuId, 'POST','/afterlease/checkplan/ledger/list', 1,@groupId);
set @funId1 = (select id from bifrost_function where code =@fun1);

set @fun2 = 'newAfterLeaseCheckPlanLedgerIndexDownload';
delete from bifrost_function where code = @fun2;
INSERT INTO bifrost_function ( code, name, sort_no, menu_id, method, path, type,group_id)
VALUES (@fun2, '检查计划台账列表导出', 0,  @menuId, 'POST','/index/download', 1,@groupId);
set @funId2 = (select id from bifrost_function where code =@fun2);

-- 查询所有业务部门
select user_id,org_id,user_name,name from gruul_user_org_job guoj
                                              inner join bifrost_user bu on  bu.id = guoj.user_id
                                              inner join bifrost_org bo on bo.id = guoj.org_id
where org_id in (select org_id from bifrost_org where type = '1')
  and job_code ='businesshead';
select * from bifrost_org;
-- 给公司和部门添加权限
insert into bifrost_org_menu_function (org_id, menu_id, function_id,  custom_tree_id)
values  ( 1, @menuId, null,  18),
        ( 1, @menuId, @funId1, 18),
        ( 1, @menuId, @funId2, 18),
        -- 领导层
        ( 2, @menuId, null,  18),
        ( 2, @menuId, @funId1, 18),
        ( 2, @menuId, @funId2, 18),
        -- 风险管理部（业务评审部）
        ( 3, @menuId, null,  18),
        ( 3, @menuId, @funId1, 18),
        ( 3, @menuId, @funId2, 18),
        -- 法律合规部（资产保全部）
        ( 23, @menuId, null,  18),
        ( 23, @menuId, @funId1, 18),
        ( 23, @menuId, @funId2, 18),
        -- 高端装备业务部
        ( 6, @menuId, null,  18),
        ( 6, @menuId, @funId1, 18),
        ( 6, @menuId, @funId2, 18),
        -- 化工建材业务部
        ( 7, @menuId, null,  18),
        ( 7, @menuId, @funId1, 18),
        ( 7, @menuId, @funId2, 18),
        -- 浙江业务部
        ( 9, @menuId, null,  18),
        ( 9, @menuId, @funId1, 18),
        ( 9, @menuId, @funId2, 18),
        -- 智能制造业务部
        ( 11, @menuId, null,  18),
        ( 11, @menuId, @funId1, 18),
        ( 11, @menuId, @funId2, 18),
        -- 交通物流业务部
        ( 12, @menuId, null,  18),
        ( 12, @menuId, @funId1, 18),
        ( 12, @menuId, @funId2, 18),
        -- 公用事业业务部
        ( 27, @menuId, null,  18),
        ( 27, @menuId, @funId1, 18),
        ( 27, @menuId, @funId2, 18),
        -- 航运业务部
        ( 31, @menuId, null,  18),
        ( 31, @menuId, @funId1, 18),
        ( 31, @menuId, @funId2, 18),
        -- 文化健康业务部
        ( 34, @menuId, null,  18),
        ( 34, @menuId, @funId1, 18),
        ( 34, @menuId, @funId2, 18),
        -- 工程建设业务部
        ( 35, @menuId, null,  18),
        ( 35, @menuId, @funId1, 18),
        ( 35, @menuId, @funId2, 18),
        -- 绿色产业业务部
        ( 37, @menuId, null,  18),
        ( 37, @menuId, @funId1, 18),
        ( 37, @menuId, @funId2, 18);

-- 添加业务部负责人
delete from bifrost_role_menu_function where role_id in (select id from bifrost_role where code in ('GDZB_HBD', 'HGJC_HBD', 'ZJ_HBD', 'ZNZZ_HBD', 'JTWL_HBD', 'GYSY_HBD', 'HY_HBD', 'WHJK_HBD', 'GCJS_HBD', 'LSCY_HBD'));
delete from gruul_user_org_role where role_id in (select id from bifrost_role where code in ('GDZB_HBD', 'HGJC_HBD', 'ZJ_HBD', 'ZNZZ_HBD', 'JTWL_HBD', 'GYSY_HBD', 'HY_HBD', 'WHJK_HBD', 'GCJS_HBD', 'LSCY_HBD'));
delete from bifrost_role where code in ('GDZB_HBD', 'HGJC_HBD', 'ZJ_HBD', 'ZNZZ_HBD', 'JTWL_HBD', 'GYSY_HBD', 'HY_HBD', 'WHJK_HBD', 'GCJS_HBD', 'LSCY_HBD');
insert into bifrost_role (code, name, type, org_id)
values
    ('GDZB_HBD', '高端装备业务部负责人', 'default', 6),
    ('HGJC_HBD', '化工建材业务部负责人', 'default', 7),
    ('ZJ_HBD', '浙江业务部负责人', 'default', 9),
    ('ZNZZ_HBD', '智能制造业务部负责人', 'default', 11),
    ('JTWL_HBD', '交通物流业务部负责人', 'default', 12),
    ('GYSY_HBD', '公用事业业务部负责人', 'default', 27),
    ('HY_HBD', '航运业务部负责人', 'default', 31),
    ('WHJK_HBD', '文化健康业务部负责人', 'default', 34),
    ('GCJS_HBD', '工程建设业务部负责人', 'default', 35),
    ('LSCY_HBD', '绿色产业业务部负责人', 'default', 37);

-- 插入角色菜单功能
insert into bifrost_role_menu_function (role_id, menu_id, function_id, custom_tree_id)
values
    ((select id from bifrost_role where code ='GDZB_HBD'),@menuId, @funId1,  @treeId),
    ((select id from bifrost_role where code ='GDZB_HBD'),@menuId, @funId2,  @treeId),
    ((select id from bifrost_role where code ='HGJC_HBD'),@menuId, @funId1,  @treeId),
    ((select id from bifrost_role where code ='HGJC_HBD'),@menuId, @funId2,  @treeId),
    ((select id from bifrost_role where code ='ZJ_HBD'),@menuId, @funId1,  @treeId),
    ((select id from bifrost_role where code ='ZJ_HBD'),@menuId, @funId2,  @treeId),
    ((select id from bifrost_role where code ='ZNZZ_HBD'),@menuId, @funId1,  @treeId),
    ((select id from bifrost_role where code ='ZNZZ_HBD'),@menuId, @funId2,  @treeId),
    ((select id from bifrost_role where code ='JTWL_HBD'),@menuId, @funId1,  @treeId),
    ((select id from bifrost_role where code ='JTWL_HBD'),@menuId, @funId2,  @treeId),
    ((select id from bifrost_role where code ='GYSY_HBD'),@menuId, @funId1,  @treeId),
    ((select id from bifrost_role where code ='GYSY_HBD'),@menuId, @funId2,  @treeId),
    ((select id from bifrost_role where code ='HY_HBD'),@menuId, @funId1,  @treeId),
    ((select id from bifrost_role where code ='HY_HBD'),@menuId, @funId2,  @treeId),
    ((select id from bifrost_role where code ='WHJK_HBD'),@menuId, @funId1,  @treeId),
    ((select id from bifrost_role where code ='WHJK_HBD'),@menuId, @funId2,  @treeId),
    ((select id from bifrost_role where code ='GCJS_HBD'),@menuId, @funId1,  @treeId),
    ((select id from bifrost_role where code ='GCJS_HBD'),@menuId, @funId2,  @treeId),
    ((select id from bifrost_role where code ='LSCY_HBD'),@menuId, @funId1,  @treeId),
    ((select id from bifrost_role where code ='LSCY_HBD'),@menuId, @funId2,  @treeId),
    -- 添加风险管理部（业务评审部）、法律合规部（资产保全部）、领导层
    ((select id from bifrost_role where code ='ZCGL'),@menuId, @funId1,  @treeId),
    ((select id from bifrost_role where code ='ZCGL'),@menuId, @funId2,  @treeId),
    ((select id from bifrost_role where code ='SXFXG'),@menuId, @funId1,  @treeId),
    ((select id from bifrost_role where code ='SXFXG'),@menuId, @funId2,  @treeId),
    ((select id from bifrost_role where code ='FLHGB-FZR'),@menuId, @funId1,  @treeId),
    ((select id from bifrost_role where code ='FLHGB-FZR'),@menuId, @funId2,  @treeId);

-- 插入用户角色
insert into gruul_user_org_role (user_id, org_id, role_id)
values
    (227, 6, (select id from bifrost_role where code ='GDZB_HBD')),
    (52, 7, (select id from bifrost_role where code ='HGJC_HBD')),
    (80, 9, (select id from bifrost_role where code ='ZJ_HBD')),
    (242, 11, (select id from bifrost_role where code ='ZNZZ_HBD')),
    (52, 12, (select id from bifrost_role where code ='JTWL_HBD')),
    (137, 27, (select id from bifrost_role where code ='GYSY_HBD')),
    (202, 31, (select id from bifrost_role where code ='HY_HBD')),
    (217, 34, (select id from bifrost_role where code ='WHJK_HBD')),
    (198, 35, (select id from bifrost_role where code ='GCJS_HBD')),
    (75, 37, (select id from bifrost_role where code ='LSCY_HBD'));
