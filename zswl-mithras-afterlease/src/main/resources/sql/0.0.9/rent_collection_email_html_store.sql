# 邮件发送优化
alter table rent_collection_email_html_store add default_flag tinyint(4) comment '是否默认记录（无界面手填内容）';
alter table rent_collection_email_html_store add version int(11) comment '版本';
