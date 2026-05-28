package cn.zswltech.mithras.dto.budget.weekly;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @description 预算管理-投放计划（月度）-项目周报-详情
 * @author vico
 * @date 2025-04-11
 */
@Data
@ApiModel("预算管理-投放计划（月度）-项目周报-详情新增-请求体")
public class BudgetPlanPayWeeklyReportDetailAddREQ {

    /**
    * 项目周报id
    */
    @ApiModelProperty(value = "项目周报id")
    @NotNull(message = "项目周报id不能为空")
    private Long budgetPlanWeeklyReportId;

    /**
    * 项目评审id
    */
    @ApiModelProperty(value = "项目评审id")
    private Long projReviewId;


}
