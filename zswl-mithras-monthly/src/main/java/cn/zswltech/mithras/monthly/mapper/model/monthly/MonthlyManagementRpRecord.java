package cn.zswltech.mithras.monthly.mapper.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 剩余本金法-记录表
 * @author yangxiong
 * @TableName monthly_management_rp_record
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value ="monthly_management_rp_record")
public class MonthlyManagementRpRecord extends MonthlyManageBaseModel implements Serializable {

    /**
     * 关联的借据ID
     */
    @TableField(value = "receipt_id")
    private Long receiptId;

    /**
     * 借据的唯一编号
     */
    @TableField(value = "receipt_code")
    private String receiptCode;

    /**
     * 关联的合同ID
     */
    @TableField(value = "contract_id")
    private Long contractId;

    /**
     * 项目名称
     */
    @TableField(value = "proj_name")
    private String projName;

    /**
     * 合同的唯一编号
     */
    @TableField(value = "contract_code")
    private String contractCode;

    /**
     * 客户名称
     */
    @TableField(value = "client_name")
    private String clientName;

    /**
     * 客户ID
     */
    @TableField(value = "client_id")
    private Long clientId;

    /**
     * 租赁类型
     */
    @TableField(value = "lease_type")
    private String leaseType;

    /**
     * 业务类型
     */
    @TableField(value = "biz_type")
    private String bizType;

    /**
     * 适用的税率
     */
    @TableField(value = "tax_rate")
    private Long taxRate;

    /**
     * 本月总收入金额（含税）
     */
    @TableField(value = "income_sum")
    private Long incomeSum;

    /**
     * 本月总收入金额（不含税）
     */
    @TableField(value = "income_without_tax_sum")
    private Long incomeWithoutTaxSum;

    /**
     * 当前是否处于逾期状态
     */
    @TableField(value = "overdue_type")
    private String overdueType;

    /**
     * 实际开始租赁的日期
     */
    @TableField(value = "actual_lease_date")
    private LocalDateTime actualLeaseDate;

    /**
     * 最近一次全额偿还租金的期次
     */
    @TableField(value = "the_latest_full_refund_rent_period")
    private Integer theLatestFullRefundRentPeriod;

    /**
     * 最近一次全额偿还租金的应收款日期
     */
    @TableField(value = "the_latest_full_refund_rent_date")
    private LocalDateTime theLatestFullRefundRentDate;

    /**
     * 最近一次全额偿还租金后剩余的本金
     */
    @TableField(value = "the_latest_full_refund_rent_capital")
    private Long theLatestFullRefundRentCapital;

    /**
     * 合同约定的名义利率
     */
    @TableField(value = "contract_nominal_interest_rate")
    private Integer contractNominalInterestRate;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}