alter table rating_amount add column is_real_estate_adjust TINYINT(1) COMMENT '是否需要房地产调整';
alter table rating_amount add column is_stock_rights_adjust TINYINT(1) COMMENT '是否需要股权调整';

alter table rating_amount_lib add column is_real_estate_adjust TINYINT(1) COMMENT '是否需要房地产调整';
alter table rating_amount_lib add column is_stock_rights_adjust TINYINT(1) COMMENT '是否需要股权调整';