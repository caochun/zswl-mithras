package cn.zswltech.mithras.service.providence.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2024/12/23 16:04
 */
@ApiModel
@Data
public class ClientMonitorPieChartRsp {

    @ApiModelProperty("预警饼图数据")
    private Map<String, BigDecimal> warnPieData;

    @ApiModelProperty("舆情饼图数据")
    private Map<String, BigDecimal> opPieData;
}
