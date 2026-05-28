package cn.zswltech.mithras.dto.budget.weekly;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author dingqi
 * @date 2025/4/15
 * @description
 */
@Data
public class BudgetPlanPayWeeklyReportDetailStatisticsREQ extends PageReq {
    @ApiModelProperty("项目周报id")
    @NotNull(message = "<项目周报id>不能为空")
    private Long budgetPlanWeeklyReportId;

    @ApiModelProperty("业务部门id")
    private Long bizDeptId;
}
