package cn.zswltech.mithras.dto.budget.weekly;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;

/**
 * @description 预算管理-投放计划-项目周报
 * @author vico
 * @date 2025-04-11
 */
@Data
@ApiModel("预算管理-投放计划-项目周报新增-请求体")
public class BudgetPlanPayWeeklyReportAddREQ {

    /**
    * 预算计划id
    */
    @ApiModelProperty(value = "预算计划id")
    private Long budgetPlanId;

    /**
    * 预算计划名称
    */
    @ApiModelProperty(value = "预算计划名称")
    private String budgetPlanName;

    /**
    * 投放计划id
    */
    @ApiModelProperty(value = "投放计划id")
    private Long budgetPlanPayId;

    /**
    * 周报区间-起
    */
    @ApiModelProperty(value = "周报区间-起")
    private LocalDate dateFrom;

    /**
    * 周报区间-止
    */
    @ApiModelProperty(value = "周报区间-止")
    private LocalDate dateTo;

    private String planStatus;

}
