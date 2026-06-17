package cn.zswltech.mithras.budget.bo.ecl;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class BudgetEclScenarioWeightBO {

    private List<BudgetEclScenarioWeightBO.BudgetEclScenarioWeightData> data;
    private BudgetEclScenarioWeightBO.BudgetEclScenarioWeightEnum enums;

    @Data
    public static class BudgetEclScenarioWeightData {
        private String scene;
        private BigDecimal sceneWeight;
    }

    @Data
    public static class BudgetEclScenarioWeightEnum {
        private List<String> sceneEnum;
    }
}
