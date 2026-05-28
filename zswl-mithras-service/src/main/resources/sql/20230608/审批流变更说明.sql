-- auto-generated definition
create table process_modify_remark
(
    id          bigint auto_increment
        primary key,
    module_type varchar(50)                        null,
    remark_type varchar(20)                        null,
    remark_json json                               null,
    main_id     bigint                             null,
    create_time datetime default CURRENT_TIMESTAMP null,
    update_time datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    create_by   bigint                             null,
    update_by   bigint                             null,
    constraint process_modify_remark_uindex
        unique (module_type, main_id, remark_type)
)
    comment '变更类型相关审批流';


-- auto-generated definition
create table process_modify_remark_lib
(
    id               bigint auto_increment
        primary key,
    module_type      varchar(50)                        null,
    remark_type      varchar(20)                        null,
    remark_json      json                               null,
    main_id          bigint                             null,
    create_time      datetime default CURRENT_TIMESTAMP null,
    update_time      datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    create_by        bigint                             null,
    update_by        bigint                             null,
    version          varchar(40)                        not null comment '版本号',
    origin_id        bigint                             not null comment '临时数据表id 需要用来比对数据 或者 流程拒绝时全量回写',
    data_create_time datetime                           null,
    data_create_by   bigint                             null,
    data_update_time datetime                           null,
    data_update_by   bigint                             null,
    version_type     tinyint  default 1                 null comment '版本标志，0无效，1有效...业务自扩展'
)
    comment '变更类型相关审批流版本表';

insert into bifrost_function (code, name, sort_no, menu_id, method, path, type, group_id)
select'processmodifyremarkdetailprojreview','变更流程-详情',0,a.id,'POST','/process/modify/remark/detail',1,b.id from bifrost_menu a, gruul_function_group b where a.name='项目评审' and b.group_describe='项目评审-查看' ;
insert into bifrost_function (code, name, sort_no, menu_id, method, path, type, group_id)
select'processmodifyremarkallprojreview','变更流程-所有（包括历史）',0,a.id,'POST','/process/modify/remark/all',1,b.id from bifrost_menu a, gruul_function_group b where a.name='项目评审' and b.group_describe='项目评审-查看' ;
insert into bifrost_function (code, name, sort_no, menu_id, method, path, type, group_id)
select'processmodifyremarkallcontract','变更流程-所有（包括历史）',0,a.id,'POST','/process/modify/remark/all',1,b.id from bifrost_menu a, gruul_function_group b where a.name='合同管理' and b.group_describe='合同管理-查看' ;
insert into bifrost_function (code, name, sort_no, menu_id, method, path, type, group_id)
select'processmodifyremarkdetailcontract','变更流程-详情',0,a.id,'POST','/process/modify/remark/detail',1,b.id from bifrost_menu a, gruul_function_group b where a.name='合同管理' and b.group_describe='合同管理-查看' ;
insert into bifrost_function (code, name, sort_no, menu_id, method, path, type, group_id)
select'processmodifyremarkallcreditestablish','变更流程-所有（包括历史）',0,a.id,'POST','/process/modify/remark/all',1,b.id from bifrost_menu a, gruul_function_group b where a.name='授信立项' and b.group_describe='授信立项-查看' ;
insert into bifrost_function (code, name, sort_no, menu_id, method, path, type, group_id)
select'processmodifyremarkdetailcreditestablish','变更流程-详情',0,a.id,'POST','/process/modify/remark/detail',1,b.id from bifrost_menu a, gruul_function_group b where a.name='授信立项' and b.group_describe='授信立项-查看' ;
insert into bifrost_function (code, name, sort_no, menu_id, method, path, type, group_id)
select'processmodifyremarkdetailcreditreview','变更流程-详情',0,a.id,'POST','/process/modify/remark/detail',1,b.id from bifrost_menu a, gruul_function_group b where a.name='授信评审' and b.group_describe='授信评审-查看' ;
insert into bifrost_function (code, name, sort_no, menu_id, method, path, type, group_id)
select'processmodifyremarkallcreditreview','变更流程-所有（包括历史）',0,a.id,'POST','/process/modify/remark/all',1,b.id from bifrost_menu a, gruul_function_group b where a.name='授信评审' and b.group_describe='授信评审-查看' ;
insert into bifrost_function (code, name, sort_no, menu_id, method, path, type, group_id)
select'processmodifyremarkaddprojreview','变更流程-新增',0,a.id,'POST','/process/modify/remark/add',2,b.id from bifrost_menu a, gruul_function_group b where a.name='项目评审' and b.group_describe='项目评审-编辑' ;
insert into bifrost_function (code, name, sort_no, menu_id, method, path, type, group_id)
select'processmodifyremarkmodifyprojreview','变更流程-修改',0,a.id,'POST','/process/modify/remark/modify',2,b.id from bifrost_menu a, gruul_function_group b where a.name='项目评审' and b.group_describe='项目评审-编辑' ;
insert into bifrost_function (code, name, sort_no, menu_id, method, path, type, group_id)
select'processmodifyremarkmodifycontract','变更流程-修改',0,a.id,'POST','/process/modify/remark/modify',2,b.id from bifrost_menu a, gruul_function_group b where a.name='合同管理' and b.group_describe='合同管理-编辑' ;
insert into bifrost_function (code, name, sort_no, menu_id, method, path, type, group_id)
select'processmodifyremarkaddcontract','变更流程-新增',0,a.id,'POST','/process/modify/remark/add',2,b.id from bifrost_menu a, gruul_function_group b where a.name='合同管理' and b.group_describe='合同管理-编辑' ;
insert into bifrost_function (code, name, sort_no, menu_id, method, path, type, group_id)
select'processmodifyremarkmodifycreditestablish','变更流程-修改',0,a.id,'POST','/process/modify/remark/modify',2,b.id from bifrost_menu a, gruul_function_group b where a.name='授信立项' and b.group_describe='授信立项-编辑' ;
insert into bifrost_function (code, name, sort_no, menu_id, method, path, type, group_id)
select'processmodifyremarkaddcreditestablish','变更流程-新增',0,a.id,'POST','/process/modify/remark/add',2,b.id from bifrost_menu a, gruul_function_group b where a.name='授信立项' and b.group_describe='授信立项-编辑' ;
insert into bifrost_function (code, name, sort_no, menu_id, method, path, type, group_id)
select'processmodifyremarkaddcreditreview','变更流程-新增',0,a.id,'POST','/process/modify/remark/add',2,b.id from bifrost_menu a, gruul_function_group b where a.name='授信评审' and b.group_describe='授信评审-编辑' ;
insert into bifrost_function (code, name, sort_no, menu_id, method, path, type, group_id)
select'processmodifyremarkmodifycreditreview','变更流程-修改',0,a.id,'POST','/process/modify/remark/modify',2,b.id from bifrost_menu a, gruul_function_group b where a.name='授信评审' and b.group_describe='授信评审-编辑' ;
