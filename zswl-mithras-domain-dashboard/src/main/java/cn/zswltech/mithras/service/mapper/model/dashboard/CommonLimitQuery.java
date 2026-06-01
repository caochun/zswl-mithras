package cn.zswltech.mithras.service.mapper.model.dashboard;

import lombok.Data;

/**
 * @author dingqi
 * @date 2024/6/16
 * @description
 */
@Data
public class CommonLimitQuery {
    private Integer start = 0;
    private Integer limit = 20;

    public void fillLimitQuery(int page, int pageSize) {
        start = (page - 1) * pageSize;
        limit = pageSize;
    }
}
