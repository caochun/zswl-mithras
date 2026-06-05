ALTER TABLE ecl_execute_record
    ADD COLUMN `evaluation_subject_id` BIGINT(20) NULL DEFAULT null COMMENT '评估主体ID' AFTER `client_name`;
ALTER TABLE ecl_execute_record
    ADD COLUMN `evaluation_subject_name` varchar(50) NULL DEFAULT null COMMENT '评估主体名称' AFTER `client_name`;


ALTER TABLE ecl_execute_record_lib
    ADD COLUMN `evaluation_subject_id` BIGINT(20) NULL DEFAULT null COMMENT '评估主体ID' AFTER `client_name`;
ALTER TABLE ecl_execute_record_lib
    ADD COLUMN `evaluation_subject_name` varchar(50) NULL DEFAULT null COMMENT '评估主体名称' AFTER `client_name`;
