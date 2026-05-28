-- 合同正常结清新增租后结清文件模板
delete from file_authentication_config where file_type = '租后结清';
INSERT INTO file_authentication_config
( file_name, file_type, owner_type, owner_post, owner_id, create_by, update_by)
VALUES( NULL, '租后结清', 1, 'admin', NULL, NULL, NULL);


















-- end