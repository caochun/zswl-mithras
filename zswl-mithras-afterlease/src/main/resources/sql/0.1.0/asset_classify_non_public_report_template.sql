ALTER table after_lease_check_project_report_template MODIFY report_type VARCHAR(50);
INSERT INTO `after_lease_check_project_report_template` (`report_type`, `area_type`, `group_name`, `code`, `title`, `content_input_type`, `content_input_option`, `order_num`)
VALUES
('ASSET_CLASSIFY_NON_PUBLIC', 'SUMMARY', '检查总结', 'ACNP_S_1_01', '风险信号及重大事项、风险防范措施', 'textArea', NULL, 300),
('ASSET_CLASSIFY_NON_PUBLIC', 'SUMMARY', '检查总结', 'ACNP_S_1_02', '重要公开信息分析', 'textArea', NULL, 310),
('ASSET_CLASSIFY_NON_PUBLIC', 'SUMMARY', '检查总结', 'ACNP_S_1_03', '其他重大事项分析', 'textArea', NULL, 320);
