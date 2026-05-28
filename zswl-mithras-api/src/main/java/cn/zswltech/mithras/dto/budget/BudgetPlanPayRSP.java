package cn.zswltech.mithras.dto.budget;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;

/**
 * @author dingqi
 * @date 2025/5/9
 * @description
 */
@Data
public class BudgetPlanPayRSP {
    /**
     * 主键id
     */
    @ApiModelProperty(value = "主键id")
    private Long id;

    /**
     * 预算计划id
     */
    @ApiModelProperty(value = "预算计划id")
    private Long budgetPlanId;

    /**
     * 计划名称
     */
    @ApiModelProperty(value = "计划名称")
    private String budgetPlanName;

    /**
     * 预算计划开始日期
     */
    @ApiModelProperty(value = "预算计划开始日期")
    private LocalDate budgetDateFrom;

    /**
     * 预算计划结束日期
     */
    @ApiModelProperty(value = "预算计划结束日期")
    private LocalDate budgetDateTo;

    /**
     * 计划填报开始日期
     */
    @ApiModelProperty(value = "计划填报开始日期")
    private LocalDate writeDateFrom;

    /**
     * 计划填报结束日期
     */
    @ApiModelProperty(value = "计划填报结束日期")
    private LocalDate writeDateTo;

    /**
     * 预算类型
     */
    @ApiModelProperty(value = "预算类型")
    private String budgetType;

    /**
     * 状态
     */
    @ApiModelProperty(value = "状态")
    private String budgetStatus;

    /**
     * 收集截止日期
     */
    @ApiModelProperty(value = "收集截止日期")
    private LocalDate collectDateTo;

    /**
     * 投放计划财务流程实例ID
     */
    @ApiModelProperty(value = "收集流程实例ID")
    private String processInstanceId;
}
