package cn.zswltech.mithras.monthly.mapper.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 实际利率法-记录表
 * @author yangxiong
 * @TableName monthly_management_air_record
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value ="monthly_management_air_record")
public class MonthlyManagementAirRecord extends MonthlyManageBaseModel implements Serializable {

    /**
     * 借据ID
     */
    @TableField(value = "receipt_id")
    private Long receiptId;

    /**
     * 客户ID
     */
    @TableField(value = "client_id")
    private Long clientId;

    /**
     * 借据编号
     */
    @TableField(value = "receipt_code")
    private String receiptCode;

    /**
     * 合同ID
     */
    @TableField(value = "contract_id")
    private Long contractId;

    /**
     * 项目名称
     */
    @TableField(value = "proj_name")
    private String projName;

    /**
     * 合同编号
     */
    @TableField(value = "contract_code")
    private String contractCode;

    /**
     * 客户名称
     */
    @TableField(value = "client_name")
    private String clientName;

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
     * 税率
     */
    @TableField(value = "tax_rate")
    private Long taxRate;

    /**
     * 本月收入金额（含税）
     */
    @TableField(value = "income_sum")
    private Long incomeSum;

    /**
     * 本月收入金额（不含税）
     */
    @TableField(value = "income_without_tax_sum")
    private Long incomeWithoutTaxSum;

    /**
     * 当前是否逾期
     */
    @TableField(value = "overdue_type")
    private String overdueType;

    /**
     * 实际起租日
     */
    @TableField(value = "actual_lease_date")
    private LocalDateTime actualLeaseDate;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}