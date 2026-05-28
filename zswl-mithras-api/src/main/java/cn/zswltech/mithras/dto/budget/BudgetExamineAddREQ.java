package cn.zswltech.mithras.dto.budget;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @description 预算管理-预算考核
 * @author vico
 * @date 2025-04-11
 */
@Data
@ApiModel("预算管理-预算考核新增-请求体")
public class BudgetExamineAddREQ {



    /**
    * 考核年份
    */
    @ApiModelProperty(value = "考核年份")
    @NotNull(message = "考核年份不能为空")
    private Integer examineYear;

    /**
    * 考核月份
    */
    @ApiModelProperty(value = "考核月份")
    @NotNull(message = "考核月份不能为空")
    private Integer examineMonth;

}
