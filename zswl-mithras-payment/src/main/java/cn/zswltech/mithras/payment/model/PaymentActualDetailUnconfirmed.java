package cn.zswltech.mithras.payment.model;

import cn.zswltech.mithras.payment.enums.WriteOffStatus;
import cn.zswltech.mithras.foundation.persistence.model.BaseModel;
import cn.zswltech.mithras.foundation.persistence.plugin.IncludeNull;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * @author dingqi
 * @date 2023/12/14
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("payment_actual_detail_unconfirmed")
public class PaymentActualDetailUnconfirmed extends BaseModel {
    /**
     * 主键id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 所属支付申请id
     */
    @TableField("payment_id")
    private Long paymentId;

    @TableField("seq_code")
    private String seqCode;

    /**
     * 所属合同id
     */
    @TableField("contract_id")
    private Long contractId;

    /**
     * 同步还是录入，显示‘财务系统’或者录入者名字
     */
    @TableField("info_source")
    private String infoSource;

    /**
     * 付款方式
     */
    @TableField("payment_method")
    private String paymentMethod;

    /**
     * 实付日期
     */
    @TableField("paid_in_date")
    private LocalDate paidInDate;

    /**
     * 实付金额
     */
    @TableField("paid_in_amount")
    private Long paidInAmount;

    /**
     * 附言
     */
    @TableField("postscript")
    @IncludeNull
    private String postscript;

    @TableField("our_account_id")
    private Long ourAccountId;
    @TableField("our_account_name")
    private String ourAccountName;
    @TableField("our_account_number")
    private String ourAccountNumber;
    @TableField("our_account_bank")
    private String ourAccountBank;

    @TableField("opposite_account_id")
    private Long oppositeAccountId;
    @TableField("opposite_account_name")
    private String oppositeAccountName;
    @TableField("opposite_account_number")
    private String oppositeAccountNumber;
    @TableField("opposite_account_bank")
    private String oppositeAccountBank;

    /**
     * 核销状态
     * {@link WriteOffStatus#name()}
     */
    @TableField("write_off_status")
    private String writeOffStatus;

    /**
     *流水id
     **/
    @TableField("flow_id")
    private String flowId;

    @TableField("client_id")
    private Long clientId;

    /**
     * 反核销标识 0正常，1反核销
     */
    @TableField(value = "cancel_write_off_flag")
    private Integer cancelWriteOffFlag;

    /**
     * 反核销对应ID
     */
    @TableField(value = "cancel_write_off_id")
    private Long cancelWriteOffId;

    /**
     * 关联付款核销确认流程id
     */
    @TableField("process_instance_id")
    private String processInstanceId;

    /**
     * 资金来源
     */
    @TableField("capital_source")
    private String capitalSource;

    /**
     * 融资编号
     */
    @TableField("financing_code")
    private String financingCode;
}
