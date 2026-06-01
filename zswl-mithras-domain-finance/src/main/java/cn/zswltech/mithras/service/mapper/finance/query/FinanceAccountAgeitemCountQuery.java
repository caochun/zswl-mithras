package cn.zswltech.mithras.service.mapper.finance.query;

import cn.zswltech.mithras.service.mapper.query.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class FinanceAccountAgeitemCountQuery extends PageQuery {
    private Long accountAgeId;
}
