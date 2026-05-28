-- 审批流优化APP端
alter table `contract_base_info` add column `adjust_remark` text default null comment '调整说明';
alter table `contract_base_info_lib` add column `adjust_remark` text default null comment '调整说明';

alter table `contract_settle_plan` add column `settle_remark` text default null comment '结清说明';
alter table `contract_settle_plan_lib` add column `settle_remark` text default null comment '结清说明';

INSERT INTO `bifrost_function` (`code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`)
VALUES
	('contractbaseinfoadjustremarksave', '保存调整说明', 0, 20, NULL, NULL, NULL, 'POST', '/contract/base/info/adjustremark/save', 2, NULL);

-- 增加岗位
INSERT INTO `general_dictionary` (`dict_key`, `dict_desc`, `code`, `display`, `sort`)
VALUES
	('job', '岗位类型', 'humanresourcessupervisor', '人力资源部负责人', 10);

