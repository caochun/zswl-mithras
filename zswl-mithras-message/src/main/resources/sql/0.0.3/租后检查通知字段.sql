alter table zhfk_notice modify column `deal_user` varchar(500) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '处理用户列表，ALL则为所有用户';
