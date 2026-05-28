INSERT INTO `general_dictionary` (`dict_key`, `dict_desc`, `code`, `display`, `sort`)
VALUES
('job', '岗位类型', 'assetManagementReview', '资产管理复核岗', 10);


INSERT INTO bifrost_function (code, name, menu_id, method, path, type)
VALUES('dashboardAfterLeaseCheckStatistics', '业务工作台-租后管理-统计', (select id from bifrost_menu where code = 'employeeDashboard'),
       'POST','/dashboard/after/lease/check/statistics', '2'),
      ('dashboardAfterLeaseCheckList', '业务工作台-租后管理-列表', (select id from bifrost_menu where code = 'employeeDashboard'), 'POST',
       '/dashboard/after/lease/check/list', '2');


