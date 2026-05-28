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
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO `asset_classify_client_risk_factor_template` (`type`, `risk_factor`, `has_risk`)
VALUES
	('租金回收', '租金逾期30天（含）以内', 0),
	('租金回收', '租金逾期30天以上，90天（含）以内', 0),
	('租金回收', '租金逾期90天以上，180天（含）以内', 0),
	('租金回收', '租金逾期180天以上，360天（含）以内', 0),
	('租金回收', '租金逾期360天（含）以内', 0),
	('租金回收', '租赁期内已完成展延期', 0),
	('租金回收', '展延期流程中', 0),
	('外部影响', '宏观经济、市场、行业等外部环境的变化对债务人的生产经营产生较大不利影响，明显影响债务人的偿还能力', 0),
	('外部影响', '债务人遭受重大自然灾害或意外事故且保险赔偿无法覆盖风险敞口导致显性风险发生', 0),
	('外部影响', '债务人/债务人实际控制人/债务人法人/评估主体/是指担保人涉及重大诉讼或负面舆情，且对债务人正常', 0),
	('外部影响', '债务人对其他经融机构发生实质逾期或债务重组', 0),
	('债务人', '债务人关键财务指标较上年同期出现重大不利变化且已影响偿还能力', 0),
	('债务人', '债务人流动性不足，需通过拍卖抵质押品、处置租赁物偿债', 0),
	('债务人', '债务人资不抵债，且无其他偿债来源', 0),
	('债务人', '债务人出现停产或半停产状况', 0),
	('债务人', '债务人依法宣告破产', 0),
	('债务人', '债务人最近一期审计报告为无法表示意见或否定意见', 0),
	('债务人', '债务人为境内外上市公司，且股票被作为ST处理', 0),
	('债务人', '债务人实际控制人、母公司发生重大不利变化', 0),
	('债务人', '债务人发生重组、改制、分立、兼并等组织形式改变，或业务性质及经营范围发生重大变化', 0),
	('债务人', '债务人或担保人利用企业兼并、重组、分立等形式恶意逃废债务', 0),
	('债务人', '自然人债务人死亡，或依法宣告失踪或死亡', 0),
	('债务人', '债务人或保证人连续2期（承租人或保证人编制报表的正常频率）无正当理由拒绝提供其财务报表', 0),
	('债务人', '出租人已向法院提起诉讼追偿', 0),
	('租赁物', '租赁物存在灭失或重大减值风险、出租人失去对租赁物的控制等情况', 0),
	('租赁物', '直租赁项目处于停建状态导致租金支付存在重大困难', 0),
	('担保人', '担保人由于财务状况严重恶化丧失担保能力', 0),
	('担保人', '担保人依法宣告破产或已无实质经营行为', 0);

INSERT INTO `bifrost_function` (`code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`)
VALUES
	('assetclassifyclientwithdrawalratio', '查询拨备计提', 0, 208, NULL, NULL, NULL, 'POST', '/assetclassify/client/withdrawal/ratio', 1, NULL),
	('assetclassifyclientwithdrawalratiomodify', '修改拨备计提', 0, 208, NULL, NULL, NULL, 'POST', '/assetclassify/client/withdrawal/ratio/modify', 2, NULL),
	('assetclassifyclientriskfactor', '查询风险因子', 0, 208, NULL, NULL, NULL, 'POST', '/assetclassify/client/risk/factor', 1, NULL),
	('assetclassifyclientriskfactormodify', '修改风险因子', 0, 208, NULL, NULL, NULL, 'POST', '/assetclassify/client/risk/factor/modify', 2, NULL),
	('assetclassifylastversion', '编辑区查询最新的文件版本数据', 0, 208, NULL, NULL, NULL, 'POST', '/assetclassify/last/version', 1, NULL),
	('assetclassifyclientreviewsubmit', '单个客户复核提交审批', 0, 208, NULL, NULL, NULL, 'POST', '/assetclassify/client/review/submit', 2, NULL);

