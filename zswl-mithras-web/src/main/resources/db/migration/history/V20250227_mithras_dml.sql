-- DML 脚本
-- 诉讼文书用印调整
INSERT INTO `bifrost_function` (`code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`)
VALUES
    ('printingremove', '文书用印删除', 0, 736, NULL, NULL, NULL, 'POST', '/printing/remove', 2, NULL);

-- prod 流动性优化-第二版
-- INSERT INTO bifrost_function (code, name, menu_id, method, path, type)
-- VALUES('liquidityManageRepay','还本付息','481','POST','/liquidity/manage/repay','2'),
--       ('liquidityManageRentIncome','租金流入','481','POST','/liquidity/manage/rent/income','2');

-- 工商信息校验
INSERT INTO bifrost_function (code, name, sort_no, menu_id, method, path, type, group_id)
VALUES ( 'corpCommerceValid', '客户工商信息校验', 0, 6,  'POST', '/corp/commerce/valid', 2, 69);