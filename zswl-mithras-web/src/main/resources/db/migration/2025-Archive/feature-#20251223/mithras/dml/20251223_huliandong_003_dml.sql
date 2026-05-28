-- 初始化
update fund_direct_financing_pledge_info set lock_contract = null;
update fund_financing_pledge_info set lock_contract = null;

-- 生效 起息
update fund_direct_financing_pledge_info set lock_contract ='1' where is_pledge  = 1 and financing_id  in (select id from Fund_Direct_Financing_Base_Info where financing_status in ('NEW','EFFECT','CARRY_INTEREST'));
update fund_financing_pledge_info set lock_contract ='1' where is_pledge  = 1 and financing_id  in (select id from Fund_Financing_Base_Info where financing_status in ('NEW','EFFECT','CARRY_INTEREST'));

-- 其他
update fund_direct_financing_pledge_info set lock_contract ='0' where lock_contract is null;
update fund_financing_pledge_info set lock_contract ='0' where lock_contract is null;