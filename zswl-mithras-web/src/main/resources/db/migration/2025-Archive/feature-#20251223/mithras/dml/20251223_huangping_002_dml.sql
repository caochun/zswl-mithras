delete from bifrost_system_config where config_key in ('toSetEmails','ccSetEmails','bccSetEmails');
INSERT INTO bifrost_system_config ( config_key, config_value, created_by, updated_by, description, status, type)
VALUES ('toSetEmails', 'ctang@amarsoft.com', 'admin', 'admin', '测试环境-邮件发送人邮箱', 1, 'String');

INSERT INTO bifrost_system_config ( config_key, config_value, created_by, updated_by, description, status, type)
VALUES ('ccSetEmails', 'xfzhou1@amarsoft.com', 'admin', 'admin', '测试环境-邮件抄送人邮箱', 1, 'String');

INSERT INTO bifrost_system_config ( config_key, config_value, created_by, updated_by, description, status, type)
VALUES ('bccSetEmails', 'huangp57998@163.com', 'admin', 'admin', '测试环境-邮件密送人邮箱', 1, 'String');