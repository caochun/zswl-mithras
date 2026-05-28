package cn.zswltech.mithras.dto.budget;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author dingqi
 * @date 2025/4/16
 * @description
 */
@Data
public class BudgetPlanPayDetailMonthAddREQ {
    @NotNull(message = "<投放计划id>不能为空")
    @ApiModelProperty("投放计划id")
    private Long budgetPlanPayId;

    @NotNull(message = "<项目评审id>不能为空")
    @ApiModelProperty("项目评审id")
    private Long projReviewId;
}
