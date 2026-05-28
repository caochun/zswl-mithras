package cn.zswltech.mithras.dto.metric.value;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * @author yibin
 */
@Data
@ApiModel("风险指标-值-报送")
public class RiskMetricValueReportReq {
    @NotNull
    @ApiModelProperty(value = "数据时点", required = true)
    private LocalDate dataTime;
}
