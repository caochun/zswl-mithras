INSERT INTO general_dictionary (dict_key, dict_desc, code, display, sort)
VALUES ('job', '岗位类型', 'yinZhangGuanLi', '印章管理', 10);

-- 租赁物类型导入
create table leasehold_property
(
    id            bigint auto_increment comment '主键ID' primary key,
    contract_code varchar(50)                        not null comment '合同编号',
    type          varchar(20)                        not null comment '类型',
    create_by     bigint                             null comment '创建人、发起人',
    create_time   datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_by     bigint                             null comment '最后更新人id',
    update_time   datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '最后更新时间'
) comment '租赁物类型表';
INSERT INTO `bifrost_function` (`code`, `name`, `sort_no`, `menu_id`, `en_name`, `method`, `path`, `type`, `group_id`)
VALUES
	('leaseholdpropertylist', '租赁物类型列表', 0, 493, NULL, 'POST', '/leaseholdproperty/list', 2, NULL),
	('leaseholdpropertyimport', '租赁物类型导入', 0, 493, NULL, 'POST', '/leaseholdproperty/import', 2, NULL),
	('leaseholdpropertydownload', '租赁物类型模板下载', 0, 493, NULL, 'GET', '/file/download/template', 2, NULL);
insert into general_dictionary (dict_key, dict_desc, code, display)
values ('zlwType', '租赁物类型', 'General', '通用设备'),
       ('zlwType', '租赁物类型', 'Special', '专用设备'),
       ('zlwType', '租赁物类型', 'Transportation', '交通运输设备'),
       ('zlwType', '租赁物类型', 'ElectronicProductsAndCommunication', '电子产品及通信设备'),
       ('zlwType', '租赁物类型', 'Electrical', '电气设备'),
       ('zlwType', '租赁物类型', 'LandHousesAndStructures', '土地、房屋及构筑物'),
       ('zlwType', '租赁物类型', 'Other', '其他租赁物');
ALTER TABLE workbench_announcement ADD expiration_from Date NULL COMMENT '过期时间';
ALTER TABLE workbench_announcement ADD expiration_to Date NULL COMMENT '过期时间';
INSERT INTO `bifrost_function` (`code`, `name`, `sort_no`, `menu_id`, `method`, `path`, `type`, `group_id`)
VALUES ('paymentPolicyInfoRemoveBatch', '批量删除保单信息', 0, 15, 'POST', '/payment/policy/info/removeBatch', 2, 75),
       ('riskMetricFactorRefresh', '刷新财报数据', 0, 99, 'POST', '/risk/metric/factor/refresh', 2, 91),
       ('riskmetricfactordetailpagelist', '明细列表', 0, 99, 'POST', '/risk/metric/factor/detail/pagelist', 1, 28),
       ('riskMetricFactorFileRemove', '删除', 0, 99, 'POST', '/risk/metric/factor/file/remove', 2, 91);
DELETE
FROM workbench_shortcuts
WHERE id IN (1,2,3,4,6,7,8,9,10,11,12,13,14,15,16,17,20,21,22);