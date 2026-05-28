-- -- XMX-76收款账户同步变更，存量账户更新
-- -- 资金管理--直融管理/间融管理--关联合同明细
update Fund_Direct_Financing_Pledge_info f set f.Account_Name=(select b1.Account_Name from base_data_bank_account b1 where b1.account_number = REPLACE(f.account_number, ' ', '')),f.Account_Bank=(select b2.Account_Bank from base_data_bank_account b2 where b2.account_number = REPLACE(f.account_number, ' ', '')) where exists(select 1 from base_data_bank_account b where b.account_number = REPLACE(f.account_number, ' ', ''));

-- -- 资金管理--流动性管理--预测参数配置--编辑--还款银行/还款账号
update fund_financing_account_setting f set f.Account_Bank=(select b2.Account_Bank from base_data_bank_account b2 where b2.account_number = REPLACE(f.account_number, ' ', '')) where exists(select 1 from base_data_bank_account b where b.account_number = REPLACE(f.account_number, ' ', ''));

-- -- 资金管理--间融管理--新增融资--我司还款账户--新增
update fund_financing_pay_account f set f.Account_Bank=(select b2.Account_Bank from base_data_bank_account b2 where b2.account_number = REPLACE(f.account_number, ' ', '')) where exists(select 1 from base_data_bank_account b where b.account_number = REPLACE(f.account_number, ' ', ''));

-- -- 资金管理--还本付息--还款账户
update Fund_Repay_Account f set f.Account_Bank=(select b2.Account_Bank from base_data_bank_account b2 where b2.account_number = REPLACE(f.account_number, ' ', '')) where exists(select 1 from base_data_bank_account b where b.account_number = REPLACE(f.account_number, ' ', ''));

-- -- 收付款管理--付款核销--新增/编辑我方银行账户名/开户行
update payment_actual_detail_unconfirmed f set f.Our_Account_Name=(select b1.Account_Name from base_data_bank_account b1 where b1.account_number = REPLACE(f.Our_Account_Number, ' ', '')),f.Our_Account_Bank=(select b2.Account_Bank from base_data_bank_account b2 where b2.account_number = REPLACE(f.Our_Account_Number, ' ', '')) where exists(select 1 from base_data_bank_account b where b.account_number = REPLACE(f.Our_Account_Number, ' ', ''));

-- -- 合同管理-收款账户-新增-收款方为甲方-账户名称/开户行
update Contract_Account f set f.Account_Name=(select b1.Account_Name from base_data_bank_account b1 where b1.account_number = REPLACE(f.Account_Num, ' ', '')),f.Account_Address=(select b2.Account_Bank from base_data_bank_account b2 where b2.account_number = REPLACE(f.Account_Num, ' ', '')) where exists(select 1 from base_data_bank_account b where b.account_number = REPLACE(f.Account_Num, ' ', ''));

-- -- 资金管理-点击流动性管理--账户余额明细
update account_balance_base_info f set f.Account_Bank=(select b2.Account_Bank from base_data_bank_account b2 where b2.account_number = REPLACE(f.account_number, ' ', '')) where exists(select 1 from base_data_bank_account b where b.account_number = REPLACE(f.account_number, ' ', ''));

-- -- 登录资金管理部角色下用户--点击同意工作台--项目视图--项目情况--点击“项目质押/监管情况”
update fund_financing_pledge_info f set f.Account_Name=(select b1.Account_Name from base_data_bank_account b1 where b1.account_number = REPLACE(f.account_number, ' ', '')),f.Account_Bank=(select b2.Account_Bank from base_data_bank_account b2 where b2.account_number = REPLACE(f.account_number, ' ', '')) where exists(select 1 from base_data_bank_account b where b.account_number = REPLACE(f.account_number, ' ', ''));

-- -- 收付款管理--收款核销--进入详情页--收款记录明细--点击“查看”
update collection_record_info f set f.Our_Account_Name=(select b1.Account_Name from base_data_bank_account b1 where b1.account_number = REPLACE(f.Our_account_number, ' ', '')),f.Our_Account_Bank=(select b2.Account_Bank from base_data_bank_account b2 where b2.account_number = REPLACE(f.Our_account_number, ' ', '')) where exists(select 1 from base_data_bank_account b where b.account_number = REPLACE(f.Our_account_number, ' ', ''));

-- -- 收付款管理--保证金管理--进入详情页--退款记录--点击“查看”
update margin_record_info f set f.Our_Account_Name=(select b1.Account_Name from base_data_bank_account b1 where b1.account_number = REPLACE(f.Our_account_number, ' ', '')),f.Our_Account_Bank=(select b2.Account_Bank from base_data_bank_account b2 where b2.account_number = REPLACE(f.Our_account_number, ' ', '')) where exists(select 1 from base_data_bank_account b where b.account_number = REPLACE(f.Our_account_number, ' ', ''));

-- end