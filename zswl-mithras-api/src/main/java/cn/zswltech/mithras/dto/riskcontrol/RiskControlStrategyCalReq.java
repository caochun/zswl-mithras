package cn.zswltech.mithras.dto.riskcontrol;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/6/8 10:52
 */
@Data
@ApiModel("风控策略快照重计算请求体")
public class RiskControlStrategyCalReq {

    @ApiModelProperty("数据时点")
    LocalDate date;
}
