package cn.zswltech.mithras.dto.budget;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author dingqi
 * @date 2025/5/21
 * @description
 */
@Data
public class BudgetPlanProfitDetailREQ {
    @NotNull(message = "利润预算id不能为空")
    @ApiModelProperty("利润预算id")
    private Long budgetPlanProfitId;

    @NotNull(message = "部门id不能为空")
    @ApiModelProperty("部门id")
    private Long belongDeptId;

    @ApiModelProperty("客户名称")
    private String clientName;
}
