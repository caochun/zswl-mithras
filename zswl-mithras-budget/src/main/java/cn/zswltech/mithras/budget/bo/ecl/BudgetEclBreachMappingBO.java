package cn.zswltech.mithras.budget.bo.ecl;

import lombok.Data;

import java.util.List;

@Data
public class BudgetEclBreachMappingBO {

    private List<BudgetEclBreachMappingBO.BreachMappingData> data;
    private BudgetEclBreachMappingBO.BreachMappingEnum enums;

    @Data
    public static class BreachMappingData {
        private String outerLevel;
        private String outerPd;
    }

    @Data
    public static class BreachMappingEnum {
        private List<String> outerLevelEnum;
    }
}
