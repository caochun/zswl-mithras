-- 删除跟踪事项创建流程项目经理节点多余选项
DELETE FROM `ACT_RU_TASK`
WHERE ID_ in(
select ID_ from (
    SELECT t1.ID_
    FROM `ACT_RU_TASK` `t1`
    INNER JOIN `ACT_RE_PROCDEF` `t2` ON `t1`.`PROC_DEF_ID_` = `t2`.`ID_`
    INNER JOIN `ACT_HI_PROCINST` `t3` ON `t1`.`PROC_INST_ID_` = `t3`.`ID_`
    LEFT JOIN `track_event_info` `t8` ON `t8`.`id` = `t3`.`BUSINESS_KEY_`
    LEFT JOIN `Proj_Review_Base_Info` `t9` ON `t9`.`id` = `t8`.`biz_id`
    WHERE `t2`.`KEY_` = 'TrackEventCreateFlow'
      AND `t1`.`TASK_DEF_KEY_` = 'userTask_project_manager2'
      AND `t1`.`ASSIGNEE_` != `t9`.`proj_sponsor_user_id`
) a);

DELETE FROM `ACT_HI_TASKINST`
WHERE ID_ in(
select ID_ from (
    SELECT t1.ID_
    FROM `ACT_HI_TASKINST` `t1`
    INNER JOIN `ACT_RE_PROCDEF` `t2` ON `t1`.`PROC_DEF_ID_` = `t2`.`ID_`
    INNER JOIN `ACT_HI_PROCINST` `t3` ON `t1`.`PROC_INST_ID_` = `t3`.`ID_`
    LEFT JOIN `track_event_info` `t8` ON `t8`.`id` = `t3`.`BUSINESS_KEY_`
    LEFT JOIN `Proj_Review_Base_Info` `t9` ON `t9`.`id` = `t8`.`biz_id`
    WHERE `t1`.`END_TIME_` IS NULL
      AND `t2`.`KEY_` = 'TrackEventCreateFlow'
      AND `t1`.`TASK_DEF_KEY_` = 'userTask_project_manager2'
      AND `t1`.`ASSIGNEE_` != `t9`.`proj_sponsor_user_id`
) a);


















-- end