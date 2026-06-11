package cn.zswltech.mithras.third.financialshare.mapper.model;

import cn.zswltech.mithras.foundation.persistence.model.BaseModelWithLogicDelete;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.io.Serializable;

/**
 * 流水核销tab信息主表
 *
 * @author bigbear
 * @TableName finance_flow_tab_main_info
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName(value = "finance_flow_tab_main_info")
public class FinanceFlowTabMainInfo extends BaseModelWithLogicDelete implements Serializable {
    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 批次号
     */
    @TableField(value = "batch_number")
    private String batchNumber;

    /**
     * 业务模块 WriteOffBusinessModelEnum#name
     */
    @TableField(value = "business_model")
    private String businessModel;

    /**
     * 账户类型
     */
    @TableField(value = "account_type")
    private String accountType;

    /**
     * 来款账户名称
     */
    @TableField(value = "bank_account_name")
    private String bankAccountName;

    /**
     * 来款账户银行账号
     */
    @TableField(value = "bank_account_number")
    private String bankAccountNumber;

    /**
     * 监管账户银行账户名称
     */
    @TableField(value = "supervise_account_name")
    private String superviseAccountName;

    /**
     * 监管账户银行账号
     */
    @TableField(value = "supervise_account_number")
    private String superviseAccountNumber;

    /**
     * 是否完美匹配
     */
    @TableField(value = "is_perfect_match")
    private Integer isPerfectMatch;

    /**
     * 核销状态
     */
    @TableField(value = "write_off_status")
    private String writeOffStatus;

    /**
     * tab过期标识，默认为0，不能为空
     */
    @TableField(value = "is_expired")
    private Integer isExpired;

    /**
     * 本次是否核销
     */
    @TableField(value = "is_write_off")
    private Integer isWriteOff;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}