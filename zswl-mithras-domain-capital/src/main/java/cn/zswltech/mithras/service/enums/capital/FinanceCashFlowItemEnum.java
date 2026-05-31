package cn.zswltech.mithras.service.enums.capital;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * 资金端所以现金流项目枚举
 * 目前收集至
 * {@link cn.zswltech.mithras.service.mapper.dto.FundPlanFlowResultDTO.CashFlowItem}
 * {@link cn.zswltech.mithras.service.enums.fund.receiptrepay.ExpenseType}
 *
 */
@Getter
@AllArgsConstructor
public enum FinanceCashFlowItemEnum {
    /**
     * 保理手续费
     */
//    FACTORING_FEE("保理手续费", "expense"),
    /**
     * 开证许可证费
     */
    OPEN_LICENSE_FEE("开户许可证费", "expense"),
    /**
     * 其他费用
     */
    OTHER_FEE("其他费用", "expense"),

    CUSTODY_FEE("托管费", "expense"),
    GUARANTEE_FEE("担保费", "expense"),
    FINANCIAL_ADVISORY_FEE("财务顾问费", "expense"),
    CREDIT_ASSESSMENT_FEE("信用评估费", "expense"),
    LOAN_SERVICE_FEE("贷款服务费", "expense"),
    AUDIT_FEE("审计费", "expense"),
    COMMISSION_FEE("手续费", "expense"),
    FINANCE_FUND("融资款", "borrow"),
    DEPOSIT_RETURN("保证金退款", "deposit"),
    DEPOSIT_PAYMENT("保证金付款", "deposit"),
    REPAY("还本付息", "repay");

    final String display;
    final String group;

    public static FinanceCashFlowItemEnum of(String name) {
        for (FinanceCashFlowItemEnum value : FinanceCashFlowItemEnum.values()) {
            if (value.name().equals(name)) {
                return value;
            }
        }
        return null;
    }

    //获取收款类型
    public static List<FinanceCashFlowItemEnum> getCollection() {
        return Arrays.asList(FINANCE_FUND, DEPOSIT_RETURN);
    }

    //获取收款类型
    public static List<FinanceCashFlowItemEnum> getPayment() {
        List<FinanceCashFlowItemEnum> list = new ArrayList<>();
        List<FinanceCashFlowItemEnum> collection = FinanceCashFlowItemEnum.getCollection();
        for (FinanceCashFlowItemEnum value : FinanceCashFlowItemEnum.values()) {
            if (!collection.contains(value)) {
                list.add(value);
            }
        }
        return list;
    }

    public static FinanceCollectionWriteOffOrderEnum transformCollection(String name) {
        if (Objects.equals(FINANCE_FUND.name(), name)) {
            return FinanceCollectionWriteOffOrderEnum.FINANCE_FUND;
        }
        if (Objects.equals(DEPOSIT_RETURN.name(), name)) {
            return FinanceCollectionWriteOffOrderEnum.DEPOSIT_REFUND;
        }
        return null;
    }

    public static FinancePaymentWriteOffOrderEnum transformPayment(String name) {
//        if (Objects.equals(FACTORING_FEE.name(), name)) {
//            return FinancePaymentWriteOffOrderEnum.FACTORING_FEE;
//        }
        if (Objects.equals(OPEN_LICENSE_FEE.name(), name)) {
            return FinancePaymentWriteOffOrderEnum.OPEN_LICENSE_FEE;
        }
        if (Objects.equals(OTHER_FEE.name(), name)) {
            return FinancePaymentWriteOffOrderEnum.OTHER_FEE;
        }
        if (Objects.equals(CUSTODY_FEE.name(), name)) {
            return FinancePaymentWriteOffOrderEnum.CUSTODY_FEE;
        }
        if (Objects.equals(GUARANTEE_FEE.name(), name)) {
            return FinancePaymentWriteOffOrderEnum.GUARANTEE_FEE;
        }
        if (Objects.equals(FINANCIAL_ADVISORY_FEE.name(), name)) {
            return FinancePaymentWriteOffOrderEnum.FINANCIAL_ADVISORY_FEE;
        }
        if (Objects.equals(CREDIT_ASSESSMENT_FEE.name(), name)) {
            return FinancePaymentWriteOffOrderEnum.CREDIT_ASSESSMENT_FEE;
        }
        if (Objects.equals(LOAN_SERVICE_FEE.name(), name)) {
            return FinancePaymentWriteOffOrderEnum.LOAN_SERVICE_FEE;
        }
        if (Objects.equals(AUDIT_FEE.name(), name)) {
            return FinancePaymentWriteOffOrderEnum.AUDIT_FEE;
        }
        if (Objects.equals(DEPOSIT_PAYMENT.name(), name)) {
            return FinancePaymentWriteOffOrderEnum.DEPOSIT_PAY;
        }
        if (Objects.equals(COMMISSION_FEE.name(), name)) {
            return FinancePaymentWriteOffOrderEnum.COMMISSION_FEE;
        }
        return null;
    }

    public static Collection<String> cashFlowItemBorrow() {
        return findByGroup("borrow");
    }

    public static Collection<String> cashFlowItemRepay() {
        return findByGroup("repay");
    }

    public static Collection<String> cashFlowItemDeposit() {
        return findByGroup("deposit");
    }

    public static Collection<String> cashFlowItemExpense() {
        return findByGroup("expense");
    }

    private static Collection<String> findByGroup(String group) {
        List<String> result = new LinkedList<>();
        for (FinanceCashFlowItemEnum item : values()) {
            if (Objects.equals(item.group, group)) {
                result.add(item.name());
            }
        }
        return result;
    }
}
