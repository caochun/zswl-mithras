package cn.zswltech.mithras.budget.bo.ecl;

import lombok.Data;

import java.util.List;

@Data
public class BudgetEclInnerBreachMappingBO {

    private List<BudgetEclInnerBreachMappingBO.BreachMappingData> data;
    private BudgetEclInnerBreachMappingBO.BreachMappingEnum enums;

    @Data
    public static class BreachMappingData {
        private String innerLevel;
        private String innerPdUpper;
        private String innerPdLower;
        private String innerPd;
    }

    @Data
    public static class BreachMappingEnum {
        private List<String> outerLevelEnum;
    }
}
