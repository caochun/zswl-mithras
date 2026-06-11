package cn.zswltech.mithras.creditreport.model;

import cn.zswltech.mithras.foundation.persistence.model.BaseModelWithLogicDelete;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @description 征信报告-信息概要表
 * @author vico
 * @date 2025-11-14
 */
@Data
public class CreditReportSummary extends BaseModelWithLogicDelete implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
    * id
    */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
    * 查询编号
    */
    @TableField("credit_code")
    private Long creditCode;

    /**
    * 征信报告基本表id
    */
    @TableField("credit_report_id")
    private Long creditReportId;

    /**
     * 征信报告客户表id
     */
    @TableField("credit_report_client_id")
    private Long creditReportClientId;

    /**
    * 首次有信贷交易年份
    */
    @TableField("first_credityear")
    private String firstCredityear;

    /**
    * 信贷交易机构数
    */
    @TableField("credit_organization_number")
    private Integer creditOrganizationNumber;

    /**
    * 未结清信贷交易机构数
    */
    @TableField("unsettled_credit_organization_number")
    private Integer unsettledCreditOrganizationNumber;

    /**
    * 首次有相关还款责任的年份
    */
    @TableField("first_repayment_responsibility_year")
    private String firstRepaymentResponsibilityYear;

    /**
    * 借贷交易-余额
    */
    @TableField("loan_transaction_balance")
    private BigDecimal loanTransactionBalance;

    /**
    * 借贷交易-被追偿余额
    */
    @TableField("loan_transaction_recovery_balance")
    private BigDecimal loanTransactionRecoveryBalance;

    /**
    * 借贷交易-关注类余额
    */
    @TableField("loan_transaction_focus_balance")
    private BigDecimal loanTransactionFocusBalance;

    /**
    * 借贷交易-不良类余额
    */
    @TableField("loan_transaction_bad_balance")
    private BigDecimal loanTransactionBadBalance;

    /**
    * 担保交易-余额
    */
    @TableField("guarantee_transaction_balance")
    private BigDecimal guaranteeTransactionBalance;

    /**
    * 担保交易-关注类余额
    */
    @TableField("guarantee_transaction_focus_balance")
    private BigDecimal guaranteeTransactionFocusBalance;

    /**
    * 担保交易-不良类余额
    */
    @TableField("guarantee_transaction_bad_balance")
    private BigDecimal guaranteeTransactionBadBalance;

    /**
    * 非信贷交易账户数
    */
    @TableField("non_credit_transaction_number")
    private Integer nonCreditTransactionNumber;

    /**
    * 欠税记录条数
    */
    @TableField("tax_arrears_records_number")
    private Integer taxArrearsRecordsNumber;

    /**
    * 民事判决记录条数
    */
    @TableField("civil_judgment_records_number")
    private Integer civilJudgmentRecordsNumber;

    /**
    * 强制执行记录条数
    */
    @TableField("mandatory_execution_records_number")
    private Integer mandatoryExecutionRecordsNumber;

    /**
    * 行政处罚记录条数
    */
    @TableField("administrative_penalty_records_number")
    private Integer administrativePenaltyRecordsNumber;


}
