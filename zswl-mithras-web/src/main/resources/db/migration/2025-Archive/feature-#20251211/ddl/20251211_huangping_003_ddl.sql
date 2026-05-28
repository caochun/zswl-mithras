ALTER TABLE flow_node_cc_config
ADD COLUMN sort TINYINT(2) NOT NULL DEFAULT 1
    COMMENT '排序值（1-20，数值越小排序越靠前）'
    AFTER `object_ids`;