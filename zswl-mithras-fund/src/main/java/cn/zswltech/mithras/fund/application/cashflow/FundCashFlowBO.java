package cn.zswltech.mithras.fund.application.cashflow;

import cn.zswltech.mithras.fund.directfinancing.persistence.model.FundDirectFinancingRepayActual;
import cn.zswltech.mithras.fund.persistence.model.financing.FundFinancingRepayActual;
import lombok.Data;

import java.time.LocalDate;
import java.util.Optional;

/**
 * @author dingqi
 * @date 2025/6/26
 * @description
 */
@Data
public class FundCashFlowBO {
    private String financingType;
    private Long financingId;
    private LocalDate cashFlowDate;
    private String cashFlowCode;
    private Long principal;
    private Long interest;

    public static FundCashFlowBO copyFromFundFinancingRepayActual(FundFinancingRepayActual fundFinancingRepayActual) {
        FundCashFlowBO fundCashFlowBO = new FundCashFlowBO();
        fundCashFlowBO.setFinancingType("INDIRECT");
        fundCashFlowBO.setFinancingId(fundFinancingRepayActual.getFinancingId());
        fundCashFlowBO.setCashFlowDate(fundFinancingRepayActual.getRepayDate());
        fundCashFlowBO.setCashFlowCode(fundFinancingRepayActual.getCashFlowCode());
        fundCashFlowBO.setPrincipal(Optional.ofNullable(fundFinancingRepayActual.getPrincipleAmount()).orElse(0L));
        fundCashFlowBO.setInterest(Optional.ofNullable(fundFinancingRepayActual.getInterestAmount()).orElse(0L));
        return fundCashFlowBO;
    }

    public static FundCashFlowBO copyFromFundDirectFinancingRepayActual(FundDirectFinancingRepayActual fundDirectFinancingRepayActual) {
        FundCashFlowBO fundCashFlowBO = new FundCashFlowBO();
        fundCashFlowBO.setFinancingType("DIRECT");
        fundCashFlowBO.setFinancingId(fundDirectFinancingRepayActual.getFinancingId());
        fundCashFlowBO.setCashFlowDate(fundDirectFinancingRepayActual.getRepayDate());
        fundCashFlowBO.setCashFlowCode(fundDirectFinancingRepayActual.getCashFlowCode());
        fundCashFlowBO.setPrincipal(Optional.ofNullable(fundDirectFinancingRepayActual.getPrincipleAmount()).orElse(0L));
        fundCashFlowBO.setInterest(Optional.ofNullable(fundDirectFinancingRepayActual.getInterestAmount()).orElse(0L));
        return fundCashFlowBO;
    }
}
