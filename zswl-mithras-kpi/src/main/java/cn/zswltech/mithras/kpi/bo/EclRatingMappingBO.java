package cn.zswltech.mithras.kpi.bo;

import lombok.Data;

import java.util.List;

/**
 * @ClassName EclRatingMappingVO
 * @Description 国内评级与穆迪评级映射
 * @Author jackerhe
 * @Date 2025/9/24 15:24
 * @Version 1.0
 **/
@Data
public class EclRatingMappingBO {
    private List<RatingMappingData> data;
    private RatingMappingEnum enums;

    @Data
    public static class RatingMappingData{
        //国内评级
        private String innerLevel;
        //穆迪评级
        private String outerLevel;
    }

    @Data
    public static class RatingMappingEnum{
        //国内评级
        private List<String> innerLevelEnum;
        //穆迪评级
        private List<String> outerLevelEnum;
    }

}
