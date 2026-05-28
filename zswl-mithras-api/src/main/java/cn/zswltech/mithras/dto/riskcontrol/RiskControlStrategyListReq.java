package cn.zswltech.mithras.dto.riskcontrol;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;

/**
 * @author zhaozhengkang
 * @description 风控管理-预警监控管理
 * @date 2023-02-08
 */
@Data
@ApiModel("预警监控指标列表-请求体")
public class RiskControlStrategyListReq extends PageReq {
    @ApiModelProperty("指标类型(LIQUIDITY_RISK-流动性风险指标限额,CREDIT_RISK-信用风险指标限额," +
            "MARKET_RISK-市场风险指标限额,BUSINESS_RISK-业务风险指标限额,REGIONAL_RISK-区域风险限额指标)")
    private String metricType;
    @ApiModelProperty("指标类别(CONTROL-控制类,GUIDANCE-指导类)")
    private String metricCategory;
    @ApiModelProperty("指标名称")
    private String metricName;

    @ApiModelProperty("预警状态（1启用，0禁用）")
    private Integer earlyWarningState;
    @ApiModelProperty("指标更新时间from")
    private LocalDate updateTimeFrom;
    @ApiModelProperty("指标更新时间to")
    private LocalDate updateTimeTo;
    @ApiModelProperty("指标快照日期")
    private LocalDate date;
}
