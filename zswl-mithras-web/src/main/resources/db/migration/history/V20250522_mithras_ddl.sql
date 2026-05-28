alter table payment_base_info
    add is_same_start_date tinyint(1) null comment '是否同起租日';

alter table group_credit_establish_base_info add client_rating_score varchar(32) null comment '客户评价';
alter table group_credit_establish_base_info add client_rating_score_id bigint null comment '客户评级id';
alter table group_credit_establish_base_info add rating_update_time datetime null comment '评级时间';
alter table group_credit_establish_base_info_lib add client_rating_score varchar(32) null comment '客户评价';
alter table group_credit_establish_base_info_lib add client_rating_score_id bigint null comment '客户评级id';
alter table group_credit_establish_base_info_lib add rating_update_time datetime null comment '评级时间';


alter table group_credit_review_base_info add client_rating_score varchar(32) null comment '客户评价';
alter table group_credit_review_base_info add client_rating_score_id bigint null comment '客户评级id';
alter table group_credit_review_base_info add rating_update_time datetime null comment '评级时间';
alter table group_credit_review_base_info_lib add client_rating_score varchar(32) null comment '客户评价';
alter table group_credit_review_base_info_lib add client_rating_score_id bigint null comment '客户评级id';
alter table group_credit_review_base_info_lib add rating_update_time datetime null comment '评级时间';
