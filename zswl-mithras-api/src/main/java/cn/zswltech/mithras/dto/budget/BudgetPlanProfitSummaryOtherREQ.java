package cn.zswltech.mithras.dto.budget;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author dingqi
 * @date 2025/5/22
 * @description
 */
@Data
public class BudgetPlanProfitSummaryOtherREQ {
    @NotNull(message = "<利润预算id>不能为空")
    @ApiModelProperty("利润预算id")
    private Long budgetPlanProfitId;

    @ApiModelProperty("部门id")
    private Long belongDeptId;
}
