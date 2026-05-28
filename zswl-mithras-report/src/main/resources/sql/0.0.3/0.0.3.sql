ALTER TABLE cr_mortgage ADD `mortgage_contract_code_2` varchar(50) DEFAULT NULL COMMENT '抵押合同编号 不含中文及括号';
ALTER TABLE cr_pledge ADD `pledge_contract_code_2` varchar(50) DEFAULT NULL COMMENT '质押合同编号 不含中文及括号';
ALTER TABLE cr_guarantor ADD `guarante_contract_code_2` varchar(50) DEFAULT NULL COMMENT '保证合同编号 不含中文及括号';
