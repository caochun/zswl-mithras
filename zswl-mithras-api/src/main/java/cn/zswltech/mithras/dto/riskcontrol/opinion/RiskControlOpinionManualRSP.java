package cn.zswltech.mithras.dto.riskcontrol.opinion;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("人工录入舆情-确定按钮-返回体")
public class RiskControlOpinionManualRSP {

    @ApiModelProperty("舆情id")
    private String id;

    @ApiModelProperty("客户id")
    private Long clientId;

    @ApiModelProperty("客户名称")
    private String clientName;

    @ApiModelProperty("统一社会信用代码")
    private String creditCode;
}
