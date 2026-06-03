package cn.zswltech.mithras.fund.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.time.LocalDate;
import java.util.Objects;

/**
 * @author dingqi
 * @date 2024/5/31
 * @description
 */
@Data
public class FundPlanFlowResultDTO {
    private Integer isDirect;
    private String identifier;
    private Long financingAmount;
    private Long receiptRepayBaseId;
    private String writeOffState;
    private String cashFlowItem;
    private String cashFlowCode;
    private Long planCashFlowAmount;
    private Long planPrincipalAmount;
    private Long planInterestAmount;
    private Integer phase;
    private LocalDate planCashFlowDate;
    private Long actualCashFlowAmount;
    private String financingCode;
    private String fundChannel;
    private String bizType;
    private Long financingId;

    @Getter
    @AllArgsConstructor
    public enum CashFlowItem {
        FINANCE_FUND("融资款"),
        DEPOSIT_RETURN("保证金退款"),
        DEPOSIT_PAYMENT("保证金付款"),
        REPAY("还本付息");

        private final String display;

        public static CashFlowItem find(String name) {
            for (CashFlowItem item : values()) {
                if (Objects.equals(item.name(), name)) {
                    return item;
                }
            }
            return null;
        }
    }
}
