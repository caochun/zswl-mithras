package cn.zswltech.mithras.dto.budget;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * @author dingqi
 * @date 2025/5/14
 * @description
 */
@Data
public class BudgetPlanProfitProcessREQ {
    @NotNull(message = "<利润预算id>不能为空")
    @ApiModelProperty("利润预算id")
    private Long budgetPlanProfitId;

    @ApiModelProperty("部门id")
    private Long belongDeptId;

    @ApiModelProperty("日期-起")
    private LocalDate queryDateFrom;

    @ApiModelProperty("日期-止")
    private LocalDate queryDateTo;
}
