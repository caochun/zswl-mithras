
-- 租赁物查重
ALTER TABLE lease_item_list_row_data ADD match_columns varchar(50) NULL COMMENT '查重重复字段标记';
