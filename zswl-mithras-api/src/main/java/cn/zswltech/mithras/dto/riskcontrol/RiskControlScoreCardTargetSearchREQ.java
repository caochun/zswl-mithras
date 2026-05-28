package cn.zswltech.mithras.dto.riskcontrol;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @description risk_control_score_cade_base_info
 * @author vico
 * @date 2023-02-27
 */
@Data
@ApiModel("查询指标名称-列表-请求体")
public class RiskControlScoreCardTargetSearchREQ {

    @ApiModelProperty(value = "年份")
    @NotNull(message = "年份不能为空")
    private Integer year;

    /**
     * 评分卡名称
     */
    @ApiModelProperty(value = "指标名称")
    private String targetName;

}
