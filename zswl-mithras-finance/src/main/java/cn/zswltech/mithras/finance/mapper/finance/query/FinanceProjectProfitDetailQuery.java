package cn.zswltech.mithras.finance.mapper.finance.query;

import cn.zswltech.mithras.foundation.persistence.query.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author dingqi
 * @date 2023/6/17
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class FinanceProjectProfitDetailQuery extends PageQuery {
    private Long projectProfitId;
    private Long deptId;
    private String contractCode;
    private Long sponsorUserId;
}
