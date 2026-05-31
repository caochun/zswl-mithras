package cn.zswltech.mithras.service.service.bo;

import lombok.Data;

import java.util.List;

/**
 * @Description 前瞻调整因子Z
 * @Author jackerhe
 * @Date 2025/9/24 15:24
 * @Version 1.0
 **/
@Data
public class EclForwardZBO {

    private List<EclForwardZBO.EclForwardZBOData> data;
    private EclForwardZBO.EclForwardZBOEnum enums;

    @Data
    public static class EclForwardZBOData{
        private String factorBaseZ;
        private String factorGloZ;
        private String factorOptZ;
        private String group;
    }

    @Data
    public static class EclForwardZBOEnum{

        //穆迪评级
        private List<String> leaseTypeEnum;
    }




}
