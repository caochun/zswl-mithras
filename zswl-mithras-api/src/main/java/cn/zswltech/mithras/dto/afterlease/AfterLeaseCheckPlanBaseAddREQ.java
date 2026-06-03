package cn.zswltech.mithras.dto.afterlease;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.time.LocalDate;

/**
 * @author dingqi
 * @date 2022/11/10
 * @description
 */
@Data
@ApiModel("租后管理-新建检查计划-请求体")
public class AfterLeaseCheckPlanBaseAddREQ {
    @ApiModelProperty("检查计划类型")
    @NotBlank(message = "检查计划类型不能为空")
    private String planType;

    private Long planId;

    @ApiModelProperty("计划clientId")
    private Long planClientId;

    @ApiModelProperty("待发起id")
    private Long commonId;

    @ApiModelProperty("检查计划名称")
    @NotBlank(message = "检查计划名称不能为空")
    private String planName;

    @ApiModelProperty("客户id")
    private Long clientId;
    /**
     * 主办id
     */
    @ApiModelProperty("主办id")
    private Long belongSponsorId;

    @ApiModelProperty("所属部门")
    private Long belongDeptId;

    @ApiModelProperty("管理形式 AfterLeaseCheckWayEnum")
    private String checkWay;

    /**
     * 协查风控经理id
     */
    @ApiModelProperty(value = "协查风控经理id")
    private Long riskManagerId;

    @ApiModelProperty(value = "检查期限")
    private Integer term;

    /**
     * 协查风控经理名称
     */
    @ApiModelProperty(value = "协查风控经理名称")
    private String riskManagerName;

    @ApiModelProperty ("租后检查截止日")
    private LocalDate deadLine;

    @ApiModelProperty ("截止日标签")
    private String deadlineLabel;

    @ApiModelProperty("风险敞口")
    private Long stockRiskExposure;

    /**
     * 检查报告模板类型 {@link cn.zswltech.mithras.afterlease.domain.enums.AfterLeaseCheckReportTypeEnum#name()}
     */
    @ApiModelProperty("检查报告模板类型@AfterLeaseCheckReportTypeEnum#name")
    private String reportType;

}
