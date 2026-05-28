package cn.zswltech.mithras.dto.budget;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author dingqi
 * @date 2025/5/14
 * @description
 */
@Data
public class BudgetPlanProfitSummaryRSP {
    @ApiModelProperty("部门数据")
    private List<BudgetPlanProfitDeptSummary> deptDataList;

    @ApiModelProperty("合计行数据")
    private Data sumData;

    @lombok.Data
    public static class BudgetPlanProfitDeptSummary {
        @ApiModelProperty("部门id")
        private Long belongDeptId;

        @ApiModelProperty("部门名称")
        private String belongDeptName;

        @ApiModelProperty("数据列表")
        private List<Data> dataList;
    }

    @lombok.Data
    public static class Data {
        // 用于标识该行数据是否部门小计行，仅后端计算时会用到
        private transient boolean isSumRow = false;

        @ApiModelProperty("FTP行业分类")
        private String ftpIndustryCategory;

        @ApiModelProperty("FTP行业分类-展示文案")
        private String ftpIndustryCategoryDisplay;

        /* ************* 存量项目字段开始 ************* */
        @ApiModelProperty("收入-存量")
        private Long incomeHistory = 0L;

        @ApiModelProperty("成本-存量")
        private Long costHistory = 0L;

        @ApiModelProperty("差价-存量")
        private Long diffHistory = 0L;

        @ApiModelProperty("税费+拨备-存量")
        private Long taxRiskHistory = 0L;

        @ApiModelProperty("利润-存量")
        private Long profitHistory = 0L;
        /* ************* 存量项目字段结束 ************* */

        /* ************* 新增项目字段开始 ************* */
        @ApiModelProperty("投放额")
        private Long payAmountFeature = 0L;

        @ApiModelProperty("收入-新增")
        private Long incomeFeature = 0L;

        @ApiModelProperty("成本-新增")
        private Long costFeature = 0L;

        @ApiModelProperty("差价-新增")
        private Long diffFeature = 0L;

        @ApiModelProperty("税费-新增")
        private Long taxFeature = 0L;

        @ApiModelProperty("拨备-新增")
        private Long riskFeature = 0L;

        @ApiModelProperty("利润-新增")
        private Long profitFeature = 0L;

        @ApiModelProperty("收益率水平（IRR）")
        private Integer irrFeature = 0;

        @ApiModelProperty("年化手续费率")
        private Integer consultingFeeRateYearFeature = 0;

        @ApiModelProperty("手续费率")
        private Integer consultingFeeRateFeature = 0;

        @ApiModelProperty("资金成本（FTP）")
        private Integer ftpFeature = 0;
        /* ************* 新增项目字段结束 ************* */

        /* ************* 合计字段开始 ************* */
        @ApiModelProperty("收入-合计")
        private Long incomeTotal = 0L;

        @ApiModelProperty("利润-合计")
        private Long profitTotal = 0L;

        @ApiModelProperty("费用-合计")
        private Long expenseTotal = 0L;

        @ApiModelProperty("目标利润-合计")
        private Long profitGoalTotal = 0L;

        @ApiModelProperty("目标利润（拨备前）-合计")
        private Long profitGoalWithoutRiskFundTotal = 0L;

        @ApiModelProperty("年初/月初资产总额-合计")
        private Long beginOfThisPeriodBalance = 0L;

        @ApiModelProperty("年末/月末资产总额-合计")
        private Long endOfThisPeriodBalance = 0L;
        /* ************* 合计字段结束 ************* */
    }
}
