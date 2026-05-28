package cn.zswltech.mithras.dto.afterlease;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@ApiModel("租后管理-租后检查计划详情-返回体")
public class AfterLeaseCheckPlanCommonlyDetailRSP{

    @ApiModelProperty("计划id")
    private Long planId;

    @ApiModelProperty("计划clientId")
    private Long planClientId;

    @ApiModelProperty("计划名称")
    private String planName;

    @ApiModelProperty("计划类型")
    private String planType;

    @ApiModelProperty("客户id")
    private Long clientId;

    @ApiModelProperty("客户名称")
    private String clientName;

    @ApiModelProperty("主办id")
    private Long belongSponsorId;

    @ApiModelProperty("主办名称")
    private String belongSponsorName;

    @ApiModelProperty("管理形式")
    private String checkWay;

    @ApiModelProperty("协查风控经理id")
    private Long riskManagerId;

    @ApiModelProperty("协查风控经理名称")
    private String riskManagerName;

    @ApiModelProperty("截止时间")
    private LocalDate deadLine;

    @ApiModelProperty("截止时间标签")
    private List<String> deadlineLabel;

    @ApiModelProperty("风险敞口（元）")
    private Long riskExposure;

    @ApiModelProperty("模版类型")
    private String reportType;

}
