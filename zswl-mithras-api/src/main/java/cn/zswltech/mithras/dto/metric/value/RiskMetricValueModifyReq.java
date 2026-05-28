package cn.zswltech.mithras.dto.metric.value;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author yibin
 */
@Data
public class RiskMetricValueModifyReq {

    @NotNull
    private Long id;

    @NotNull
    @ApiModelProperty("风险指标值")
    private Long metricValueAdjusted;
}
