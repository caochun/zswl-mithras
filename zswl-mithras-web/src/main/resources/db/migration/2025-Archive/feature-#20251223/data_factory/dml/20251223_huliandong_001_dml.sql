
-- use data_factory;
-- 调整客户评级主表中模型的名称
update rating_client set model_name='制造业风控场景模型' where model_name='制造业风控场景服务';
update rating_client set model_name='非制造业模型' where model_name='非制造业服务';
update rating_client set model_name='新能源模型' where model_name='新能源模型服务';
update rating_client set model_name='政信主体地级市模型' where model_name='政信主体地级市服务';
update rating_client set model_name='政信主体区县级模型' where model_name='政信主体区县级服务';



-- 存量数据标记初评或者复评    逻辑需要调整   有很多草稿的情况下如何判定初评
-- init 数据初始化
UPDATE rating_client set rating_type = null;  -- 1398
-- 1、给只有一次评级的赋值初评   786
UPDATE rating_client AS rc
    JOIN (
    SELECT client_id
    FROM rating_client
    GROUP BY client_id
    HAVING COUNT(*) = 1
    ) AS t ON rc.client_id = t.client_id
    SET rc.rating_type = '初评'
WHERE rc.rating_type IS NULL;
-- 2、给多次评级的最早赋值初评  288
UPDATE rating_client AS rc
    JOIN (
    SELECT MIN(id) AS id
    FROM (
    SELECT id, client_id, create_time
    FROM rating_client
    WHERE client_id IN (
    SELECT client_id
    FROM rating_client
    WHERE process_status in('UNDER_APPROVAL','UN_SUBMIT','APPROVAL_PASS')
    GROUP BY client_id
    HAVING COUNT(*) > 0
    ) and process_status in('UNDER_APPROVAL','UN_SUBMIT','APPROVAL_PASS')
    order by client_id
    ) AS t
    GROUP BY client_id
    ) AS x ON x.id = rc.id
    SET rc.rating_type = '初评'
WHERE rc.rating_type IS NULL;
-- 3、找到所有的初评和对应的时间   相同id对应的时间晚于初评 则都为复评      301
UPDATE rating_client AS rc
    JOIN (
    select a.id from rating_client a
    left join rating_client b on a.client_id = b.client_id
    where a.rating_type is null and b.rating_type ='初评' and a.create_time > b.create_time
    ) AS x ON x.id = rc.id
    SET rc.rating_type = '复评'
WHERE rc.rating_type IS NULL;
-- 4、 id早于初评  + 兜底 设置都为初评   （防止出现多条，全部都审批不通过的）  23
update rating_client set rating_type='初评' where rating_type is null;




-- 对存量未提交部分的客户评级清理快照并删除测算结果   上线前需要导出，业务估计会线下二次确认
-- select * from rating_client t where process_status ='UN_SUBMIT' and snapshot_id  is not null;
--update rating_client set report_id = null ,snapshot_id = null ,score = null ,model_score = null where process_status ='UN_SUBMIT' and snapshot_id  is not null;
update rating_client set report_id = null ,snapshot_id = null ,score = null ,model_score = null where process_status ='UN_SUBMIT' and snapshot_id  is not null and model_code in ('client_fzzy_service','client_S4_1_m_d_f','client_djs_service');
