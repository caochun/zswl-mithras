package cn.zswltech.mithras.budget.bo.ecl;

import lombok.Data;

import java.util.List;

@Data
public class BudgetEclForwardZBO {

    private List<BudgetEclForwardZBO.BudgetEclForwardZData> data;
    private BudgetEclForwardZBO.BudgetEclForwardZEnum enums;

    @Data
    public static class BudgetEclForwardZData {
        private String factorBaseZ;
        private String factorGloZ;
        private String factorOptZ;
        private String group;
    }

    @Data
    public static class BudgetEclForwardZEnum {
        private List<String> leaseTypeEnum;
    }
}
