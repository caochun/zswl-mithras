CREATE TABLE email_send_fail_log (
  id bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  to_set varchar(1000) NOT NULL COMMENT '接收人邮箱（逗号分隔）',
  cc_set varchar(1000) DEFAULT '' COMMENT '抄送人邮箱（逗号分隔）',
  bcc_set varchar(1000) DEFAULT '' COMMENT '密送人邮箱（逗号分隔）',
  email_type varchar(50) NOT NULL COMMENT '邮件类型',
  email_title varchar(255) NOT NULL COMMENT '邮件标题',
  business_data text COMMENT '业务数据（JSON格式存储）',
  error_msg text NOT NULL COMMENT '失败异常信息',
  create_by bigint(20) DEFAULT NULL COMMENT '创建人id',
  create_time datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_by bigint(20) DEFAULT NULL COMMENT '最后更新人id',
  update_time datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  retry_times int  NOT NULL DEFAULT 0 COMMENT '重试次数',
  retry_status tinyint(1) NOT NULL DEFAULT 0 COMMENT '重试状态（0=未重试，1=已重试，2=重试失败）',
  PRIMARY KEY (id),
  KEY idx_retry_status (retry_status) COMMENT '重试状态索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='邮件发送失败日志表';