-- 租金催收email表
CREATE TABLE `rent_collection_email_record` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `collection_id` bigint(20) DEFAULT NULL COMMENT '收款id',
    `receiver_mail` varchar(256) DEFAULT NULL COMMENT '收信邮箱',
    `comment` varchar(1024) DEFAULT NULL COMMENT '备注',
    `title` varchar(64) DEFAULT NULL COMMENT '邮件标题',
    `file_id` bigint(20) DEFAULT NULL COMMENT '文件id(用于前端onlyoffice预览)',
    `mail_content` longtext DEFAULT NULL COMMENT '邮件主体内容',
    `bank_id` bigint(20) DEFAULT NULL COMMENT '银行账号id',
    `html_key` varchar(32) DEFAULT NULL COMMENT 'htmlStore文件key 用于预览邮件',

    `create_by` bigint(20) DEFAULT NULL COMMENT '创建人id',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` bigint(20) DEFAULT NULL COMMENT '最后更新人id',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    PRIMARY KEY (`id`),
    KEY `idx_collection_id` (`collection_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='租金催收邮件发送记录';

CREATE TABLE `rent_collection_email_html_store` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `collection_id` bigint(20) DEFAULT NULL COMMENT '收款表id',
    `html_key` varchar(32) DEFAULT NULL COMMENT '键值（随机字符串）',
    `html_data` longtext DEFAULT NULL COMMENT '邮件主体内容',

    `create_by` bigint(20) DEFAULT NULL COMMENT '创建人id',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` bigint(20) DEFAULT NULL COMMENT '最后更新人id',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    PRIMARY KEY (`id`),
    KEY `idx_collection_id` (`collection_id`),
    KEY `idx_html_key` (`html_key`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='发送租金催收邮件html缓存';

-- email催收后 修改收款主表的催收次数 用于过滤条件
ALTER TABLE collection_base_info ADD email_notice_count int(11) DEFAULT 0 COMMENT '租金催收email发送次数';

CREATE TABLE `collection_penalty_reduction_info`
(
    `id`                                bigint(20) NOT NULL AUTO_INCREMENT,
    `contract_id`                       bigint(20) DEFAULT NULL COMMENT '合同id',
    `penalty_interest_deduction_amount` bigint(20) DEFAULT NULL COMMENT '罚息减免金额',
    `penalty_interest_surplus_amount`   bigint(20) DEFAULT NULL COMMENT '罚息减免剩余可用金额',
    `reason_explain`                    varchar(1000) DEFAULT NULL COMMENT '原因简述',
    `process_status`                    varchar(50)   DEFAULT NULL COMMENT '流程状态',
    `collection_status`                 varchar(50)   DEFAULT NULL COMMENT '罚息减免状态',
    `create_by`                         bigint(20) DEFAULT NULL COMMENT '创建人、发起人',
    `create_time`                       datetime      DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`                         bigint(20) DEFAULT NULL COMMENT '最后更新人id',
    `update_time`                       datetime      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='租后-罚息减免基本表';

alter table collection_base_info
    add penalty_interest_deduction_amount bigint(20) DEFAULT '0' COMMENT '罚息减免金额';

alter table collection_base_info
    add notice_financial_flag tinyint(1) DEFAULT NULL COMMENT '是否通知过苍穹（1是0否）';

alter table collection_base_info
    add overdue_collection_count int(11) DEFAULT '0' COMMENT '逾期催收次数';

alter table collection_base_info
    add plan_penalty_interest_date datetime DEFAULT NULL COMMENT '计划罚息收款日期';

alter table contract_base_info
    add overdue_collection_flag tinyint(1) DEFAULT '1' COMMENT '逾期催收状态0可催收，1不可催收';

alter table contract_base_info_lib
    add overdue_collection_flag tinyint(1) DEFAULT NULL COMMENT '逾期催收状态0可催收，1不可催收';


CREATE TABLE `collection_penalty_reduction_relation`
(
    `id`            BIGINT ( 20 ) NOT NULL AUTO_INCREMENT,
    `reduce_id`     BIGINT ( 20 ) DEFAULT NULL COMMENT '减免基本表id',
    `payment_code`  VARCHAR(50) DEFAULT NULL COMMENT '付款code 借据编号',
    `reduce_amount` BIGINT ( 20 ) DEFAULT NULL COMMENT '罚息减免剩余金额',
    `code`          VARCHAR(50) DEFAULT NULL COMMENT '收款编号 现金流编号',
    `status`        tinyint(1) DEFAULT NULL COMMENT '罚息减免状态 0失效，1生效',
    `create_by`     BIGINT ( 20 ) DEFAULT NULL COMMENT '创建人、发起人',
    `create_time`   datetime    DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`     BIGINT ( 20 ) DEFAULT NULL COMMENT '最后更新人id',
    `update_time`   datetime    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = INNODB DEFAULT CHARSET = utf8mb4 COMMENT = '租后-罚息减免关联收款表';