-- 账户表
select
`payment_apply_code` as `编号`,
`client_name` as `客户名称`,
case `biz_type` when 14 then '融资租赁' when 13 then '保理融资' when 41 then '垫款' end as `业务类型`,
case `rental_calc_type` when 11 then '等额本息' when 12 then '等额本金' when 21 then '到期一次还本付息' when 22 then '预先付息到期还本' when 29 then '其他类型非分期还款' when 19 then '其他类型分期还款' end as `租金计算方式`,
case `repay_rate` when 10 then '月' when 21 then '双月' when 22 then '季' when 23 then '半年' when 24 then '年' when 40 then '还款间隔不固定' when 99 then '其他' end as `还款频率`,
round(`payment_amount` / 10000.00, 2) as `借款金额（元）`,
round(`earnest_money` / 10000.00, 2) as `保证金（元）`, 
`proj_lease_month_count` as `借款期限（月）`,
`lending_date` as `放款日期`,
`closed_date` as `结清日期`
from `cr_account`;

-- 还款表
select
a.`payment_apply_code` as `编号`,
b.`contract_code` as `合同编号`, 
a.`phase` as `期项`,
a.`cash_flow_date` as `应收日期`,
a.`grace_period` as `宽限期（天）`,
round(a.`rent` / 10000.00, 2) as `应收租金（元）`, 
round(a.`principal` / 10000.00, 2) as `应收本金（元）`, 
round(c.`actual_collection_amount` / 10000.00, 2) as `收款金额（元）`, 
round(c.`actual_collection_principal` / 10000.00, 2) as `实收本金（元）`
from `mithras_pre_report`.`cr_repay_plan` as a 
left join `mithras_pre`.`contract_base_info` as b on a.`contract_id` = b.`id`
left join (select `payment_apply_code`, `phase`, sum(`collection_amount`) as `actual_collection_amount`, sum(`collection_principal`) as `actual_collection_principal` from `mithras_pre_report`.`cr_actual_repay` group by `payment_apply_code`, `phase`) as c on a.`payment_apply_code` = c.`payment_apply_code` and a.`phase` = c.`phase`;

-- 特定交易表
select
a.`payment_apply_code` as `编号`, 
b.`contract_code` as `合同编号`,
case(a.`trade_type`) when 11 then '展期' when 12 then '提前结清' end as `交易类型`, 
a.`trade_date` as `交易日期`, 
round(a.`trade_amount` / 10000.00, 2) as `交易金额（元）`, 
a.`change_month_count` as `到期日变更月数（月）`
from `mithras_pre_report`.`cr_special_trade` as a
left join `mithras_pre`.`contract_base_info` as b on a.`contract_id` = b.`id`;

-- 逾期表
select
a.`payment_apply_code` as `编号`, 
b.`contract_code` as `合同编号`,
round(a.`overdue_principal` / 10000.00, 2) as `逾期金额（元）`, 
a.`overdue_day` as `逾期天数（天）`, 
round(a.`overdue_total` / 10000.00, 2) as `逾期总额（元）`, 
a.`overdue_change_date` as `逾期改变日期`
from `mithras_pre_report`.`cr_overdue_record` as a
left join `mithras_pre`.`contract_base_info` as b on a.`contract_id` = b.`id`;

-- 五级分类表
select
a.`payment_apply_code` as `编号`, 
b.`contract_code` as `合同编号`,
case(a.`five_class`) when 1 then '正常' when 2 then '关注' when 3 then '次级' when 4 then '可疑' when 5 then '损失' when 9 then '未分类' end as `五级分类`,
a.`identification_date` as `五级分类认定日期`
from `mithras_pre_report`.`cr_five_class` as a
left join `mithras_pre`.`contract_base_info` as b on a.`contract_id` = b.`id`;

-- 客户表
select
a.`client_code` as `客户编号`, 
a.`client_name` as `客户名称`, 
a.`zhong_zheng_code` as `中征码`,
case a.`continuous_status` when '1' then '正常' when '2' then '注销' when 'X' then '未知' when '9' then '其他' end as `存续状态`, 
case a.`org_type` when '1' then '企业' when 'A' then '个体工商户' when '3' then '机关' when '5' then '事业单位' when '7' then '社会团体' when '9' then '其他组织机构' end as `组织机构类型`, 
a.`register_address` as `注册地址`, 
a.`region_code` as `行政区划`, 
a.`establish_date` as `成立日期`, 
a.`biz_license_end_date` as `营业许可到期日`,
a.`biz_scope` as `业务范围`,
b.`display` as `行业分类`, 
case a.`economy_type` when 100 then '内资' when 200 then '港、澳台投资' when 300 then '国外投资' when 900 then '其他' end as `经济类型`, 
case a.`org_scale` when 'TINY' then '微型' when 'SMALL' then '小型' when 'MIDDLE' then '中型' when 'BIG' then '大型' when 'X' then '未知' end as `企业规模`, 
case a.`register_currency_type` when 'CNY' then '人民币' when 'HKD' then '港币' when 'JPY' then '日元' when 'EUR' then '欧元' when 'GBP' then '英镑' when 'USD' then '美元' end as `注册资本币种`, 
round(a.`register_capital` / 10000.00, 2) as `注册资本（元）`,
a.`corp_represent` as `法人代表`,
case a.`corp_cert_type` when '10' then '居民身份证' when '1' then '户口簿' when '2' then '护照' end as `法人证件类型`, 
a.`corp_cert_code` as `法人证件号码`, 
a.`effect_date` as `数据更新日期`
from `mithras_pre_report`.`cr_client` as a
left join `mithras_pre`.`industry_type` as b on a.`industry_type` = b.`code`;

-- 保证表
select
a.`payment_apply_code` as `编号`, 
b.`contract_code` as `合同编号`, 
a.`guarante_contract_code` as `保证合同编号`, 
a.`guarante_contract_code_2` as `保证合同编号（简）`, 
a.`client_name` as `客户名称`, 
case a.`client_type` when '1' then '自然人' when '2' then '组织机构' end as `客户分类`, 
case a.`guarantor_id_type` when '20' then '统一社会信用代码' when '10' then '居民身份证' end as `身份标识类型`, 
a.`guarantor_id` as `身份标识号码`, 
case a.`client_class` when '1' then '共同借款人' when '2' then '保证人' end as `客户类型`,
round(a.`repay_liability_amount` / 10000.00, 2) as `还款责任金额（元）`, 
case a.`joint_guarantor_flag` when '0' then '非联保' when '1' then '联保' end as `联保标识`
from `mithras_pre_report`.`cr_guarantor` as a
left join `mithras_pre`.`contract_base_info` as b on a.`contract_id` = b.`id`;

-- 抵押表
select
a.`payment_apply_code` as `编号`, 
b.`contract_code` as `合同编号`,
round(a.`apply_payment_amount` / 10000.00, 2) as `借据本金（元）`,
a.`mortgage_contract_code` as `抵押合同编号`, 
a.`mortgage_contract_code_2` as `抵押合同编号（简）`, 
case a.`mortgage_type` when '1' then '自然人' when '2' then '组织机构' end as `抵押人类型`,
a.`mortgage_name` as `抵押人名称`, 
case a.`mortgage_id_type` when '20' then '统一社会信用代码' when '10' then '居民身份证' end as `抵押人身份标识类型`, 
a.`mortgage_id` as `抵押人身份标识号码`, 
case a.`max_flag` when '0' then '否' when '1' then '是' end as `最高额担保标识`,
case a.`appraisal_company_type` when '1' then '自评估' when '2' then '第三方评估机构' end as `评估机构类型`, 
a.`assessed_date` as `评估日期`, 
a.`mortgage_describe` as `抵押物描述`,
a.`sequence` as `序号`,
case a.`type` when '99' then '其他' when '11' then '房产' when '12' then '土地使用权(包含土地附着物)' when '13' then '交通运输设备' when '14' then '机器设备' end as `抵押物种类`, 
case a.`model_type` when "1" then '房产权证号' when '2' then '土地使用权证号' when '3' then '建设用地规划许可证号' when '4' then '交通工具识别号' when '5' then '交通工具运营执照号' when '6' then '机器设备规划型号' end as `抵押物识别号类型`,
a.`model` as `抵押物唯一识别号`, 
round(a.`assessed_value` / 10000.00, 2) as `评估价值`
from `mithras_pre_report`.`cr_mortgage` as a
left join `mithras_pre`.`contract_base_info` as b on a.`contract_id` = b.`id`;

-- 质押表
select
a.`payment_apply_code` as `编号`, 
b.`contract_code` as `合同编号`,
round(a.`apply_payment_amount` / 10000.00, 2) as `借据本金（元）`,
a.`pledge_contract_code` as `质押合同编号`, 
a.`pledge_contract_code_2` as `质押合同编号（简）`, 
case a.`pledge_type` when '1' then '自然人' when '2' then '组织机构' end as `出质人类型`,
a.`pledge_name` as `出质人名称`, 
case a.`pledge_id_type` when '20' then '统一社会信用代码' when '10' then '居民身份证' end as `出质人身份标识类型`, 
a.`pledge_id` as `出质人身份标识号码`, 
case a.`max_flag` when '0' then '否' when '1' then '是' end as `最高额担保标识`,
a.`sequence` as `序号`,
case a.`type` when '90' then '其他' when '11' then '现金及其等价物' when '12' then '票据' when '13' then '保单' when '14' then '债券' when '15' then '股权' when '21' then '基金' when '22' then '贵金属' when '23' then '应收账款' when '24' then '流动资产' when '25' then '无形资产' when '26' then '涉农质物' end as `质押物种类`, 
round(a.`assessed_value` / 10000.00, 2) as `质物价值`
from `mithras_pre_report`.`cr_pledge` as a
left join `mithras_pre`.`contract_base_info` as b on a.`contract_id` = b.`id`;