package cn.zswltech.mithras.service.fund.direct.entity;

import cn.zswltech.mithras.common.model.BaseModel;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * @description 直接融资-资产池信息
 * @author zhaozhengkang
 * @date 2023-06-17
 */
@Data
public class FundDirectFinancingAssetPool extends BaseModel implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("financing_id")
    private Long financingId;

    /**
     * 封包日
     */
    @TableField("package_date")
    private LocalDate packageDate;

    /**
     * 加权平均贷款年利率（%）
     */
    @TableField("average_annual_interest_rate")
    private Long averageAnnualInterestRate;

    /**
     * 期末贷款笔数
     */
    @TableField("number_of_loans")
    private Long numberOfLoans;

    /**
     * 加权平均合同期限（月）
     */
    @TableField("average_contract_term")
    private Long averageContractTerm;

    /**
     * 借款人户数（户）
     */
    @TableField("number_of_borrowers")
    private Long numberOfBorrowers;

    /**
     * 加权平均账龄（月）
     */
    @TableField("average_aging")
    private Long averageAging;

    /**
     * 最高贷款利率（%）
     */
    @TableField("max_loan_interest_rate")
    private Long maxLoanInterestRate;

    /**
    * 最低贷款利率（%）
    */
    @TableField("min_loan_interest_rate")
    private String minLoanInterestRate;

    /**
     * 期末租金余额（万元）
     */
    @TableField("ending_rent_balance")
    private Long endingRentBalance;

    /**
     * 期末本金余额（万元）
     */
    @TableField("ending_principal_balance")
    private Long endingPrincipalBalance;

    /**
     * 加权平均剩余期限（月）
     */
    @TableField("average_remaining_term")
    private Long averageRemainingTerm;

    /**
     * 覆盖倍数
     */
    @TableField("coverage_multiple")
    private Long coverageMultiple;

    /**
     * 单笔贷款最高本金余额（万元）
     */
    @TableField("max_principal_balance")
    private Long maxPrincipalBalance;

    /**
     * 单笔贷款平均本金余额（万元）
     */
    @TableField("average_principal_balance")
    private Long averagePrincipalBalance;

    /**
     * 贷款最长剩余期限（月）
     */
    @TableField("max_remaining_term")
    private Long maxRemainingTerm;

    /**
     * 贷款最短剩余期限（月）
     */
    @TableField("min_remaining_term")
    private Long minRemainingTerm;

}
