package cn.zswltech.mithras.fund.domain.enums.receiptrepay;

import cn.zswltech.mithras.service.config.enumscan.PullDown;
import cn.zswltech.mithras.fund.application.dto.FundPlanFlowResultDTO;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/2/20 17:18
 */
@Getter
public enum ExpenseType implements PullDown {
    /**
     * 保理手续费
     */
//    FACTORING_FEE("保理手续费"),
    /**
     * 开证许可证费
     */
    OPEN_LICENSE_FEE("开户许可证费"),
    /**
     * 其他费用
     */
    OTHER_FEE("其他费用"),

    CUSTODY_FEE("托管费"),
    GUARANTEE_FEE("担保费"),
    FINANCIAL_ADVISORY_FEE("财务顾问费"),
    CREDIT_ASSESSMENT_FEE("信用评估费"),
    LOAN_SERVICE_FEE("贷款服务费"),
    AUDIT_FEE("审计费"),

    COMMISSION_FEE("手续费"),
    ;

    private final String display;

    ExpenseType(String display) {
        this.display = display;
    }

    public static ExpenseType of(String name) {
        return ExpenseType.valueOf(name);
    }

    public static ExpenseType find(String name) {
        for (ExpenseType item : values()) {
            if (Objects.equals(item.name(), name)) {
                return item;
            }
        }
        return null;
    }

    public static List<ExpenseType> indirectExpenseType() {
        return Arrays.asList(ExpenseType.COMMISSION_FEE,
                ExpenseType.OPEN_LICENSE_FEE,
                ExpenseType.OTHER_FEE);
    }

    public static List<ExpenseType> directExpenseType() {
        return Arrays.asList(ExpenseType.COMMISSION_FEE,
                ExpenseType.OPEN_LICENSE_FEE,
                ExpenseType.CUSTODY_FEE,
                ExpenseType.LOAN_SERVICE_FEE,
                ExpenseType.FINANCIAL_ADVISORY_FEE,
                ExpenseType.CREDIT_ASSESSMENT_FEE,
                ExpenseType.AUDIT_FEE,
                ExpenseType.OTHER_FEE);
    }

    @Override
    public String display() {
        return this.display;
    }
}
