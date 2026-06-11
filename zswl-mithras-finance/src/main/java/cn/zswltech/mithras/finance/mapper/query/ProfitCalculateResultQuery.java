package cn.zswltech.mithras.finance.mapper.query;

import cn.zswltech.mithras.foundation.persistence.query.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * @author dingqi
 * @date 2023/6/25
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ProfitCalculateResultQuery extends PageQuery {
    private LocalDate calculateDate;
    private String contractCode;
}
