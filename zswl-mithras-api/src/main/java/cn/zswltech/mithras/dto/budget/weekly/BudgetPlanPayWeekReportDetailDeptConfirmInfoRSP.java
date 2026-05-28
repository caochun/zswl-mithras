package cn.zswltech.mithras.dto.budget.weekly;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author dingqi
 * @date 2025/4/15
 * @description
 */
@Data
public class BudgetPlanPayWeekReportDetailDeptConfirmInfoRSP {
    @ApiModelProperty("业务部门id")
    private Long bizDeptId;

    @ApiModelProperty("业务部门名称")
    private String bizDeptName;

    @ApiModelProperty("归属业务部门的数量")
    private Integer count;

    @ApiModelProperty("部门负责人是否确认")
    private Integer isBusinessheadConfirm;

}
