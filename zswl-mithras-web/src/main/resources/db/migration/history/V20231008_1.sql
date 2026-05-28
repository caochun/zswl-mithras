-- 工商信息校验
CREATE TABLE `client_business_history`
(
    `id`                    bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `client_id`             bigint(20) DEFAULT NULL COMMENT '客户id',
    `tyc_name`              varchar(100)       DEFAULT NULL COMMENT '客户名称',
    `tyc_corp_represent`    varchar(100)       DEFAULT NULL COMMENT '法人代表',
    `tyc_share_holder_info` json               DEFAULT NULL COMMENT '股东信息',
    `create_time`           timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '记录创建时间',
    `update_time`           timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by`             bigint(20) DEFAULT NULL COMMENT '创建人',
    `update_by`             bigint(20) DEFAULT NULL COMMENT '更新人',
    PRIMARY KEY (`id`),
    KEY                     `client_business_history_client_id` (`client_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客户工商信息历史表';

CREATE TABLE `client_business_opinion`
(
    `id`          bigint    NOT NULL AUTO_INCREMENT COMMENT '主键',
    `flow_id`     varchar(100)       DEFAULT NULL COMMENT '流程id',
    `module_name` varchar(255)       DEFAULT NULL,
    `node_name`   varchar(100)       DEFAULT NULL COMMENT '处理节点名称',
    `contract_id` bigint             DEFAULT NULL,
    `opinion`     varchar(500)       DEFAULT NULL COMMENT '处理意见',
    `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '记录创建时间',
    `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by`   bigint             DEFAULT NULL COMMENT '创建人',
    `update_by`   bigint             DEFAULT NULL COMMENT '更新人',
    PRIMARY KEY (`id`),
    UNIQUE KEY `client_business_opinion_key` (`flow_id`,`node_name`) USING BTREE COMMENT '客户流程节点意见'
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4  COMMENT='客户工商信息处理意见表'
INSERT INTO `bifrost_function` (`code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`)
VALUES ('assetclassifyManualInitialDivision', '五级分类手动初分', 0, 208, NULL, NULL, NULL, 'POST', '/assetclassify/manual/initial/division', 2, NULL),
       ('assetclassifyClientRemove', '五级分类删除客户', 0, 208, NULL, NULL, NULL, 'POST', '/assetclassify/client/remove', 2, NULL),
       ('contractClientCompareBusiness', '合同比对承租人及担保人工商信息', 0, 20, NULL, NULL, NULL, 'POST', '/contract/client/compare/business', 1, NULL),
       ('paymentClientCompareBusiness', '付款比对承租人及担保人工商信息', 0, 15, NULL, NULL, NULL, 'POST', '/payment/client/compare/business', 1, NULL),
       ('clientBusinessOpinionAdd', '新增客户工商信息处理意见表', 0, 6, NULL, NULL, NULL, 'POST', '/client/business/opinion/add', 2, NULL),
       ('clientBusinessOpinionList', '客户工商信息处理意见表列表', 0, 6, NULL, NULL, NULL, 'POST', '/client/business/opinion/list', 1, NULL);
INSERT INTO general_dictionary (dict_key, dict_desc, code, display, sort) VALUES ('FILE_TEMPLATE_TYPE', '文件模板类型', '资产五级分类', '资产五级分类', 14);
-- auto-generated definition
create table flow_task_duration
(
    id                 bigint auto_increment
        primary key,
    proc_inst_id       varchar(64) null comment '流程实例id',
    task_id            varchar(64) null comment '任务id',
    task_def_key       varchar(255) null comment 'task定义key；e.g：userTask_bizDivisionLeader',
    assignee           bigint null comment '审批人id',
    task_start_at      datetime null comment '任务创建时间',
    task_end_at        datetime null comment '任务结束时间',
    task_cost_workdays int null comment '耗费工作日',
    task_cost_days     int null comment '任务耗费天数',
    create_time        datetime default CURRENT_TIMESTAMP null,
    create_by          bigint null,
    update_time        datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    update_by          bigint null,
    constraint flow_task_duration_task_id_uindex
        unique (task_id)
) comment '审批任务耗时表';

create
index flow_task_duration_proc_inst_id_index
    on flow_task_duration (proc_inst_id);
-- 兼容无主办客户
ALTER TABLE asset_classify_client modify COLUMN belong_dept_id BIGINT(20) DEFAULT null comment '归属部门id';
ALTER TABLE asset_classify_client modify COLUMN belong_sponsor_id BIGINT(20) DEFAULT null comment '归属部门id';
ALTER TABLE asset_classify_client_auxiliary_lib modify COLUMN belong_dept_id BIGINT(20) DEFAULT null comment '归属部门id';
ALTER TABLE asset_classify_client_auxiliary_lib modify COLUMN belong_sponsor_id BIGINT(20) DEFAULT null comment '归属部门id';
ALTER TABLE asset_classify_client_lib modify COLUMN belong_dept_id BIGINT(20) DEFAULT null comment '归属部门id';
ALTER TABLE asset_classify_client_lib modify COLUMN belong_sponsor_id BIGINT(20) DEFAULT null comment '归属部门id';