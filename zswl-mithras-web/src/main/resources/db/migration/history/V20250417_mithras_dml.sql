-- ABS分层核销 <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
INSERT INTO `bifrost_function` (`code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`)
VALUES
    ('bankcenterfinancerepaysplitwriteofflist', 'ABS/ABN业务获取还款计划拆分核销明细', 0, 495, NULL, NULL, NULL, 'POST', '/bank/center/finance/repay/split/writeoff/list', 1, NULL),
    ('bankcenterfinancerepaysplitlist', 'ABS/ABN业务获取还款计划拆分明细', 0, 495, NULL, NULL, NULL, 'POST', '/bank/center/finance/repay/split/list', 1, NULL),
    ('funddirectfinancingrepayactualsplitlist', '直接融资-实际还款表-拆分明细列表', 0, 490, NULL, NULL, NULL, 'POST', '/fund/direct/financing/repay/actual/split/list', 1, NULL);
-- ABS分层核销 <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
INSERT INTO `bifrost_function` (`code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`) VALUES ('ledgerdetailinit', '租赁物中文类型初始化', 0, 494, NULL, NULL, NULL, 'POST', '/ledger/detail/init', 2, NULL);

-- 评审会线上纪要 开始

--  会议纪要线上化权限

INSERT INTO `bifrost_function` (`code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`)
VALUES
('projReviewMeetMinuteBaseInfoDetail', '项目评审会议纪要表详情', 0, 12, NULL, NULL, NULL, 'POST', '/proj/review/meet/minute/base/info/detail', 2, NULL),
('projReviewMeetMinuteBaseInfoModify', '修改项目评审会议纪要表-保存', 0, 12, NULL, NULL, NULL, 'POST', '/proj/review/meet/minute/base/info/modify', 2, NULL),
('projReviewMeetMinuteBaseInfoSubmit', '修改项目评审会议纪要表-提交', 0, 12, NULL, NULL, NULL, 'POST', '/proj/review/meet/minute/base/info/submit', 2, NULL),
('projReviewMeetMinuteGetRelatedCustomers', '项目评审会议纪要-获取交易结构用户', 0, 12, NULL, NULL, NULL, 'POST', '/proj/review/meet/minute/get/related/customers', 2, NULL);


INSERT INTO `bifrost_function` (`code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`)
VALUES
('projReviewQuotationProposalCashflowplanUpload', '项目评审-评审会会议纪要-上传现金流计划表', 0, 12, NULL, NULL, NULL, 'POST', '/proj/review/quotation/proposal/cashflowplan/upload', 2,NULL),
('projReviewQuotationProposalCashflowplanList', '项目评审-评审会会议纪要-获取现金流计划表', 0, 12, NULL, NULL, NULL, 'POST', '/proj/review/quotation/proposal/cashflowplan/list', 2,NULL),
('projReviewQuotationProposalCashflowplanRentExport', '项目评审-评审会会议纪要-导出租金表', 0, 12, NULL, NULL, NULL, 'POST', '/proj/review/quotation/proposal/cashflowplan/rent/export', 2,NULL),
('projPricingCashflowplanMeetMinuteCompare', '获取现金流计划表与评审会纪要比对', 0, 12, NULL, NULL, NULL, 'POST', '/proj/pricing/cashflowplan/meet/minute/compare', 2,NULL),
('projReviewQuotationProposalCashflowplanCashflowExport', '项目评审-评审会会议纪要-导出现金流表', 0, 12, NULL, NULL, NULL, 'POST', '/proj/review/quotation/proposal/cashflowplan/cashflow/export', 2,NULL);

INSERT INTO `bifrost_function` (`code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`)
VALUES
('projReviewMeetMinuteTrackEventList', '项目评审会议纪要表-跟踪事项列表', 0, 12, NULL, NULL, NULL, 'POST', '/proj/review/meet/minute/trackEvent/list', 2, NULL);


INSERT INTO bifrost_function (code, name, menu_id, method, path, type)
VALUES ('projReviewMeetMinuteFileList', '项目评审会议纪要表--文件', 12, 'POST',
        '/file/list', '2'),
       ('projReviewMeetMinuteFileUpload', '项目评审会议纪要表--文件上传', 12, 'POST',
        '/file/upload', '2'),
       ('projReviewMeetMinuteFileBatchRemove', '项目评审会议纪要表--文件删除', 12, 'POST',
        '/file/batch/remove', '2'),
       ('projReviewMeetMinuteFileDownload', '项目评审会议纪要表--文件下载', 12, 'GET',
        '/file/download', '2');

INSERT INTO bifrost_function (code, name, menu_id, method, path, type)
VALUES ('projReviewQueryEffect', '评审生效模糊查询', 12, 'POST','/proj/review/query/effect', '2');



INSERT INTO `bifrost_function` (`code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`)
VALUES ('groupCreditReviewMeetMinuteBaseInfoDetail', '授信评审会议纪要表详情', 0, (select id from bifrost_menu where code = 'groupCreditReview'), NULL, NULL,
        NULL, 'POST', '/proj/review/meet/minute/base/info/detail', 2, NULL),
       ('groupCreditReviewMeetMinuteBaseInfoModify', '修改授信评审会议纪要表-保存', 0, (select id from bifrost_menu where code = 'groupCreditReview'), NULL, NULL,
        NULL, 'POST', '/proj/review/meet/minute/base/info/modify', 2, NULL),
       ('groupCreditReviewMeetMinuteBaseInfoSubmit', '修改授信评审会议纪要表-提交', 0, (select id from bifrost_menu where code = 'groupCreditReview'), NULL, NULL,
        NULL, 'POST', '/proj/review/meet/minute/base/info/submit', 2, NULL),
       ('groupCreditReviewMeetMinuteGetRelatedCustomers', '授信评审会议纪要-获取交易结构用户', 0, (select id from bifrost_menu where code = 'groupCreditReview'),
        NULL, NULL, NULL, 'POST', '/proj/review/meet/minute/get/related/customers', 2, NULL);


INSERT INTO `bifrost_function` (`code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`)
VALUES ('groupCreditReviewQuotationProposalCashflowplanUpload', '授信评审-评审会会议纪要-上传现金流计划表', 0,
        (select id from bifrost_menu where code = 'groupCreditReview'), NULL, NULL, NULL, 'POST',
        '/proj/review/quotation/proposal/cashflowplan/upload', 2, NULL),
       ('groupCreditReviewQuotationProposalCashflowplanList', '授信评审-评审会会议纪要-获取现金流计划表', 0,
        (select id from bifrost_menu where code = 'groupCreditReview'), NULL, NULL, NULL, 'POST', '/proj/review/quotation/proposal/cashflowplan/list',
        2, NULL),
       ('groupCreditReviewQuotationProposalCashflowplanRentExport', '授信评审-评审会会议纪要-导出租金表', 0,
        (select id from bifrost_menu where code = 'groupCreditReview'), NULL, NULL, NULL, 'POST',
        '/proj/review/quotation/proposal/cashflowplan/rent/export', 2, NULL),
       ('groupCreditReviewQuotationProposalCashflowplanCashflowExport', '授信评审-评审会会议纪要-导出现金流表', 0,
        (select id from bifrost_menu where code = 'groupCreditReview'), NULL, NULL, NULL, 'POST',
        '/proj/review/quotation/proposal/cashflowplan/cashflow/export', 2, NULL);

INSERT INTO `bifrost_function` (`code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`)
VALUES ('groupCreditReviewMeetMinuteTrackEventList', '授信评审会议纪要表-跟踪事项列表', 0, (select id from bifrost_menu where code = 'groupCreditReview'), NULL,
        NULL, NULL, 'POST', '/proj/review/meet/minute/trackEvent/list', 2, NULL);

INSERT INTO `bifrost_function` (`code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`)
VALUES
('projReviewTrackEventClose', '项目评审会议纪要表-跟踪事项关闭', 0, 12, NULL, NULL, NULL, 'POST', '/trackEvent/close', 2, NULL);

INSERT INTO `bifrost_function` (`code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`)
VALUES ('groupCreditReviewTrackEventClose', '授信评审会议纪要表-跟踪事项关闭', 0, (select id from bifrost_menu where code = 'groupCreditReview'), NULL,
        NULL, NULL, 'POST', '/trackEvent/close', 2, NULL);



INSERT INTO `bifrost_function` (`code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`)
VALUES ('paymentMeetMinuteCreditDateCheck', '授信评审会议纪要表-授信到期日判断', 0, (select id from bifrost_menu where code = 'QX0114'), NULL,
        NULL, NULL, 'POST', '/proj/review/meet/minute/credit/date/check', 2, NULL);


-- 评审会线上纪要 结束

-- <<<<<<<<<<<<<<<<<<<<<<<<< 资金优化 大熊 >>>>>>>>>>>>>>>>>>>>>>>
INSERT INTO bifrost_function (code, name, sort_no, menu_id, method, path, type)
VALUES ('FundFinancingPledgeContractSearch', '根据合同编号模糊查询', 0, 358,  'POST', '/fund/financing/pledge/contract/search', 2);
-- <<<<<<<<<<<<<<<<<<<<<<<<< 资金优化 大熊 >>>>>>>>>>>>>>>>>>>>>>>
