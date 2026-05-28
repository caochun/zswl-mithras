

-- ============= 来自文件：20260109_luyujie_001_ddl.sql ============= 

-- cr_overdue_record phase
ALTER TABLE cr_overdue_record
ADD COLUMN phase VARCHAR(128)
COMMENT '期项'
AFTER `overdue_change_date`;

-- cr_overdue_record_draft phase
ALTER TABLE cr_overdue_record_draft
ADD COLUMN phase VARCHAR(128)
COMMENT '期项'
AFTER `report_flag`;

-- cr_overdue_record_full_snap phase
ALTER TABLE cr_overdue_record_full_snap
ADD COLUMN phase VARCHAR(128)
COMMENT '期项'
AFTER `overdue_change_date`;

-- cr_overdue_record_proc_snap phase
ALTER TABLE cr_overdue_record_proc_snap
ADD COLUMN phase VARCHAR(128)
COMMENT '期项'
AFTER `report_flag`;
