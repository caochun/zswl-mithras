package cn.zswltech.mithras.dto.metric.factor;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author yibin
 */
@Data
@ApiModel("风险指标因子-文件删除-参数")
public class RiskMetricFactorFileRemoveReq {
    @NotNull
    @ApiModelProperty("记录id")
    private Long id;
}
