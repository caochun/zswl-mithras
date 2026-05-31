package cn.zswltech.mithras.service.mapper.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * @author dingqi
 * @date 2023/6/25
 * @description 会计利润测算结果
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("profit_calculate_result")
public class ProfitCalculateResult extends BaseModel {
    /**
     * 主键id
     */
    @TableId(type = IdType.AUTO)
    @TableField(value = "id")
    private Long id;

    /**
     * 测算日期（T+1）
     */
    @TableField(value = "calculate_date")
    private LocalDate calculateDate;

    /**
     * 合同id
     */
    @TableField(value = "contract_id")
    private Long contractId;

    /**
     * 投放时间
     */
    @TableField(value = "contract_start_date")
    private LocalDate contractStartDate;

    /**
     * 当年已确认收入（税后）
     */
    @TableField(value = "confirm_income_this_year")
    private Long confirmIncomeThisYear;

    /**
     * 当年测算利息收入
     */
    @TableField(value = "calculate_interest_this_year")
    private Long calculateInterestThisYear;

    /**
     * 营业收入
     */
    @TableField(value = "operating_income")
    private Long operatingIncome;

    /**
     * FTP成本
     */
    @TableField(value = "ftp_interest")
    private Long ftpInterest;

    /**
     * 上期末风险金余额
     */
    @TableField(value = "risk_balance_end_of_last_year")
    private Long riskBalanceEndOfLastYear;

    /**
     * 本期末风险金余额
     */
    @TableField(value = "risk_balance_end_of_this_year")
    private Long riskBalanceEndOfThisYear;

    /**
     * 本期风险金计提/转回
     */
    @TableField(value = "risk_provision_this_year")
    private Long riskProvisionThisYear;

    /**
     * 附加税
     */
    @TableField(value = "additional_tax")
    private Long additionalTax;

    /**
     * 利润总额
     */
    @TableField(value = "profit")
    private Long profit;

    /**
     * 利润总额（扣除费用）
     */
    @TableField(value = "profit_exclude_fee")
    private Long profitExcludeFee;

    /**
     * 本年末剩余本金
     */
    @TableField(value = "remaining_principle_end_of_this_year")
    private Long remainingPrincipleEndOfThisYear;

    /**
     * 本年末保证金余额
     */
    @TableField(value = "remaining_earnest_end_of_this_year")
    private Long remainingEarnestEndOfThisYear;

    /**
     * 年末敞口
     */
    @TableField(value = "exposure_end_of_this_year")
    private Long exposureEndOfThisYear;
}
