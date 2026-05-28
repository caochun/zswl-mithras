package cn.zswltech.mithras.dto.riskcontrol;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description risk_control_score_cade_base_info
 * @author vico
 * @date 2023-02-27
 */
@Data
@ApiModel("查询指标名称-列表-返回体")
public class RiskControlScoreCardTargetSearchRSP {

    /**
     * 方案id
     */
    @ApiModelProperty("指标id")
    private Long id;

    @ApiModelProperty("指标名称")
    private String name;

}
