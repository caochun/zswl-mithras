update contract_base_info c set c.lease_item_types = (select lease_item_info.lease_item_types from lease_item_info where id = c.lease_item_info_id)
where c.lease_item_types is null ;

update contract_base_info set lease_item_types = '["VESSEL"]' where id in (1074, 1075, 1334, 1423);
update contract_base_info set lease_item_types = '["PRODUCTION_EQUIPMENT"]' where id in (1027, 1115, 1157, 1162, 1164, 1297, 1382, 1446, 1472, 1473, 1605, 1708, 1709,
                                                                                         1798, 1814, 1815);
