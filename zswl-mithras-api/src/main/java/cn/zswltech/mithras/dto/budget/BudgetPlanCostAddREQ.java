package cn.zswltech.mithras.dto.budget;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;

/**
 * @description 预算管理-预算计划-成本预算
 * @author vico
 * @date 2025-04-11
 */
@Data
@ApiModel("预算管理-预算计划-成本预算新增-请求体")
public class BudgetPlanCostAddREQ {

    /**
    * 逻辑删除，0-未删除
    */
    @ApiModelProperty(value = "逻辑删除，0-未删除")
    private Integer deleted;

    /**
    * 预算计划id
    */
    @ApiModelProperty(value = "预算计划id")
    private Long budgetPlanId;

    /**
    * 计划名称
    */
    @ApiModelProperty(value = "计划名称")
    private String budgetPlanName;

    /**
    * 预算计划开始日期
    */
    @ApiModelProperty(value = "预算计划开始日期")
    private LocalDate budgetDateFrom;

    /**
    * 预算计划结束日期
    */
    @ApiModelProperty(value = "预算计划结束日期")
    private LocalDate budgetDateTo;

    /**
    * 计划填报开始日期
    */
    @ApiModelProperty(value = "计划填报开始日期")
    private LocalDate writeDateFrom;

    /**
    * 计划填报结束日期
    */
    @ApiModelProperty(value = "计划填报结束日期")
    private LocalDate writeDateTo;

    /**
    * 预算类型
    */
    @ApiModelProperty(value = "预算类型")
    private String budgetType;

    /**
    * 状态
    */
    @ApiModelProperty(value = "状态")
    private String budgetStatus;

    /**
    * 收集截止日期
    */
    @ApiModelProperty(value = "收集截止日期")
    private LocalDate collectDateTo;

}
