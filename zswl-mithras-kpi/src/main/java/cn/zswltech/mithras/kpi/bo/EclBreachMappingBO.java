package cn.zswltech.mithras.kpi.bo;

import lombok.Data;

import java.util.List;

/**
 * @ClassName EclRatingMappingVO
 * @Description 穆迪评级和违约概率映射
 * @Author jackerhe
 * @Date 2025/9/24 15:24
 * @Version 1.0
 **/
@Data
public class EclBreachMappingBO {

    private List<EclBreachMappingBO.BreachMappingData> data;
    private EclBreachMappingBO.BreachMappingEnum enums;

    @Data
    public static class BreachMappingData{
        //国内评级
        private String outerLevel;
        //穆迪评级
        private String outerPd;
    }

    @Data
    public static class BreachMappingEnum{

        //穆迪评级
        private List<String> outerLevelEnum;
    }



}
