package cn.zswltech.mithras.dto.assetclassify;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/8/23 16:10
 */
@Data
@ApiModel("拨备计提信息响应体")
public class AssetClassifyClientWithdrawalRatioListRsp {

    @ApiModelProperty("借据id")
    private Long receiptId;
    @ApiModelProperty("借据编号")
    private String receiptCode;
    @ApiModelProperty("合同id")
    private Long contractId;
    @ApiModelProperty("合同编号")
    private String contractCode;
    @ApiModelProperty("业务类型")
    private String bizType;
    @ApiModelProperty("剩余租期")
    private Integer remainingPhase;
    @ApiModelProperty("投放金额")
    private Long deliveryAmount;
    @ApiModelProperty("存量风险敞口")
    private Long stockExposure;
    @ApiModelProperty("计提比例")
    private Integer withdrawalRatio;
}
