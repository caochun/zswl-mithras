package cn.zswltech.mithras.customer.application.monitor.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Map;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2024/12/23 16:04
 */
@ApiModel
@Data
public class ClientMonitorRiskLineChartRsp {

    @ApiModelProperty(value = "预警数量折线图数据")
    private Map<String, Integer> warnLineData;

    @ApiModelProperty(value = "舆情数量折线图数据")
    private Map<String, Integer> opLineData;

}
