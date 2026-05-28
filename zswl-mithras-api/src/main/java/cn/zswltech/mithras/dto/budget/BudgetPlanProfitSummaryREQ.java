package cn.zswltech.mithras.dto.budget;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author dingqi
 * @date 2025/5/14
 * @description
 */
@Data
public class BudgetPlanProfitSummaryREQ {
    @NotNull(message = "<利润预算id>不能为空")
    @ApiModelProperty("利润预算id")
    private Long budgetPlanProfitId;

    @ApiModelProperty("部门id")
    private Long belongDeptId;

    @ApiModelProperty("FTP行业分类")
    private String ftpIndustryCategory;
}
