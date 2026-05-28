package cn.zswltech.mithras.dto.budget;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author dingqi
 * @date 2025/5/23
 * @description
 */
@Data
public class BudgetPlanPayDetailDeptConfirmInfoREQ {
    @NotNull(message = "投放计划id不能为空")
    @ApiModelProperty("投放计划id")
    private Long budgetPlanPayId;

    @ApiModelProperty("部门id")
    private Long belongDeptId;
}
