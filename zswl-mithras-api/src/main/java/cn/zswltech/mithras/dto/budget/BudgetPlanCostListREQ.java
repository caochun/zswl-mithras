package cn.zswltech.mithras.dto.budget;
import lombok.Data;
import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
/**
 * @description 预算管理-预算计划-成本预算
 * @author vico
 * @date 2025-04-11
 */
@Data
@ApiModel("预算管理-预算计划-成本预算列表-请求体")
public class BudgetPlanCostListREQ extends PageReq {

    @ApiModelProperty(value = "状态 budgetStatusEnum")
    private String budgetStatus;
}
