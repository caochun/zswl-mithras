-- 增加利息计算方式字段
alter table `proj_establish_lease_price` add column `interest_way` varchar(20) not null default 'ACTUAL_RATE' comment '利息计算方式' after `rental_calc_type`;
alter table `proj_establish_lease_price_lib` add column `interest_way` varchar(20) not null default 'ACTUAL_RATE' comment '利息计算方式' after `rental_calc_type`;
alter table `proj_establish_factoring_price` add column `interest_way` varchar(20) not null default 'ACTUAL_RATE' comment '利息计算方式' after `rental_calc_type`;
alter table `proj_establish_factoring_price_lib` add column `interest_way` varchar(20) not null default 'ACTUAL_RATE' comment '利息计算方式' after `rental_calc_type`;
alter table `proj_establish_aoc_price` add column `interest_way` varchar(20) not null default 'ACTUAL_RATE' comment '利息计算方式' after `rental_calc_type`;
alter table `proj_establish_aoc_price_lib` add column `interest_way` varchar(20) not null default 'ACTUAL_RATE' comment '利息计算方式' after `rental_calc_type`;

alter table `proj_review_lease_price` add column `interest_way` varchar(20) not null default 'ACTUAL_RATE' comment '利息计算方式' after `rental_calc_type`;
alter table `proj_review_lease_price_lib` add column `interest_way` varchar(20) not null default 'ACTUAL_RATE' comment '利息计算方式' after `rental_calc_type`;
alter table `proj_review_factoring_price` add column `interest_way` varchar(20) not null default 'ACTUAL_RATE' comment '利息计算方式' after `rental_calc_type`;
alter table `proj_review_factoring_price_lib` add column `interest_way` varchar(20) not null default 'ACTUAL_RATE' comment '利息计算方式' after `rental_calc_type`;
alter table `proj_review_aoc_price` add column `interest_way` varchar(20) not null default 'ACTUAL_RATE' comment '利息计算方式' after `rental_calc_type`;
alter table `proj_review_aoc_price_lib` add column `interest_way` varchar(20) not null default 'ACTUAL_RATE' comment '利息计算方式' after `rental_calc_type`;

alter table `contract_lease_price` add column `interest_way` varchar(20) not null default 'ACTUAL_RATE' comment '利息计算方式' after `rental_calc_type`;
alter table `contract_lease_price_lib` add column `interest_way` varchar(20) not null default 'ACTUAL_RATE' comment '利息计算方式' after `rental_calc_type`;
alter table `contract_factoring_price` add column `interest_way` varchar(20) not null default 'ACTUAL_RATE' comment '利息计算方式' after `repay_calc_type`;
alter table `contract_factoring_price_lib` add column `interest_way` varchar(20) not null default 'ACTUAL_RATE' comment '利息计算方式' after `repay_calc_type`;
alter table `contract_aoc_price` add column `interest_way` varchar(20) not null default 'ACTUAL_RATE' comment '利息计算方式' after `repay_calc_type`;
alter table `contract_aoc_price_lib` add column `interest_way` varchar(20) not null default 'ACTUAL_RATE' comment '利息计算方式' after `repay_calc_type`;