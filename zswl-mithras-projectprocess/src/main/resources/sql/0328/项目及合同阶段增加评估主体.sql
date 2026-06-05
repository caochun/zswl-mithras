-- 添加项目
ALTER TABLE proj_establish_base_info
    ADD COLUMN `evaluation_subject_id` BIGINT(20) NULL DEFAULT null COMMENT '评估主体ID' AFTER `funds_purpose`;
ALTER TABLE proj_establish_base_info_lib
    ADD COLUMN `evaluation_subject_id` BIGINT(20) NULL DEFAULT null COMMENT '评估主体ID' AFTER `funds_purpose`;
ALTER TABLE proj_review_base_info
    ADD COLUMN `evaluation_subject_id` BIGINT(20) NULL DEFAULT null COMMENT '评估主体ID' AFTER `proj_source`;
ALTER TABLE proj_review_base_info_lib
    ADD COLUMN `evaluation_subject_id` BIGINT(20) NULL DEFAULT null COMMENT '评估主体ID' AFTER `proj_source`;

INSERT INTO `bifrost_function` (`code`, `name`, `sort_no`, `menu_id`, `method`, `path`, `type`)
select 'projEstablishGetClientAddress', '获取法人客户地址信息', 0, id, 'POST', '/proj/establish/get/client/address', 2
from bifrost_menu where `code` = 'QX0106';

INSERT INTO `bifrost_function` (`code`, `name`, `sort_no`, `menu_id`, `method`, `path`, `type`)
select 'projReviewGetClientAddress', '获取法人客户地址信息', 0, id, 'POST', '/proj/establish/get/client/address', 2
from bifrost_menu where `code` = 'QX0110';
