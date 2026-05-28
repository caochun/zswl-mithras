package cn.zswltech.mithras.dto.budget;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;

/**
 * @description 预算管理-预算计划-成本预算-明细
 * @author vico
 * @date 2025-04-11
 */
@Data
@ApiModel("预算管理-预算计划-成本预算-明细新增-请求体")
public class BudgetPlanCostDetailAddREQ {

    /**
    * 预算计划id
    */
    @ApiModelProperty(value = "预算计划id")
    private Long budgetPlanId;

    /**
    * 成本预算id
    */
    @ApiModelProperty(value = "成本预算id")
    private Long budgetPlanCostId;

}
