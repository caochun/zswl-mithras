package cn.zswltech.mithras.dto.budget;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @description 预算管理-预算计划-成本预算-明细
 * @author vico
 * @date 2025-04-11
 */
@Data
@ApiModel("预算管理-预算计划-成本预算-明细列表-请求体")
public class BudgetPlanCostDetailListREQ {

    @ApiModelProperty(value = "成本预算id")
    @NotNull(message = "成本预算ID不能为空")
    private Long budgetPlanCostId;

}
