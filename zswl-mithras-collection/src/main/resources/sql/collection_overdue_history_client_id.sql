alter table collection_overdue_history
    add client_id bigint(20) null comment '冗余客户id';

UPDATE collection_overdue_history c
SET c.client_id = (SELECT b.client_id FROM collection_base_info b WHERE b.id = c.collection_id);
