package cn.zswltech.mithras.service.overdue.application.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2024/10/22 10:48
 */
@Data
@ApiModel(value = "催收列表响应")
public class CollectionListDto {

    private Long id;

    @ApiModelProperty(value = "客户Id")
    private Long clientId;

    @ApiModelProperty(value = "客户名称")
    private String clientName;

    @ApiModelProperty(value = "风险敞口")
    private Long riskExposure;

    @ApiModelProperty(value = "逾期租金")
    private Long overdueRent;

    @ApiModelProperty(value = "最大逾期天数")
    private Integer curMaxOverdueDays;

    @ApiModelProperty(value = "逾期罚息")
    private Long lateCharge;

    @ApiModelProperty(value = "主办")
    private String projectSponsorName;

    @ApiModelProperty(value = "业务部门")
    private String bizDeptName;

    @ApiModelProperty(value = "最新进展")
    private String latestProgress;

    @ApiModelProperty(value = "最近跟进人")
    private String processPerson;

    @ApiModelProperty(value = "最近跟进时间")
    private LocalDate processTime;

    @ApiModelProperty(value = "逾期")
    private Boolean overdue;
}
