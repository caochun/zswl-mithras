package cn.zswltech.mithras.payment.model;

import cn.zswltech.mithras.payment.enums.PaymentStatusEnum;
import cn.zswltech.mithras.foundation.persistence.model.BaseModel;
import cn.zswltech.mithras.foundation.persistence.tag.IEntity;
import cn.zswltech.mithras.foundation.persistence.plugin.IncludeNull;
import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author zhaozhengkang
 * @description payment_base_info
 * @date 2022-08-12
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
public class PaymentBaseInfo extends BaseModel implements Serializable, IEntity {

    private static final long serialVersionUID = 1L;
    public  static final int DEFAULT_BEYOND_DAYS = -1;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 主键id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 付款申请编号
     */
    @TableField("payment_code")
    private String paymentCode;

    /**
     * 对应合同id
     */
    @TableField("contract_id")
    private Long contractId;

    /**
     * 合同编号
     */
    @TableField("contract_code")
    private String contractCode;

    /**
     * 借据id（预关联）
     */
    @IncludeNull
    @TableField(value = "receipt_id")
    private Long receiptId;

    /**
     * 借据编号
     */
    @IncludeNull
    @TableField(value = "receipt_code")
    private String receiptCode;

    /**
     * 借据id（审批通过的最终关联）
     */
    @IncludeNull
    @TableField("receipt_id_final")
    private Long receiptIdFinal;

    /**
     * 客户id
     */
    @TableField("client_id")
    private Long clientId;

    /**
     * 应付款项
     */
    private String payables;

    /**
     * 申请付款日期
     */
    @TableField("apply_payment_date")
    private LocalDateTime applyPaymentDate;

    /**
     * 申请付款金额
     */
    @TableField("apply_payment_amount")
    private Long applyPaymentAmount;

    /**
     * 保证金
     */
    @TableField(value = "earnest_money")
    private Long earnestMoney;

    /**
     * 币种
     */
    @TableField(value = "leased_currency")
    private String leasedCurrency;

    /**
     * 租赁财产价值
     */
    @TableField(value = "leased_price")
    private Long leasedPrice;

    /**
     * 质保金
     **/
    @TableField(value = "retention_money")
    private Long retentionMoney;

    /**
     * 质保金标识，0 内扣，1 不内扣
     **/
    @TableField(value = "retention_money_type")
    private Integer retentionMoneyType;

    /**
     * 首付款
     */
    @TableField(value = "down_payment")
    private Long downPayment;

    /**
     * 首付款标志，0不包括，1包括 不包括的自动核销
     */
    @TableField(value = "down_payment_type")
    private Integer downPaymentType;

    /**
     * 服务费/咨询费
     */
    @TableField(value = "consulting_fee")
    private Long consultingFee;

    /**
     * 手续费(元)
     */
    @TableField(value = "commission")
    private Long commission;

    /**
     * 首期利息(元)
     */
    @TableField(value = "first_installment_interest")
    private Long firstInstallmentInterest;

    /**
     * 备注说明
     **/
    @IncludeNull
    private String remark;

    @TableField(value = "nominal_price")
    private Long nominalPrice;

    /**
     * 最低irr
     **/
    @TableField(value = "lowest_irr")
    private Integer lowestIrr;

    /**
     * 默认收款日
     **/
    @TableField(value = "default_collection_day")
    private Integer defaultCollectionDay;

    @TableField("payment_process_status")
    private String paymentProcessStatus;

    /**
     * {@link PaymentStatusEnum#name()}
     */
    @TableField("payment_status")
    private String paymentStatus;

    @TableField("paid_in_date")
    private LocalDate paidInDate;

    @TableField("write_off_status")
    private String writeOffStatus;

    @TableField(value = "write_off_user_ids", updateStrategy = FieldStrategy.IGNORED)
    private String writeOffUserIds;

    /**
     * 付款记录数量，用于生成记录序号
     */
    @TableField("actual_detail_count")
    private Integer actualDetailCount;

    /**
     * 现金FTP价格
     * @deprecated 业务流程变化，使用{@link FtpAssessmentInfo#getAssessmentPrice()}替代该字段
     */
    @Deprecated
    @TableField("cash_ftp")
    private Integer cashFtp;

    /**
     * 现金FTP价格（最终确认值）
     * @deprecated 业务流程变化，使用{@link FtpAssessmentInfo#getAssessmentPrice()}替代该字段
     */
    @Deprecated
    @TableField("cash_ftp_final")
    private Integer cashFtpFinal;

    /**
     * 票据FTP价格
     * @deprecated 业务流程变化，使用{@link FtpAssessmentInfo#getTicketPrice()}替代该字段
     */
    @Deprecated
    @TableField("bill_ftp")
    private Integer billFtp;

    /**
     * 票据FTP价格（最终确认值）
     * @deprecated 业务流程变化，使用{@link FtpAssessmentInfo#getTicketPrice()}替代该字段
     */
    @Deprecated
    @TableField("bill_ftp_final")
    private Integer billFtpFinal;

    // 以下为冗余字段，每次detail操作时会更新
    @TableField("con_project_type")
    private String conProjectType;
    @TableField("con_apply_credit_amount")
    private Long conApplyCreditAmount;
    @TableField("con_biz_dept_id")
    private Long conBizDeptId;
    @TableField("con_biz_dept_leader_id")
    private Long conBizDeptLeaderId;
    @TableField("con_risk_control_manager_id")
    private Long conRiskControlManagerId;
    @TableField("con_biz_division_leader_id")
    private Long conBizDivisionLeaderId;
    /**
     * 0 隐藏勾选框/1 无需购买保险 / 2 尚未购买保险
     */
    @TableField("policy_flag")
    private Integer policyFlag;

    @TableField("financial_status")
    private Integer financialStatus;

    /**
     * 是否结束投放(页面选择)
     */
    @TableField("is_finish_put")
    private Boolean isFinishPut;

    /**
     * 是否结束投放(最终结果)
     */
    @TableField("is_finish_put_final")
    private Boolean isFinishPutFinal;

    /**
     * 超期天数
     */
    @TableField("beyond_days")
    private Integer beyondDays;

    /**
     * 运营提前审核状态，0-未审核，1-已审核
     */
    @TableField("yunying_review_state")
    private Integer yunyingReviewState;

    /**
     * 运营审核通过日期
     */
    @TableField("yunying_review_date")
    private LocalDate yunyingReviewDate;

    @TableField(exist = false)
    private Long bizDeptId;

    /**
     * 是否初始化公共信息
     */
    @TableField("is_init_public_info")
    private Integer isInitPublicInfo;

    /**
     * 是否同起租日
     */
    @TableField(value = "is_same_start_date")
    private Boolean sameStartDate;


    @Override
    public Long getMainId() {
        return getId();
    }

    @Override
    public void setMainId(Long id) {
        setId(id);
    }
}
