
INSERT INTO `bifrost_function` (`code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`,
                                `path`, `type`, `group_id`)
select 'provisionDetailIndexDownload',
       '下载word',
       0,
       a.`id`,
       NULL,
       NULL,
       NULL,
       'GET',
       '/kpi/provision/detail/export',
       1,
       b.`id`
from `bifrost_menu` a,
     `gruul_function_group` b
where a.`name` = '拨备计提'
  and b.`group_describe` = '拨备计提-查看';


INSERT INTO general_dictionary (`dict_key`, `dict_desc`, `code`, `display`, `sort`)
VALUES ('job', '岗位类型', 'headoflegalcompliance', '法律合规部负责人', 10);

INSERT INTO bifrost_system_config (`gmt_create`, `gmt_modified`, `config_key`, `config_value`, `created_by`,
                                   `updated_by`, `description`, `status`, `type`)
VALUES ('2023-07-26 08:34:26', '2023-07-27 14:35:41', 'flow.search.job_model_map',
        '{\n  \"assetmanagement\":[\"RiskControlOpinionHandleAfterLaunchFlow\",\"RiskControlOpinionHandleFlow\"],\n  \"riskdeptmanager\":[\"RiskControlOpinionHandleAfterLaunchFlow\",\"RiskControlOpinionHandleFlow\"],\n  \"riskmanager\":[\"RiskControlOpinionHandleAfterLaunchFlow\",\"RiskControlOpinionHandleFlow\"],\n  \"headoflegalcompliance\":[\"RiskControlOpinionHandleAfterLaunchFlow\",\"RiskControlOpinionHandleFlow\"]\n}',
        'admin', 'admin', '岗位能查看的全量流程', 0, 'Json');

CREATE TABLE proj_client_role
(
    `id`          bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
    `module_type` varchar(50) DEFAULT NULL COMMENT '业务模块：立项、评审、合同',
    `main_id`     bigint(20) DEFAULT NULL COMMENT '业务模块主id',
    `client_id`   bigint(20) DEFAULT NULL COMMENT '客户id',
    `role`        varchar(50) DEFAULT NULL COMMENT '在项目中的角色 承租人 or 担保人',
    `create_by`   bigint(20) DEFAULT NULL COMMENT '创建人、发起人',
    `create_time` datetime    DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`   bigint(20) DEFAULT NULL COMMENT '最后更新人id',
    `update_time` datetime    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4;


INSERT INTO bifrost_function (`code`, `name`, `sort_no`, `menu_id`, `method`,
                              `path`, `type`, `group_id`)
VALUES ('riskcontrolopinionmonitordetail', '舆情监测详情', 0, 398, 'POST',
        '/risk/control/opinion/monitor/detail', 1, NULL);

