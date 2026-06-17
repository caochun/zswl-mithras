package cn.zswltech.mithras.budget.bo.ecl;

import lombok.Data;

import java.util.List;

@Data
public class BudgetEclRatingMappingBO {

    private List<RatingMappingData> data;
    private RatingMappingEnum enums;

    @Data
    public static class RatingMappingData {
        private String innerLevel;
        private String outerLevel;
    }

    @Data
    public static class RatingMappingEnum {
        private List<String> innerLevelEnum;
        private List<String> outerLevelEnum;
    }
}
