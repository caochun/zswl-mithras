ALTER TABLE corp_commerce_info_lib ADD domestic_or_abroad varchar(30) COMMENT '境内or境外';
ALTER TABLE corp_commerce_info_lib ADD special_org_code varchar(50) COMMENT '境外企业特殊机构代码';
ALTER TABLE corp_commerce_info ADD domestic_or_abroad varchar(30) COMMENT '境内or境外';
ALTER TABLE corp_commerce_info ADD special_org_code varchar(50) COMMENT '境外企业特殊机构代码';
ALTER TABLE client ADD domestic_or_abroad varchar(30) COMMENT '境内or境外';
ALTER TABLE client ADD special_org_code varchar(50) COMMENT '境外企业特殊机构代码';


