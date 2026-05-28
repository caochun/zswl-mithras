package cn.zswltech.mithras.dto.budget.weekly;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
/**
 * @description 预算管理-投放计划-项目周报
 * @author vico
 * @date 2025-04-11
 */
@Data
@ApiModel("预算管理-投放计划-项目周报列表-请求体")
public class BudgetPlanPayWeeklyReportListREQ extends PageReq {

    /**
     * 状态
     */
    @ApiModelProperty("状态")
    private String planStatus;


    /**
     * 预算计划名称
     */
    @ApiModelProperty("预算计划名称")
    private String budgetPlanName;

}
