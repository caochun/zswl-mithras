alter table asset_classify_client
    add provisions text null comment '合同拨备计提比例';

alter table asset_classify_client_lib
    add provisions text null comment '合同拨备计提比例';

alter table asset_classify_client_auxiliary_lib
    add provisions text null comment '合同拨备计提比例';

alter table asset_classify_client
    add risk_factor text null comment '风险因子';

alter table asset_classify_client_lib
    add risk_factor text null comment '风险因子';

alter table asset_classify_client_auxiliary_lib
    add risk_factor text null comment '风险因子';


CREATE TABLE `asset_classify_client_risk_factor_template`
(
    `id`          bigint(20) NOT NULL AUTO_INCREMENT,
    `type`        varchar(50)        DEFAULT NULL,
    `risk_factor` varchar(255)       DEFAULT NULL,
    `has_risk`    bit(1)             DEFAULT NULL,
    `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '记录创建时间',
    `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by`   bigint(20) DEFAULT NULL COMMENT '创建人',
    `update_by`   bigint(20) DEFAULT NULL COMMENT '更新人',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8;

BEGIN;
INSERT INTO `asset_classify_client_risk_factor_template` (`id`, `type`, `risk_factor`, `has_risk`, `create_time`,
                                                          `update_time`, `create_by`, `update_by`)
VALUES (1, '租金回收', '租金逾期30天（含）以内', b'0', '2023-09-05 03:27:58', '2023-09-05 03:27:58', NULL, NULL);
INSERT INTO `asset_classify_client_risk_factor_template` (`id`, `type`, `risk_factor`, `has_risk`, `create_time`,
                                                          `update_time`, `create_by`, `update_by`)
VALUES (2, '租金回收', '租金逾期30天以上，90天（含）以内', b'0', '2023-09-05 03:27:58', '2023-09-05 03:27:58', NULL, NULL);
INSERT INTO `asset_classify_client_risk_factor_template` (`id`, `type`, `risk_factor`, `has_risk`, `create_time`,
                                                          `update_time`, `create_by`, `update_by`)
VALUES (3, '租金回收', '租金逾期90天以上，180天（含）以内', b'0', '2023-09-05 03:27:58', '2023-09-05 03:27:58', NULL,
        NULL);
INSERT INTO `asset_classify_client_risk_factor_template` (`id`, `type`, `risk_factor`, `has_risk`, `create_time`,
                                                          `update_time`, `create_by`, `update_by`)
VALUES (4, '租金回收', '租金逾期180天以上，360天（含）以内', b'0', '2023-09-05 03:27:58', '2023-09-05 03:27:58', NULL,
        NULL);
INSERT INTO `asset_classify_client_risk_factor_template` (`id`, `type`, `risk_factor`, `has_risk`, `create_time`,
                                                          `update_time`, `create_by`, `update_by`)
VALUES (5, '租金回收', '租金逾期360天（含）以内', b'0', '2023-09-05 03:27:58', '2023-09-05 03:27:58', NULL, NULL);
INSERT INTO `asset_classify_client_risk_factor_template` (`id`, `type`, `risk_factor`, `has_risk`, `create_time`,
                                                          `update_time`, `create_by`, `update_by`)
VALUES (6, '租金回收', '租赁期内已完成展延期', b'0', '2023-09-05 03:27:58', '2023-09-05 03:27:58', NULL, NULL);
INSERT INTO `asset_classify_client_risk_factor_template` (`id`, `type`, `risk_factor`, `has_risk`, `create_time`,
                                                          `update_time`, `create_by`, `update_by`)
VALUES (7, '租金回收', '展延期流程中', b'0', '2023-09-05 03:27:58', '2023-09-05 03:27:58', NULL, NULL);
INSERT INTO `asset_classify_client_risk_factor_template` (`id`, `type`, `risk_factor`, `has_risk`, `create_time`,
                                                          `update_time`, `create_by`, `update_by`)
VALUES (8, '外部影响', '宏观经济、市场、行业等外部环境的变化对债务人的生产经营产生较大不利影响，明显影响债务人的偿还能力',
        b'0', '2023-09-05 03:27:58', '2023-09-05 03:27:58', NULL, NULL);
INSERT INTO `asset_classify_client_risk_factor_template` (`id`, `type`, `risk_factor`, `has_risk`, `create_time`,
                                                          `update_time`, `create_by`, `update_by`)
VALUES (9, '外部影响', '债务人遭受重大自然灾害或意外事故且保险赔偿无法覆盖风险敞口导致显性风险发生', b'0',
        '2023-09-05 03:27:58', '2023-09-05 03:27:58', NULL, NULL);
INSERT INTO `asset_classify_client_risk_factor_template` (`id`, `type`, `risk_factor`, `has_risk`, `create_time`,
                                                          `update_time`, `create_by`, `update_by`)
VALUES (10, '外部影响', '债务人/债务人实际控制人/债务人法人/评估主体/是指担保人涉及重大诉讼或负面舆情，且对债务人正常',
        b'0', '2023-09-05 03:27:58', '2023-09-05 03:27:58', NULL, NULL);
INSERT INTO `asset_classify_client_risk_factor_template` (`id`, `type`, `risk_factor`, `has_risk`, `create_time`,
                                                          `update_time`, `create_by`, `update_by`)
VALUES (11, '外部影响', '债务人对其他经融机构发生实质逾期或债务重组', b'0', '2023-09-05 03:27:58',
        '2023-09-05 03:27:58', NULL, NULL);
INSERT INTO `asset_classify_client_risk_factor_template` (`id`, `type`, `risk_factor`, `has_risk`, `create_time`,
                                                          `update_time`, `create_by`, `update_by`)
VALUES (12, '债务人', '债务人关键财务指标较上年同期出现重大不利变化且已影响偿还能力', b'0', '2023-09-05 03:27:58',
        '2023-09-05 03:27:58', NULL, NULL);
COMMIT;
