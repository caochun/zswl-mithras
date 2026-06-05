INSERT INTO `bifrost_function` (`code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`)
VALUES ('paymentMeetMinuteCreditDateCheck', '授信评审会议纪要表-授信到期日判断', 0, (select id from bifrost_menu where code = 'QX0114'), NULL,
        NULL, NULL, 'POST', '/proj/review/meet/minute/credit/date/check', 2, NULL);
