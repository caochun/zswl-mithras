package cn.zswltech.mithras.dto.budget;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
/**
 * @description 预算管理-预算考核-预算执行情况表
 * @author vico
 * @date 2025-04-11
 */
@Data
@ApiModel("预算管理-预算考核-预算执行情况表编辑-请求体")
public class BudgetExamineBudgetExecuteModifyREQ {

    /**
    * 主键id
    */
    @ApiModelProperty(value = "主键id")
    private Long id;


    /**
    * 字段名称
    */
    @ApiModelProperty(value = "字段名称")
    private String fieldName;

    /**
     * 本月数
     */
    @ApiModelProperty("本月数")
    private Long currentMonth;

    /**
     * 本年累计
     */
    @ApiModelProperty("本年累计")
    private Long totalYear;

    /**
     * 上年同期
     */
    @ApiModelProperty("上年同期")
    private Long lastYearPeriod;

    /**
     * 同比
     */
    @ApiModelProperty("同比")
    private Long onYear;

    /**
     * 全年预算目标
     */
    @ApiModelProperty("全年预算目标")
    private Long annualBudgetTarget;

    /**
     * 进度预算目标
     */
    @ApiModelProperty("进度预算目标")
    private Long progressBudgetTarget;

    /**
     * 进度预算完成率
     */
    @ApiModelProperty("进度预算完成率")
    private Long progressBudgetCompletionRate;

    /**
     * 全年预算完成率
     */
    @ApiModelProperty("全年预算完成率")
    private Long annualBudgetCompletionRate;
}
