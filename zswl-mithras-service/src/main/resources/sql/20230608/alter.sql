alter table client
    add province_of_affiliation varchar(20) null comment '隶属省份';

alter table client
    add artificial_province bit(1) default b'0' comment '隶属省份是否人工修改值';
