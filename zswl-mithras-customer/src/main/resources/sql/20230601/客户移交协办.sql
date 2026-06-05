alter table client_transfer
    add to_cosponsor_ids json null comment '协办id列表' after to_sponsor_id;

