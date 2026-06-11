package cn.zswltech.mithras.foundation.persistence.query;

import lombok.Data;

/**
 * @author dingqi
 * @date 2023/6/17
 * @description
 */
@Data
public class PageQuery {
    private int start = 0;
    private int size = 20;
}
