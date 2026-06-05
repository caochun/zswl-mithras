ALTER TABLE payment_base_info_lib ADD version_type TINYINT(4) DEFAULT 1 COMMENT '版本标志，0无效，1有效...业务自扩展';
ALTER TABLE payment_planed_detail_lib ADD version_type TINYINT(4) DEFAULT 1 COMMENT '版本标志，0无效，1有效...业务自扩展';
ALTER TABLE payment_questionnaire_answer_lib ADD version_type TINYINT(4) DEFAULT 1 COMMENT '版本标志，0无效，1有效...业务自扩展';
--保单信息 版本表
CREATE TABLE `payment_policy_info_lib`
(
    `id`                   bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
    `payment_id`           bigint(20) DEFAULT NULL,
    `policy_code`          varchar(50)  DEFAULT NULL COMMENT '保单编号',
    `policy_amount`        bigint(20) DEFAULT NULL COMMENT '保单金额',
    `insurance_start_date` datetime     DEFAULT NULL COMMENT '保险起始日',
    `insurance_end_date`   datetime     DEFAULT NULL COMMENT '保险到期日',
    `insurance_company`    varchar(255) DEFAULT NULL COMMENT '保险公司名称',
    `create_by`            bigint(20) DEFAULT NULL COMMENT '创建人、发起人',
    `create_time`          datetime     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`            bigint(20) DEFAULT NULL COMMENT '最后更新人id',
    `update_time`          datetime     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `version`              varchar(40) NOT NULL COMMENT '版本号',
    `origin_id`            bigint(20) NOT NULL COMMENT '临时数据表id 需要用来比对数据 或者 流程拒绝时全量回写',
    `data_create_time`     datetime     DEFAULT NULL,
    `data_create_by`       bigint(20) DEFAULT NULL,
    `data_update_time`     datetime     DEFAULT NULL,
    `data_update_by`       bigint(20) DEFAULT NULL,
    `version_type`         tinyint(4) DEFAULT '1' COMMENT '版本标志，0无效，1有效...业务自扩展',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;
