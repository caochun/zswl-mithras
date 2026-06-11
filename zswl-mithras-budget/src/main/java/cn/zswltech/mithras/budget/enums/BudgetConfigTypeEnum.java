package cn.zswltech.mithras.budget.enums;

import cn.zswltech.mithras.dto.budget.BudgetParameterConfigListRSP;
import cn.zswltech.mithras.foundation.metadata.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

/**
 * @author dingqi
 * @date 2025/4/13
 * @description
 */
@AllArgsConstructor
@Getter
public enum BudgetConfigTypeEnum implements PullDown {
    FTP_PRICE("FTP定价", BudgetParameterConfigListRSP.BudgetParameterFtpBO.class),
    RISK_RATIO("风险准备金计提比例", BudgetParameterConfigListRSP.BudgetParameterRiskBO.class),
    EXPENSE_RATIO("费用比例", BudgetParameterConfigListRSP.BudgetParameterExpenseRatioBO.class),
    ;

    private final String display;
    private final Class<?> tClass;

    @Override
    public String display() {
        return this.display;
    }

    public static BudgetConfigTypeEnum findByName(String name) {
        for (BudgetConfigTypeEnum item : values()) {
            if (Objects.equals(item.name(), name)) {
                return item;
            }
        }
        return null;
    }
}
