INSERT INTO `bifrost_function` (`code`, `name`, `sort_no`, `menu_id`, `method`, `path`, `type`)
select 'paymentWriteOffSelectorgs', '付款核销-业务部门列表', 0, id, 'GET', '/select/orgs', 1
from bifrost_menu where `code` = 'QX0114';