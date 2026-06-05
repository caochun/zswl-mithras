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
VALUES ('groupCreditReviewTrackEventClose', '授信评审会议纪要表-跟踪事项关闭', 0, (select id from bifrost_menu where code = 'groupCreditReview'), NULL,
        NULL, NULL, 'POST', '/trackEvent/close', 2, NULL);
