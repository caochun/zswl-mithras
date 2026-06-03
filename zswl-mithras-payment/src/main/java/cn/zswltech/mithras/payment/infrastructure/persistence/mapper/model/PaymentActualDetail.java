package cn.zswltech.mithras.payment.infrastructure.persistence.mapper.model;

import cn.zswltech.mithras.payment.domain.enums.WriteOffStatus;
import cn.zswltech.mithras.payment.domain.enums.WriteOffTypeEnum;
import cn.zswltech.mithras.service.mapper.model.BaseModelWithLogicDelete;
import cn.zswltech.mithras.service.plugin.IncludeNull;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * @author zhaozhengkang
 * @description 实际付款记录表
 * @date 2022-08-16
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class PaymentActualDetail extends BaseModelWithLogicDelete implements Serializable {

    private static final long serialVersionUID = 1L;

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
     * 流水id
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
     * 操作日期
     */
    @TableField(value = "operation_date")
    private LocalDate operationDate;

    /**
     * 核销方式 自动核销，手工核销
     * {@link WriteOffTypeEnum#name()}
     **/
    @TableField(value = "write_off_type")
    private String writeOffType;

    /**
     * 资金流水ID
     */
    @TableField("finance_flow_id")
    private Long financeFlowId;

    /**
     * 收款对应银行流水号
     */
    @TableField(value = "bank_detail_no")
    private String bankDetailNo;

    /**
     * 关联un_confirmed表id
     */
    @TableField(value = "un_confirmed_id")
    private Long unConfirmedId;

    /**
     * 资金来源
     */
    @TableField(value = "capital_source")
    private String capitalSource;

    /**
     * 融资编号
     */
    @TableField(value = "financing_code")
    private String financingCode;

}
