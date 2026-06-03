package cn.zswltech.mithras.kpi.bo;

import lombok.Data;

import java.util.List;

/**
 * @ClassName EclRatingMappingVO
 * @Description 内部评级和违约概率映射
 * @Author jackerhe
 * @Date 2025/9/24 15:24
 * @Version 1.0
 **/
@Data
public class EclInnerBreachMappingBO {

    private List<EclInnerBreachMappingBO.BreachMappingData> data;
    private EclInnerBreachMappingBO.BreachMappingEnum enums;

    @Data
    public static class BreachMappingData{
        //国内评级
        private String innerLevel;
        private String innerPdUpper;
        private String innerPdLower;
        //穆迪评级
        private String innerPd;
    }

    @Data
    public static class BreachMappingEnum{

        //穆迪评级
        private List<String> outerLevelEnum;
    }



}
