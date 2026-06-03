package cn.zswltech.mithras.budget.domain.bo;

import lombok.Data;

import java.time.LocalDate;
import java.util.Map;

/**
 *
 * @author: jackerhe
 * ecl模型计算本月风险金结果
 **/
@Data
public class BudgetEclRiskReserveBO {
    //计算月份
    private LocalDate calculationDate;

    //非逾期状态下风险金计算值
    Map<Long, Long> receiptId2RiskReserve;

    //逾期状态下风险金计算值
    Map<Long, Long> receiptId2RiskReserveOverdue;
}
