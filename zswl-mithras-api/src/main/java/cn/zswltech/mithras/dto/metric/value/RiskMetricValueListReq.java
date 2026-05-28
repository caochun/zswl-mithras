package cn.zswltech.mithras.dto.metric.value;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;

/**
 * @author yibin
 */
@Data
public class RiskMetricValueListReq extends PageReq {

    @ApiModelProperty("指标名称")
    private String metricName;

    @ApiModelProperty("指标编号")
    private String metricCode;

    @ApiModelProperty("是否需要报送")
    private Boolean needReport;

    @ApiModelProperty(value = "数据日期", required = false)
    private LocalDate dataTime;
}
