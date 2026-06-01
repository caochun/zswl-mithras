package cn.zswltech.mithras.service.mapper.model.collection;

import cn.zswltech.mithras.service.enums.payment.WriteOffTypeEnum;
import cn.zswltech.mithras.service.mapper.model.BaseModelWithLogicDelete;
import cn.zswltech.mithras.service.plugin.IncludeNull;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * 收款记录明细表
 * @TableName collection_record_info
 */
@TableName(value ="collection_record_info")
@Data
public class CollectionRecordInfo extends BaseModelWithLogicDelete implements Serializable {
    /**
     * 收款记录明细id	
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 收款核销明细id
     */
    private Long collectionId;

    /**
     * 信息来源
     */
    private String dataSource;

    /**
     * 收款类型
     */
    private String collectionType;

    /**
     * 实收日期
     */
    private LocalDate collectionDate;

    /**
     * 来源标志：0：财务系统 1：用户新增
     */
    private Integer sourceFlag;

    /**
     * 实收金额
     */
    private Long collectionAmount;

    /**
     * 本金
     */
    private Long principal;

    /**
     * 利息
     */
    private Long interest;

    /**
     * 罚息
     */
    private Long penaltyInterest;

    /**
     * 附言
     */
    @TableField(value = "postscript", updateStrategy = FieldStrategy.IGNORED)
    @IncludeNull
    private String postscript;

    /**
     * 附件id
     */
    @TableField(value = "enclosure_id", updateStrategy = FieldStrategy.IGNORED)
    @IncludeNull
    private Long enclosureId;

    /**
     * 附件名
     */
    @TableField(value = "enclosure_name", updateStrategy = FieldStrategy.IGNORED)
    @IncludeNull
    private String enclosureName;

    /**
     * 核销状态
     */
    private String writeOffStatus;

    /**
     * 我方账户ID
     */
    private Long ourAccountId;

    /**
     * 我方账户名
     */
    private String ourAccountName;

    /**
     * 我方银行账号
     */
    private String ourAccountNumber;

    /**
     * 我方开户行
     */
    private String ourAccountBank;

    /**
     * 序号
     */
    private Integer sortId;

    /**
     *是否开票 0不开 1开
     **/
    @TableField(value = "invoice_flag")
    private String invoiceFlag;

    /**
     *流水id
     **/
    private String flowId;

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

    //抵扣保证金id
    @TableField(value = "deduction_margin_base_id")
    private Long deductionMarginBaseId;
    //抵扣保证金编号
    @TableField(value = "deduction_margin_base_code")
    private String deductionMarginBaseCode;


    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}