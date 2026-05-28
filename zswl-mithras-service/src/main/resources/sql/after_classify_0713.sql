alter table asset_classify_client add column suggest_flag tinyint(2)  COMMENT '1.调整 0不调整';
alter table asset_classify_client_lib add column suggest_flag tinyint(2)  COMMENT '1.调整 0不调整';
alter table asset_classify_client_auxiliary_lib add column suggest_flag tinyint(2)  COMMENT '1.调整 0不调整';

alter table asset_classify_client add column suggest_reason VARCHAR(500)  COMMENT '建议原因';
alter table asset_classify_client_lib add column suggest_reason VARCHAR(500)  COMMENT '建议原因';
alter table asset_classify_client_auxiliary_lib add column suggest_reason VARCHAR(500)  COMMENT '建议原因';

update asset_classify_client set review_status = 'FINISH' where review_status != 'UN_SUBMIT';
update asset_classify_client set review_status = 'PROCESS' where review_status = 'UN_SUBMIT';

update asset_classify_client_lib set review_status = 'FINISH' where review_status != 'UN_SUBMIT';
update asset_classify_client_lib set review_status = 'PROCESS' where review_status = 'UN_SUBMIT';

update asset_classify_client_auxiliary_lib set review_status = 'FINISH' where review_status != 'UN_SUBMIT';
update asset_classify_client_auxiliary_lib set review_status = 'PROCESS' where review_status = 'UN_SUBMIT';