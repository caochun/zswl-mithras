package cn.zswltech.mithras.dto.riskcontrol;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/2/8 16:16
 */
@ApiModel("预警监控指标详情-请求体")
@Data
public class RiskControlStrategyDetailReq {

    @ApiModelProperty(value = "主键")
    @NotNull
    private Long id;

    @ApiModelProperty(value = "指标快照时间")
    private LocalDate date;
}
