package cn.zswltech.mithras.projectprocess.service.bo;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author dingqi
 * @date 2024/4/8
 * @description 日折现率测算结果
 */
@Data
public class DailyDiscountRateCalcResultBO {
    private BigDecimal dailyDiscountRate;
    private List<CashFlowAdjustBO> cashFlowAdjustList;

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class CashFlowAdjustBO extends CashFlowBO {
        /**
         * 按天调整期项
         */
        private Integer adjustCashFlowPhase;
        /**
         * 调整后现金流
         */
        private BigDecimal adjustCashFlowAmount;
        /**
         * 该期天数
         */
        private Integer currentPhaseDays;
    }
}
