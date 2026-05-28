package cn.zswltech.mithras.dto.budget;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * @description 预算管理-预算计划-利润预算
 * @author vico
 * @date 2025-04-11
 */
@Data
@ApiModel("预算管理-预算计划-利润预算新增-请求体")
public class BudgetPlanProfitCreateREQ {
    /**
     * 预算区间开始日期
     */
    @NotNull(message = "<预算区间开始日期>不能为空")
    @ApiModelProperty(value = "预算区间开始日期")
    private LocalDate budgetDateFrom;

    /**
     * 预算区间结束日期
     */
    @NotNull(message = "<预算区间结束日期>不能为空")
    @ApiModelProperty(value = "预算区间结束日期")
    private LocalDate budgetDateTo;

    /**
     * 填报区间开始日期
     */
    @NotNull(message = "<填报区间开始日期>不能为空")
    @ApiModelProperty(value = "填报区间开始日期")
    private LocalDate writeDateFrom;

    /**
     * 填报区间结束日期
     */
    @NotNull(message = "<填报区间结束日期>不能为空")
    @ApiModelProperty(value = "填报区间结束日期")
    private LocalDate writeDateTo;

    /**
     * 预算类型
     */
    @NotBlank(message = "<预算类型>不能为空")
    @ApiModelProperty(value = "预算类型")
    private String budgetType;

    /**
     * 是否收集
     */
    @NotNull(message = "<是否收集>不能为空")
    @ApiModelProperty(value = "是否收集")
    private Integer needCollect;

    /**
     * 收集截止日期
     */
    @ApiModelProperty(value = "收集截止日期")
    private LocalDate collectDateTo;

    /**
     * 原计划id（月度调整时使用）
     */
    @ApiModelProperty(value = "原计划id（月度调整时使用）")
    private Long originBudgetPlanId;

}
