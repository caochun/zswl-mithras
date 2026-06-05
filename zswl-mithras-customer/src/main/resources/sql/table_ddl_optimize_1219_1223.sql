ALTER TABLE corp_commerce_info ADD enterprise_nature varchar(20) NULL COMMENT '企业性质' AFTER listed_company;
ALTER TABLE corp_commerce_info_lib ADD enterprise_nature varchar(20) NULL COMMENT '企业性质' AFTER listed_company;

# 已执行 解决线上合同审批通过报错的问题
# ALTER TABLE contract_lease_item_lib modify column storage_place varchar(100) DEFAULT '' COMMENT '存放地点';