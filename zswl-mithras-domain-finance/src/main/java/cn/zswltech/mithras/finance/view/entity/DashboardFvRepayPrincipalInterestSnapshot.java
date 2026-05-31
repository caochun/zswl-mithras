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
 * 工作台还本付息信息表(DashboardFvRepayPrincipalInterestSnapshot)表实体类
 *
 * @author makejava
 * @since 2025-09-02 09:30:35
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("dashboard_fv_repay_principal_interest_snapshot")
public class DashboardFvRepayPrincipalInterestSnapshot extends Model<DashboardFvRepayPrincipalInterestSnapshot> {
    /**
     * Id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 卡片Id
     */
    @TableField("card_id")
    private Long cardId;

    /**
     * 收付现金流Id
     */
    @TableField("receipt_repay_cash_flow_id")
    private Long receiptRepayCashFlowId;

    /**
     * 核销状态编码
     */
    @TableField("write_off_state")
    private String writeOffState;

    /**
     * 核销状态显示
     */
    @TableField("write_off_state_display")
    private String writeOffStateDisplay;

    /**
     * 融资Id
     */
    @TableField("financing_id")
    private Long financingId;

    /**
     * 融资编号
     */
    @TableField("financing_code")
    private String financingCode;

    /**
     * 是否直融(0-否,1-是)
     */
    @TableField("is_direct")
    private Integer isDirect;

    /**
     * 机构名称
     */
    @TableField("org_name")
    private String orgName;

    /**
     * 贷款金额
     */
    @TableField("loan_amount")
    private BigDecimal loanAmount;

    /**
     * 贷款余额
     */
    @TableField("loan_balance_amount")
    private BigDecimal loanBalanceAmount;

    /**
     * 应还总额
     */
    @TableField("repay_total_amount")
    private BigDecimal repayTotalAmount;

    /**
     * 应还本金
     */
    @TableField("repay_principal_amount")
    private BigDecimal repayPrincipalAmount;

    /**
     * 应还利息
     */
    @TableField("repay_interest_amount")
    private BigDecimal repayInterestAmount;

    /**
     * 应还日期
     */
    @TableField("repay_date")
    private String repayDate;

    /**
     * 实还金额
     */
    @TableField("actual_repay_amount")
    private BigDecimal actualRepayAmount;

    /**
     * 还款余额
     */
    @TableField("repay_balance_amount")
    private BigDecimal repayBalanceAmount;

    /**
     * 实际还款日期
     */
    @TableField("actual_repay_date")
    private String actualRepayDate;

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

