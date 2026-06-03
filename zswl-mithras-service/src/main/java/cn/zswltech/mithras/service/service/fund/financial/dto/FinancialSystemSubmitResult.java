package cn.zswltech.mithras.service.service.fund.financial.dto;

import cn.zswltech.mithras.fund.domain.enums.financing.FundFinancingBizTypeEnum;
import cn.zswltech.mithras.fund.domain.enums.financing.LprArrangeModeEnum;
import cn.zswltech.mithras.service.enums.projestablish.RateType;
import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.experimental.Accessors;


/**
 * @author zswl
 */
@Data
@ApiModel("贷款合同推送-返回体")
public class FinancialSystemSubmitResult {

    private CwgsHead cwgsHead;

    private CswgApiAppUser cwgsApiAppUser;

    @Data
    public static class CwgsHead{
        @JSONField(name = "trandate")
        private String tranDate;

        @JSONField(name = "trantime")
        private String tranTime;

        @JSONField(name = "returnMsg")
        private String returnMsg;

        @JSONField(name = "serviceNo")
        private String serviceNo;

        @JSONField(name = "consumerId")
        private String consumerId;

        @JSONField(name = "returnCode")
        private String returnCode;

        @JSONField(name = "channelType")
        private String channelType;

        @JSONField(name = "reqSequence")
        private String reqSequence;

        @JSONField(name = "serviceCode")
        private String serviceCode;

        @JSONField(name = "consumerCode")
        private String consumerCode;
    }

    @Data
    public static class CswgApiAppUser{
        @JSONField(name = "organ")
        private String organ;

        @JSONField(name = "operator")
        private String operator;
    }

    @Data
    public static class Body{

    }



}
