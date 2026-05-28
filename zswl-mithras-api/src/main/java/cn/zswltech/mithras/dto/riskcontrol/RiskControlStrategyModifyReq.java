package cn.zswltech.mithras.dto.riskcontrol;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zhaozhengkang
 * @description 预警监控管理
 * @date 2023-02-08
 */
@Data
@ApiModel("预警监控指标编辑-请求体")
public class RiskControlStrategyModifyReq {
    @ApiModelProperty(value = "主键")
    private Long id;
    @ApiModelProperty(value = "预警值1")
    private String earlyWarningValueOne;
    @ApiModelProperty(value = "预警值2")
    private String earlyWarningValueTwo;
    @ApiModelProperty(value = "限定值1")
    private Long limitValueOne;
    @ApiModelProperty(value = "限定值2")
    private Long limitValueTwo;
    @ApiModelProperty(value = "预警状态（1启用，0禁用）")
    private Integer earlyWarningState;

}
