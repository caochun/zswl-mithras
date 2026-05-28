package cn.zswltech.mithras.dto.budget;
import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
/**
 * @description 预算管理-预算考核-预算执行情况表
 * @author vico
 * @date 2025-04-11
 */
@Data
@ApiModel("预算管理-预算考核-预算执行情况表新增-请求体")
public class BudgetExamineBudgetExecuteAddREQ {


    /**
    * 预算考核id
    */
    @ApiModelProperty(value = "预算考核id")
    private Long budgetExamineId;


    /**
     * 预算考核年份
     */
    @ApiModelProperty(value = "预算考核年份")
    private Integer budgetExamineYear;

    /**
     * 预算考核月份
     */
    @ApiModelProperty(value = "预算考核月份")
    private Integer budgetExamineMonth;


}
