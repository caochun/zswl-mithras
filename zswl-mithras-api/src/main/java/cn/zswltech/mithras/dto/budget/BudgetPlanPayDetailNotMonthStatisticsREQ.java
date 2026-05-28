package cn.zswltech.mithras.dto.budget;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author dingqi
 * @date 2025/4/15
 * @description
 */
@Data
public class BudgetPlanPayDetailNotMonthStatisticsREQ {
    @ApiModelProperty("投放计划id")
    @NotNull(message = "<投放计划id>不能为空")
    private Long budgetPlanPayId;

    @ApiModelProperty("业务部门id")
    private Long bizDeptId;
}
