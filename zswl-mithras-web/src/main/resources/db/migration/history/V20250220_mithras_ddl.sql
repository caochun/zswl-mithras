-- 客户评级展示逻辑优化 <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
alter table proj_establish_base_info add column evaluation_subject_rating_score varchar(20) default null comment '评估主体评级';
alter table proj_establish_base_info add column evaluation_subject_rating_score_id bigint(20) default null comment '评估主体评级记录id';
alter table proj_establish_base_info add column main_lessee_rating_score varchar(20) default null comment '主承租人评级';
alter table proj_establish_base_info add column main_lessee_rating_score_id bigint(20) default null comment '主承租人评级记录id';

alter table proj_establish_base_info_lib add column evaluation_subject_rating_score varchar(20) default null comment '评估主体评级';
alter table proj_establish_base_info_lib add column evaluation_subject_rating_score_id bigint(20) default null comment '评估主体评级记录id';
alter table proj_establish_base_info_lib add column main_lessee_rating_score varchar(20) default null comment '主承租人评级';
alter table proj_establish_base_info_lib add column main_lessee_rating_score_id bigint(20) default null comment '主承租人评级记录id';

alter table proj_review_base_info add column evaluation_subject_rating_score varchar(20) default null comment '评估主体评级';
alter table proj_review_base_info add column evaluation_subject_rating_score_id bigint(20) default null comment '评估主体评级记录id';
alter table proj_review_base_info add column main_lessee_rating_score varchar(20) default null comment '主承租人评级';
alter table proj_review_base_info add column main_lessee_rating_score_id bigint(20) default null comment '主承租人评级记录id';

alter table proj_review_base_info_lib add column evaluation_subject_rating_score varchar(20) default null comment '评估主体评级';
alter table proj_review_base_info_lib add column evaluation_subject_rating_score_id bigint(20) default null comment '评估主体评级记录id';
alter table proj_review_base_info_lib add column main_lessee_rating_score varchar(20) default null comment '主承租人评级';
alter table proj_review_base_info_lib add column main_lessee_rating_score_id bigint(20) default null comment '主承租人评级记录id';
-- 客户评级展示逻辑优化 <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<