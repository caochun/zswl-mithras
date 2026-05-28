package cn.zswltech.mithras.dto.budget;
import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
/**
 * @description 预算管理-预算考核-投放计划执行情况表
 * @author vico
 * @date 2025-04-11
 */
@Data
@ApiModel("预算管理-预算考核-投放计划执行情况表新增-请求体")
public class BudgetExaminePayPlanExecuteAddREQ {


    /**
    * 预算考核id
    */
    @ApiModelProperty(value = "预算考核id")
    private Long budgetExamineId;

}
