package cn.zswltech.mithras.service.service.bo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Description 情景权重
 * @Author jackerhe
 * @Date 2025/9/24 15:24
 * @Version 1.0
 **/
@Data
public class EclScenarioWeightBO {

    private List<EclScenarioWeightBO.EclScenarioWeightData> data;
    private EclScenarioWeightBO.EclScenarioWeightEnum enums;

    @Data
    public static class EclScenarioWeightData{
        private String scene;
        private BigDecimal sceneWeight;

    }

    @Data
    public static class EclScenarioWeightEnum{

        //穆迪评级
        private List<String> sceneEnum;
    }



}
