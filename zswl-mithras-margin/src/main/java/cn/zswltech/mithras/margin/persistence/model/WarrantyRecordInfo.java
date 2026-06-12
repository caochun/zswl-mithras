package cn.zswltech.mithras.margin.persistence.model;

import cn.zswltech.mithras.foundation.persistence.model.BaseModelWithLogicDelete;
import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * 质保金核销记录明细表
 * @TableName warranty_record_info
 */
@EqualsAndHashCode(callSuper = true)
@TableName(value ="warranty_record_info")
@Data
public class WarrantyRecordInfo extends BaseModelWithLogicDelete implements Serializable {
    /**
     * 质保金核销记录明细id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 质保金明细id
     */
    private Long warrantyId;

    /**
     * 信息来源
     */
    private String dataSource;

    /**
     * 来源标志：0：财务系统 1：用户新增
     */
    private Integer sourceFlag;

    /**
     * 收款类型
     */
    private String collectionType;

    /**
     * 记录类型：退款 or 收款
     */
    private String recordType;

    /**
     * 实收or付日期
     */
    private LocalDate collectionDate;

    /**
     * 实收or付金额
     */
    private Long collectionAmount;

    /**
     * 抵扣本金
     */
    private Long deductPrincipal;

    /**
     * 抵扣利息
     */
    private Long deductInterest;

    /**
     * 抵扣罚息
     */
    private Long deductPenaltyInterest;

    /**
     * 抵扣租金
     */
    private Long deductRent;

    /**
     * 抵扣期项
     */
    private Integer deductTerm;

    /**
     *租金现金流编号 抵扣现金流编号
     **/
    private String rentCollectionCode;

    /**
     * 收款核销id
     */
    private Long collectionId;

    /**
     * 附言
     */
    private String postscript;

    /**
     * 附件id
     */
    private Long enclosureId;

    /**
     * 附件名
     */
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
     * 对方账户名
     */
    private String otherAccountName;

    /**
     * 对方银行账号
     */
    private String otherAccountNumber;

    /**
     * 对方开户行
     */
    private String otherAccountBank;

    /**
     * 序号
     */
    private String sortId;

    /**
     * 核销
     */
    private String writeOff;

    /**
     * 复核
     */
    private String review;

    /**
     * 核销人
     */
    @TableField(value = "write_off_user", updateStrategy = FieldStrategy.IGNORED)
    private Long writeOffUser;

    /**
     * 复核人
     */
    @TableField(value = "review_user", updateStrategy = FieldStrategy.IGNORED)
    private Long reviewUser;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    /**
     *是否开票 0不开 1开
     **/
    private Integer invoiceFlag;

    /**
     *流水号
     **/
    private String flowId;

    /**
     * 资金流水ID
     */
    @TableField("finance_flow_id")
    private Long financeFlowId;

    @TableField("bank_detail_no")
    private String bankDetailNo;

}
