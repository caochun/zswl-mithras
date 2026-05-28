-- 征信报送优化
alter table cr_client_draft add column report_flag tinyint(4) NOT NULL DEFAULT 1 COMMENT '是否报送标识，默认为1';
alter table cr_guarantor_draft add column report_flag tinyint(4) NOT NULL DEFAULT 1 COMMENT '是否报送标识，默认为1';
alter table cr_mortgage_draft add column report_flag tinyint(4) NOT NULL DEFAULT 1 COMMENT '是否报送标识，默认为1';
alter table cr_overdue_record_draft add column report_flag tinyint(4) NOT NULL DEFAULT 1 COMMENT '是否报送标识，默认为1';
alter table cr_pledge_draft add column report_flag tinyint(4) NOT NULL DEFAULT 1 COMMENT '是否报送标识，默认为1';

alter table cr_client_proc_snap add column report_flag tinyint(4) NOT NULL DEFAULT 1 COMMENT '是否报送标识，默认为1';
alter table cr_guarantor_proc_snap add column report_flag tinyint(4) NOT NULL DEFAULT 1 COMMENT '是否报送标识，默认为1';
alter table cr_mortgage_proc_snap add column report_flag tinyint(4) NOT NULL DEFAULT 1 COMMENT '是否报送标识，默认为1';
alter table cr_overdue_record_proc_snap add column report_flag tinyint(4) NOT NULL DEFAULT 1 COMMENT '是否报送标识，默认为1';
alter table cr_pledge_proc_snap add column report_flag tinyint(4) NOT NULL DEFAULT 1 COMMENT '是否报送标识，默认为1';
-- 征信报送优化