package cn.zswltech.mithras.budget.bo.ecl;

import lombok.Data;

import java.util.List;

@Data
public class BudgetEclLossLgdBO {

    private List<BudgetEclLossLgdBO.BudgetEclLossLgdData> data;
    private BudgetEclLossLgdBO.BudgetEclLossLgdEnum enums;

    @Data
    public static class BudgetEclLossLgdData {
        private String leaseType;
        private String lgd;
    }

    @Data
    public static class BudgetEclLossLgdEnum {
        private List<String> leaseTypeEnum;
    }
}
