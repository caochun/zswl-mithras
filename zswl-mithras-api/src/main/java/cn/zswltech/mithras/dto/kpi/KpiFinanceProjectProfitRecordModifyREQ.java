package cn.zswltech.mithras.dto.kpi;
import lombok.Data;
import java.time.LocalDateTime;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
/**
 * @description 绩效考核-项目利润明细-记录表
 * @author vico
 * @date 2024-09-25
 */
@Data
@ApiModel("绩效考核-项目利润明细-记录表编辑-请求体")
public class KpiFinanceProjectProfitRecordModifyREQ {

    /**
    * id
    */
    @ApiModelProperty(value = "id")
    private Long id;

    /**
    * finance_project_profit_detail id
    */
    @ApiModelProperty(value = "finance_project_profit_detail id")
    private Long profitDetailId;

    /**
    * 批次号
    */
    @ApiModelProperty(value = "批次号")
    private String batchNumber;

    /**
    * 项目利润记录id
    */
    @ApiModelProperty(value = "项目利润记录id")
    private Long projectProfitId;

    /**
    * 年份
    */
    @ApiModelProperty(value = "年份")
    private Integer year;

    /**
    * 月份
    */
    @ApiModelProperty(value = "月份")
    private Integer month;

    /**
    * 合同id
    */
    @ApiModelProperty(value = "合同id")
    private Long contractId;

    /**
    * 投放日
    */
    @ApiModelProperty(value = "投放日")
    private LocalDateTime contractStartDate;

    /**
    * 当月收入
    */
    @ApiModelProperty(value = "当月收入")
    private Long incomeThisMonth;

    /**
    * 当月资金成本
    */
    @ApiModelProperty(value = "当月资金成本")
    private Long costThisMonth;

    /**
    * 当月风险金
    */
    @ApiModelProperty(value = "当月风险金")
    private Long riskThisMonth;

    /**
    * 当月税金及附加
    */
    @ApiModelProperty(value = "当月税金及附加")
    private Long taxThisMonth;

    /**
    * 当月项目利润
    */
    @ApiModelProperty(value = "当月项目利润")
    private Long profitThisMonth;

    /**
    * 当年累计收入
    */
    @ApiModelProperty(value = "当年累计收入")
    private Long totalIncomeThisYear;

    /**
    * 当年累计资金成本
    */
    @ApiModelProperty(value = "当年累计资金成本")
    private Long totalCostThisYear;

    /**
    * 当年累计风险金
    */
    @ApiModelProperty(value = "当年累计风险金")
    private Long totalRiskThisYear;

    /**
    * 当年累计税金及附加
    */
    @ApiModelProperty(value = "当年累计税金及附加")
    private Long totalTaxThisYear;

    /**
    * 当年累计利润总额
    */
    @ApiModelProperty(value = "当年累计利润总额")
    private Long totalProfitThisYear;

    /**
    * 费用计提比例快照
    */
    @ApiModelProperty(value = "费用计提比例快照")
    private Integer expenseRadio;

    /**
    * 当月附加税
    */
    @ApiModelProperty(value = "当月附加税")
    private Long additionalTaxThisMonth;

    /**
    * 当月印花税
    */
    @ApiModelProperty(value = "当月印花税")
    private Long stampTaxThisMonth;

    /**
    * 当年累计附加税
    */
    @ApiModelProperty(value = "当年累计附加税")
    private Long totalAdditionalTaxThisYear;

    /**
    * 当年累计印花税
    */
    @ApiModelProperty(value = "当年累计印花税")
    private Long totalStampTaxThisYear;

    /**
    * 本年累计毛利
    */
    @ApiModelProperty(value = "本年累计毛利")
    private Long totalGrossProfitThisYear;

    /**
    * 本月毛利
    */
    @ApiModelProperty(value = "本月毛利")
    private Long grossProfitThisMonth;

    /**
    * 本月收入
    */
    @ApiModelProperty(value = "本月收入")
    private Long revenueThisMonth;

    /**
    * 考核部门id
    */
    @ApiModelProperty(value = "考核部门id")
    private Long assessDeptId;

    /**
    * 本年累计利润总额扣费前
    */
    @ApiModelProperty(value = "本年累计利润总额扣费前")
    private Long totalProfitThisYearBefore;

    /**
    * 年初风险金余额
    */
    @ApiModelProperty(value = "年初风险金余额")
    private Long riskBalanceBeginYear;

    /**
    * 累计风险金计提/冲抵
    */
    @ApiModelProperty(value = "累计风险金计提/冲抵")
    private Long totalRiskBalanceThisYear;

}
