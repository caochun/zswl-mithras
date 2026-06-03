package cn.zswltech.mithras.kpi.bo;

import lombok.Data;

import java.util.List;

/**
 * @Description 违约损失率LGD
 * @Author jackerhe
 * @Date 2025/9/24 15:24
 * @Version 1.0
 **/
@Data
public class EclLossLgdBO {

    private List<EclLossLgdBO.EclLossLgdData> data;
    private EclLossLgdBO.EclLossLgdEnum enums;

    @Data
    public static class EclLossLgdData{
        private String leaseType;
        private String lgd;

    }

    @Data
    public static class EclLossLgdEnum{

        //穆迪评级
        private List<String> leaseTypeEnum;
    }


}
