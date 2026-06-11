package cn.zswltech.mithras.capital.enums;

import cn.zswltech.mithras.foundation.metadata.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

/**
 * @author dingqi
 * @date 2024/6/6
 * @description
 */
@Getter
@AllArgsConstructor
public enum FinancePaymentWriteOffOrderEnum implements PullDown {
    PRINCIPAL("本金", 10),
    DEPOSIT_PAY("保证金付款", 12),
    FACTORING_FEE("保理手续费", 13),
    OPEN_LICENSE_FEE("开户许可证费", 14),
    OTHER_FEE("其他费用", 15),
    CUSTODY_FEE("托管费", 16),
    GUARANTEE_FEE("担保费", 17),
    FINANCIAL_ADVISORY_FEE("财务顾问费", 18),
    CREDIT_ASSESSMENT_FEE("信用评估费", 19),
    LOAN_SERVICE_FEE("贷款服务费", 20),
    AUDIT_FEE("审计费", 21),
    INTEREST("利息", 9),
    COMMISSION_FEE("手续费", 23),
    ;

    private final String display;
    private final int sort;

    @Override
    public String display() {
        return this.display;
    }

    public static FinanceCashFlowItemEnum transform(String name) {
        if (Objects.equals(PRINCIPAL.name(), name)) {
            return FinanceCashFlowItemEnum.REPAY;
        }
        if (Objects.equals(DEPOSIT_PAY.name(), name)) {
            return FinanceCashFlowItemEnum.DEPOSIT_PAYMENT;
        }
//        if (Objects.equals(FACTORING_FEE.name(), name)) {
//            return FinanceCashFlowItemEnum.FACTORING_FEE;
//        }
        if (Objects.equals(OPEN_LICENSE_FEE.name(), name)) {
            return FinanceCashFlowItemEnum.OPEN_LICENSE_FEE;
        }
        if (Objects.equals(OTHER_FEE.name(), name)) {
            return FinanceCashFlowItemEnum.OTHER_FEE;
        }
        if (Objects.equals(CUSTODY_FEE.name(), name)) {
            return FinanceCashFlowItemEnum.CUSTODY_FEE;
        }
        if (Objects.equals(GUARANTEE_FEE.name(), name)) {
            return FinanceCashFlowItemEnum.GUARANTEE_FEE;
        }
        if (Objects.equals(FINANCIAL_ADVISORY_FEE.name(), name)) {
            return FinanceCashFlowItemEnum.FINANCIAL_ADVISORY_FEE;
        }
        if (Objects.equals(CREDIT_ASSESSMENT_FEE.name(), name)) {
            return FinanceCashFlowItemEnum.CREDIT_ASSESSMENT_FEE;
        }
        if (Objects.equals(LOAN_SERVICE_FEE.name(), name)) {
            return FinanceCashFlowItemEnum.LOAN_SERVICE_FEE;
        }
        if (Objects.equals(AUDIT_FEE.name(), name)) {
            return FinanceCashFlowItemEnum.AUDIT_FEE;
        }
        if (Objects.equals(INTEREST.name(), name)) {
            return FinanceCashFlowItemEnum.REPAY;
        }
        if (Objects.equals(COMMISSION_FEE.name(), name)) {
            return FinanceCashFlowItemEnum.COMMISSION_FEE;
        }
        return null;
    }
}
