alter table payment_base_info add  down_payment_type TINYINT NULL COMMENT '首付款标志，0不包括，1包括';
alter table payment_base_info_lib add  down_payment_type TINYINT NULL COMMENT '首付款标志，0不包括，1包括';