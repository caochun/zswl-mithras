package cn.zswltech.mithras.budget.enums;

import cn.zswltech.mithras.budget.bo.ecl.BudgetEclBreachMappingBO;
import cn.zswltech.mithras.budget.bo.ecl.BudgetEclForwardZBO;
import cn.zswltech.mithras.budget.bo.ecl.BudgetEclInnerBreachMappingBO;
import cn.zswltech.mithras.budget.bo.ecl.BudgetEclLossLgdBO;
import cn.zswltech.mithras.budget.bo.ecl.BudgetEclRatingMappingBO;
import cn.zswltech.mithras.budget.bo.ecl.BudgetEclScenarioWeightBO;
import cn.zswltech.mithras.foundation.metadata.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BudgetEclConfigEnum implements PullDown {
    RATING_MAPPING("国内评级与穆迪评级映射", BudgetEclRatingMappingBO.class),
    BREACH_MAPPING("穆迪评级和违约概率映射", BudgetEclBreachMappingBO.class),
    FORWARD_Z("前瞻调整因子Z", BudgetEclForwardZBO.class),
    LOSS_LGD("违约损失率LGD", BudgetEclLossLgdBO.class),
    SCENARIO_WEIGHT("情景权重", BudgetEclScenarioWeightBO.class),
    INNER_BREACH_MAPPING("内部评级和违约概率映射", BudgetEclInnerBreachMappingBO.class),
    ;

    private final String display;

    private final Class<?> relationClass;

    public static BudgetEclConfigEnum ofName(String name) {
        for (BudgetEclConfigEnum item : BudgetEclConfigEnum.values()) {
            if (item.name().equals(name)) {
                return item;
            }
        }
        return null;
    }

    @Override
    public String display() {
        return this.display;
    }
}
