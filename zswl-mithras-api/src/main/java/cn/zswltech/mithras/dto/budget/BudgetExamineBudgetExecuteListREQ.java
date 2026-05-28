package cn.zswltech.mithras.dto.budget;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @description 预算管理-预算考核-预算执行情况表
 * @author vico
 * @date 2025-04-11
 */
@Data
@ApiModel("预算管理-预算考核-预算执行情况表列表-请求体")
public class BudgetExamineBudgetExecuteListREQ {


    /**
     * 预算考核id
     */
    @ApiModelProperty("预算考核id")
    @NotNull(message = "预算考核id不能为空")
    private Long budgetExamineId;
}
