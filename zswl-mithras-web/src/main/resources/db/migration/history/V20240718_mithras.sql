INSERT INTO `bifrost_function` (`code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`)
VALUES
	('thirdcqrecordignore', '苍穹接口调用记录-忽略', 0, 495, NULL, NULL, NULL, 'POST', '/third/cq/record/ignore', 2, NULL),
	('thirdcqrecordpush', '苍穹接口调用记录-推送', 0, 495, NULL, NULL, NULL, 'POST', '/third/cq/record/push', 2, NULL),
	('thirdcqrecordpagelist', '苍穹接口调用记录-分页列表', 0, 495, NULL, NULL, NULL, 'POST', '/third/cq/record/pagelist', 1, NULL);
