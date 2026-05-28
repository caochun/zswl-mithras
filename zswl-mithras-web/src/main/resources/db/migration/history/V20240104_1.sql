ALTER TABLE risk_control_opinion_monitor add risk_type TINYINT(1) COMMENT '舆情类型: 1 基本舆情 2 工商舆情';

ALTER TABLE risk_control_opinion_monitor add new_type_opinion VARCHAR(20) COMMENT '工商舆情类型 工商舆情可用' ;

DROP TABLE IF EXISTS `ep_caseinfo`;
CREATE TABLE `ep_caseinfo`  (
                                `id` bigint(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                `case_number` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '案号',
                                `court_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '法院名称',
                                `judge` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '法官',
                                `judge_assistant` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '法官助理',
                                `case_date` datetime NULL DEFAULT NULL COMMENT '立案日期',
                                `session_date` datetime NULL DEFAULT NULL COMMENT '开庭日期',
                                `end_date` datetime NULL DEFAULT NULL COMMENT '结案日期',
                                `area` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '地区',
                                `case_status_desc` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '案件状态描述',
                                `case_status` int(11) NULL DEFAULT NULL COMMENT '案件状态代码',
                                `link_address` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '链接地址',
                                `insert_time` datetime NULL DEFAULT NULL COMMENT '消息插入时间',
                                `msg_update_time` datetime NULL DEFAULT NULL COMMENT '消息更新时间',
                                `company_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '公司名称',
                                `credit_code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '信用代码',
                                `jsid` bigint(11) NULL DEFAULT NULL COMMENT '唯一标识符',
                                `msg_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '舆情消息id',
                                `create_by` bigint(11) NULL DEFAULT NULL COMMENT '创建人',
                                `update_by` bigint(11) NULL DEFAULT NULL COMMENT '修改人',
                                `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                                PRIMARY KEY (`id`) USING BTREE,
                                UNIQUE INDEX `uniq_msg_id`(`msg_id`) USING BTREE,
                                UNIQUE INDEX `uniq_caseinfo_jsid`(`jsid`) USING BTREE,
                                INDEX `idx_jsid_msg_id`(`jsid`, `msg_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '立案信息' ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;

DROP TABLE IF EXISTS `ep_changeinfo`;
CREATE TABLE `ep_changeinfo`  (
                                  `id` bigint(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
                                  `enterprise_code` char(12) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '企业编码',
                                  `change_item` int(11) NULL DEFAULT NULL COMMENT '变更事项',
                                  `change_note` varchar(2000) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '变更事项描述',
                                  `before_change` longtext CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '变更前内容',
                                  `after_change` longtext CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '变更后内容',
                                  `change_date` datetime NULL DEFAULT NULL COMMENT '变更日期',
                                  `insert_time` datetime NOT NULL COMMENT '消息插入时间',
                                  `msg_update_time` datetime NOT NULL COMMENT '消息更新时间',
                                  `jsid` bigint(20) NOT NULL COMMENT 'JSID',
                                  `data_json` longtext CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT 'DataJson',
                                  `if_history` int(11) NULL DEFAULT NULL COMMENT '是否历史',
                                  `msg_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '疫情消息id',
                                  `create_by` bigint(11) NULL DEFAULT NULL COMMENT '创建人',
                                  `update_by` bigint(11) NULL DEFAULT NULL COMMENT '修改人',
                                  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                                  PRIMARY KEY (`id`) USING BTREE,
                                  UNIQUE INDEX `uniq_changeinfo_jsid`(`jsid`) USING BTREE,
                                  UNIQUE INDEX `uniq__msg_id`(`msg_id`) USING BTREE,
                                  INDEX `idx_jsid_msg_id`(`jsid`, `msg_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;


DROP TABLE IF EXISTS `ep_courtannounce`;
CREATE TABLE `ep_courtannounce`  (
                                     `id` bigint(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                     `announcement_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '公告类型',
                                     `case_number` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '案件号',
                                     `court_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '法院名称',
                                     `party_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '当事人名称',
                                     `plaintiff` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '原告',
                                     `state` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '省份信息',
                                     `state_code` int(11) NULL DEFAULT NULL COMMENT '省份代码',
                                     `publ_date` datetime NULL DEFAULT NULL COMMENT '发布日期',
                                     `publ_page` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '发布页码',
                                     `process_level` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '法律程序级别',
                                     `judge_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '法官姓名',
                                     `insert_time` datetime NULL DEFAULT NULL COMMENT '消息插入时间',
                                     `msg_update_time` datetime NULL DEFAULT NULL COMMENT '消息更新时间',
                                     `content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '公告内容',
                                     `company_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '公司名称',
                                     `credit_code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '信用代码',
                                     `jsid` int(11) NULL DEFAULT NULL COMMENT '唯一标识符',
                                     `msg_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '疫情消息id',
                                     `create_by` bigint(11) NULL DEFAULT NULL COMMENT '创建人',
                                     `update_by` bigint(11) NULL DEFAULT NULL COMMENT '修改人',
                                     `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                     `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                                     PRIMARY KEY (`id`) USING BTREE,
                                     UNIQUE INDEX `uniq_msg_id`(`msg_id`) USING BTREE,
                                     UNIQUE INDEX `uniq_courtannounce_jsid`(`jsid`) USING BTREE,
                                     INDEX `idx_jsid_msg_id`(`jsid`, `msg_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '法院公告' ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;

DROP TABLE IF EXISTS `ep_courtsession`;
CREATE TABLE `ep_courtsession`  (
                                    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                    `court_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '法院名称',
                                    `court_room` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '法庭名称',
                                    `department` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '法院部门',
                                    `lawful_day` datetime NULL DEFAULT NULL COMMENT '合法日期',
                                    `schedule_date` datetime NULL DEFAULT NULL COMMENT '日程日期',
                                    `case_number` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '案号',
                                    `subject_matter` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '案由',
                                    `subject_matter_code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '案由代码',
                                    `plaintiff` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '原告',
                                    `defendant` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '被告',
                                    `party_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '当事人名称',
                                    `chief_judge` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '首席法官',
                                    `state` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '省份信息',
                                    `state_code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '省份代码',
                                    `insert_time` datetime NULL DEFAULT NULL COMMENT '插入时间',
                                    `msg_update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
                                    `content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '公告内容',
                                    `company_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '公司名称',
                                    `credit_code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '信用代码',
                                    `jsid` bigint(11) NULL DEFAULT NULL COMMENT '唯一标识符',
                                    `msg_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '疫情消息id',
                                    `create_by` bigint(11) NULL DEFAULT NULL COMMENT '创建人',
                                    `update_by` bigint(11) NULL DEFAULT NULL COMMENT '修改人',
                                    `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                    `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                                    PRIMARY KEY (`id`) USING BTREE,
                                    UNIQUE INDEX `uniq_msg_id`(`msg_id`) USING BTREE,
                                    UNIQUE INDEX `uniq_courtsession_jsid`(`jsid`) USING BTREE,
                                    INDEX `idx_jsid_msg_id`(`jsid`, `msg_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '开庭公告' ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;

update `risk_control_opinion_monitor` set `risk_type` = 1;

INSERT INTO `bifrost_function` (`code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`)
VALUES
	('riskopinioninfoextracaseInfo', '立案信息', 0, 398, NULL, NULL, NULL, 'GET', '/risk/opinion/info/extra/caseInfo', 1, NULL),
	('riskopinioninfoextracourtSession', '开庭公告', 0, 398, NULL, NULL, NULL, 'GET', '/risk/opinion/info/extra/courtSession', 1, NULL),
	('riskopinioninfoextracourtAnnounce', '法院公告', 0, 398, NULL, NULL, NULL, 'GET', '/risk/opinion/info/extra/courtAnnounce', 1, NULL),
	('riskopinioninfoextrachangeInfo', '企业信息变更', 0, 398, NULL, NULL, NULL, 'GET', '/risk/opinion/info/extra/changeInfo', 1, NULL);
