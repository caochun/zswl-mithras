package cn.zswltech.mithras.kpi.bo;

import cn.zswltech.mithras.service.enums.common.ProjectBizType;
import cn.zswltech.mithras.service.enums.projestablish.LeaseType;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @description 绩效考核-项目利润明细-记录表
 */
@Data
public class KpiFinanceProjectProfitRecordBo {

    private static final long serialVersionUID = 1L;

    /**
    * id
    */
    private Long id;

    /**
    * finance_project_profit_detail id
    */
    private Long profitDetailId;

    /**
    * 批次号
    */
    private String batchNumber;

    /**
    * 项目利润记录id
    */
    private Long projectProfitId;

    /**
    * 年份
    */
    @TableField("year")
    private Integer year;

    /**
    * 月份
    */
    private Integer month;

    /**
    * 合同id
    */
    private Long contractId;

    @ApiModelProperty("借据id")
    private Long receiptId;

    private LocalDate receiptFirstPaymentDate;


    /**
     * {@link ProjectBizType#name()}
     **/
    private String bizType;

    /**
     * 租赁类型。直租、回租、经营性租赁
     * {@link LeaseType#name()}
     */
    private String leaseType;


    /**
    * 投放日
    */
    private LocalDate contractStartDate;

    /**
    * 当月收入
    */
    private Long incomeThisMonth;

    /**
    * 当月资金成本
    */
    private Long costThisMonth;

    /**
    * 当月风险金
    */
    private Long riskThisMonth;

    /**
    * 当月税金及附加
    */
    private Long taxThisMonth;

    /**
    * 当月项目利润
    */
    private Long profitThisMonth;

    /**
    * 当年累计收入
    */
    private Long totalIncomeThisYear;

    /**
    * 当年累计资金成本
    */
    private Long totalCostThisYear;

    /**
    * 当年累计风险金
    */
    private Long totalRiskThisYear;

    /**
    * 当年累计税金及附加
    */
    private Long totalTaxThisYear;

    /**
    * 当年累计利润总额
    */
    private Long totalProfitThisYear;

    /**
    * 费用计提比例快照
    */
    private Integer expenseRadio;

    /**
    * 当月附加税
    */
    private Long additionalTaxThisMonth;

    /**
    * 当月印花税
    */
    private Long stampTaxThisMonth;

    /**
    * 当年累计附加税
    */
    private Long totalAdditionalTaxThisYear;

    /**
    * 当年累计印花税
    */
    private Long totalStampTaxThisYear;

    /**
    * 本年累计毛利
    */
    private Long totalGrossProfitThisYear;

    /**
    * 本月毛利
    */
    private Long grossProfitThisMonth;

    /**
    * 本月收入
    */
    private Long revenueThisMonth;

    /**
    * 考核部门id
    */
    private Long assessDeptId;

    /**
    * 本年累计利润总额扣费前
    */
    private Long totalProfitThisYearBefore;

    /**
    * 年初风险金余额
    */
    private Long riskBalanceBeginYear;

    /**
    * 累计风险金计提/冲抵
    */
    private Long totalRiskBalanceThisYear;


    private LocalDateTime createTime;
    private Long createBy;
    private LocalDateTime updateTime;
    private Long updateBy;

}
