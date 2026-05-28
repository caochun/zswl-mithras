package cn.zswltech.mithras.service.service.bo;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * @author dingqi
 * @date 2022/12/13
 * @description
 */
@Data
public class CashFlowIRRBO {
    /**
     * 期利率irr
     */
    private BigDecimal irrPerPhase;
    /**
     * irr
     */
    private BigDecimal irr;
    /**
     * 经过调整后的现金流量表
     */
    private List<CashFlowAdjustBO> cashFlowAdjustList;

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class CashFlowAdjustBO extends CashFlowBO {
        /**
         * 调整后日期
         */
        private LocalDate adjustCashFlowDate;
        /**
         * 调整后期项 = 调整后期项分母 / 调整后期项分子
         */
        private BigDecimal adjustCashFlowPhase;
        /**
         * 调整后期项分母
         */
        private int adjustCashFlowPhaseFM;
        /**
         * 调整后期项分子
         */
        private int adjustCashFlowPhaseFZ;
        /**
         * 调整后现金流
         */
        private BigDecimal adjustCashFlowAmount;
    }
}
