package cn.zswltech.mithras.finance.view.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 工作台融资情况快照表(DashboardFvFinanceInfoSnapshot)表实体类
 *
 * @author makejava
 * @since 2025-09-02 09:30:34
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("dashboard_fv_finance_info_snapshot")
public class DashboardFvFinanceInfoSnapshot extends Model<DashboardFvFinanceInfoSnapshot> {

    /**
     * Id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 卡片id
     */
    @TableField("card_id")
    private Long cardId;

    /**
     * 融资id
     */
    @TableField("financing_id")
    private Long financingId;

    /**
     * 融资编号
     */
    @TableField("financing_code")
    private String financingCode;

    /**
     * 机构名称
     */
    @TableField("org_name")
    private String orgName;

    /**
     * 融资类别Code FinancingTypeEnum
     */
    @TableField("financing_type_code")
    private String financingTypeCode;

    /**
     * 融资类别display FinancingTypeEnum
     */
    @TableField("financing_type_display")
    private String financingTypeDisplay;

    /**
     * 还款期限(月)
     */
    @TableField("financing_month")
    private String financingMonth;

    /**
     * 还款方式
     */
    @TableField("repay_way")
    private String repayWay;

    /**
     * 融资金额
     */
    @TableField("loan_amount")
    private BigDecimal loanAmount;

    /**
     * 剩余金额
     */
    @TableField("remaining_principle_amount")
    private BigDecimal remainingPrincipleAmount;

    /**
     * 质押资产合同编号
     */
    @TableField("pledge_contract_code")
    private String pledgeContractCode;


    /**
     * 综合资金成本
     */
    @TableField("comprehensive_interest_rate")
    private BigDecimal comprehensiveInterestRate;

    /**
     * 合同利率
     */
    @TableField("interest_rate")
    private BigDecimal interestRate;

    /**
     * 起息日
     */
    @TableField("actual_loan_date")
    private LocalDate actualLoanDate;

    /**
     * 到期日
     */
    @TableField("actual_expire_date")
    private LocalDate actualExpireDate;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 获取主键值
     *
     * @return 主键值
     */
    @Override
    protected Serializable pkVal() {
        return this.id;
    }
}

