-- 合同提前结清优化
INSERT INTO `bifrost_function` (`code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`)
VALUES
    ('contractprepaymentcalculation', '合同变更-提前还款-计算相关金额', 0, 20, NULL, NULL, NULL, 'POST', '/contract/prepayment/calculation', 1, NULL);

-- 运营管报-合同时效监控表
INSERT INTO `management_report` (`report_name`, `report_url`, `report_type`, `sort_num`, `report_type_name`, `report_source`, `report_key`)
VALUES
    ('合同时效监控表', '', 'yunying', 0, '运营报表', 'MITHRAS', 'HE_TONG_SHI_XIAO');
INSERT INTO `guanyuan_ds_info` (`business_key`, `guanyuan_ds_id`, `guanyuan_ds_remark`, `deleted`)
VALUES
    ('YunYingManageReportZLHeTongShiXiao', 'sbfcb2b736f3946bb873b8d5', '运营管报-合同（仅租赁类型）耗时ETL-结果', 0);

-- <<<<<<<<<<<<<<<<<<<<<<<<<<< BigBear SQL BEGIN>>>>>>>>>>>>>>>>>>>>>>>>>>>
INSERT INTO bifrost_function (code, name, sort_no, menu_id, method, path, type)
VALUES ('paymentCheckApplyAmount', '校验申请金额', 0, 15, 'POST', '/payment/check/apply/amount', 2);

INSERT INTO bifrost_function (code, name, sort_no, menu_id, method, path, type, group_id)
VALUES ( 'fileTemplateUpdate', '模板文件更新', 0, 492, 'POST', '/file/template/update', 1, 147);
-- <<<<<<<<<<<<<<<<<<<<<<<<<<< BigBear SQL END>>>>>>>>>>>>>>>>>>>>>>>>>>>>>