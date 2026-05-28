package cn.zswltech.mithras.dto.budget;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author vico
 * @description 预算管理-预算考核-投放计划执行情况表
 * @date 2025-04-11
 */
@Data
@ApiModel("预算管理-预算考核-投放计划执行情况表列表-返回体")
public class BudgetExaminePayPlanExecuteListRSP {

    @ApiModelProperty(value = "部门id")
    private Long deptId;
    @ApiModelProperty(value = "部门名称")
    private String deptName;
    @ApiModelProperty(value = "审批状态")
    private String approvalStatus;
    @ApiModelProperty(value = "本月数")
    private BudgetExaminePayPlanExecuteListBody monthDate;
    @ApiModelProperty(value = "本年累计")
    private BudgetExaminePayPlanExecuteListBody yearTotalDate;

    @Data
    public static class BudgetExaminePayPlanExecuteListBody {
        /**
         * 资金计划偏离度
         */
        @ApiModelProperty("资金计划偏离度")
        private Integer deviationDegreePlan;

        /**
         * 项目准确度
         */
        @ApiModelProperty("项目准确度")
        private Integer projectAccuracy;

        /**
         * 未及时提报次数-资金计划
         */
        @ApiModelProperty("未及时提报次数-资金计划")
        private Integer failedReportFundingPlan;

        /**
         * 未及时提报次数-周报
         */
        @ApiModelProperty("未及时提报次数-周报")
        private Integer failedReportWeek;


        /**
         * 延迟天数-资金计划
         */
        @ApiModelProperty("延迟天数-资金计划")
        private Integer delayDaysFundingPlan;

        /**
         * 延迟天数-周报
         */
        @ApiModelProperty("延迟天数-周报")
        private Integer delayDaysWeek;
    }
}
