package cn.zswltech.mithras.service.mapper.model.collection;

import cn.zswltech.mithras.service.enums.CashFlowItemEnum;
import cn.zswltech.mithras.common.model.BaseModel;
import cn.zswltech.mithras.common.annotation.IncludeNull;
import cn.zswltech.mithras.service.util.LongUtil;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Id;
import java.io.Serializable;
import java.time.LocalDate;

/**
 * 收款明细表
 * collection_base_info
 */
@EqualsAndHashCode(callSuper = true)
@TableName(value = "collection_base_info")
@Data
public class CollectionBaseInfo extends BaseModel implements Serializable {
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
    /**
     * 收款明细id
     */
    @Id
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 合同id
     */
    private Long contractId;
    /**
     *借据ID
     **/
    @TableField("receipt_id")
    private Long receiptId;

    /**
     *借据编号 租金关联借据
     **/
    @TableField("receipt_code")
    private String receiptCode;


    /**
     * 付款id
     */
    private Long paymentId;


    /**
     * 付款code 除租金外其他类型关联本次付款
     */
    private String paymentCode;


    /**
     * 期项
     */
    private Integer phase;
    /**
     * 合同编号
     */
    private String contractCode;
    /**
     * 客户id
     */
    private Long clientId;
    /**
     * 收款编号
     */
    private String code;
    /**
     * 核销状态
     */
    private String writeOffStatus;
    /**
     * 实收日期
     */
    @TableField(value = "collection_date", updateStrategy = FieldStrategy.IGNORED)
    private LocalDate collectionDate;
    /**
     * 实收金额
     */
    private Long collectionAmount;
    /**
     * 本金
     */
    private Long principal;
    /**
     * 剩余本金-借据中剩余本金
     */
    @TableField(value = "receipt_remaining_principal", updateStrategy = FieldStrategy.IGNORED)
    private Long receiptRemainingPrincipal;
    /**
     * 利息
     */
    private Long interest;
    /**
     * 累计罚息
     */
    private Long penaltyInterest;
    /**
     * 罚息减免金额
     */
    @TableField("penalty_interest_deduction_amount")
    private Long penaltyInterestDeductionAmount;
    /**
     * 计划罚息收款日期
     */
    private LocalDate planPenaltyInterestDate;
    /**
     * 计划收款金额
     */
    private Long planCollectionAmount;
    /**
     * 计划收款日期
     */
    private LocalDate planCollectionDate;
    /**
     * 现金流项目
     */
    private String cashFlowItem;
    /**
     * 现金流金额
     */
    private Long cashFlowAmount;
    /**
     * 实收本金
     */
    private Long collectionPrincipal;
    /**
     * 实收利息
     */
    private Long collectionInterest;
    /**
     * 实收罚息
     */
    private Long collectionPenaltyInterest;
    /**
     * 罚息修改备注
     */
    @IncludeNull
    private String comment;
    /**
     * 实际租金表id
     */
    private Long rentActualId;
    @TableField(value = "write_off_user_ids", updateStrategy = FieldStrategy.IGNORED)
    private String writeOffUserIds;
    /**
     * 罚息是否修改
     */
    private Integer penaltyInterestUpdate;
    /**
     * 序号最大值
     */
    private Integer allRecordSort;

    /**
     * 是否通知过苍穹（1是0否）不再使用
     */
   /* @TableField(value = "notice_financial_flag")
    private Integer noticeFinancialFlag;*/
    /**
     * 租金催收邮件次数
     */
    @TableField(value = "email_notice_count")
    private Integer emailNoticeCount;
    /**
     * 催收次数 -- 不再催收后，表示减免次数
     **/
    @TableField("overdue_collection_count")
    private Long overdueCollectionCount;

    /**
     * 罚息计算状态 0 计算，1 不再计算
     **/
    @TableField("penalty_interest_calculate_flag")
    private int penaltyInterestCalculateFlag;

    /**
     * 0 已同步财务系统， 1撤回
     */
   /* @TableField("financial_status")
    private Integer financialStatus;*/

    /**
     * 保证金退抵锁 1：锁定， 0：解锁
     */
    @TableField("retreat_lock")
    private String retreatLock;

    /**
     * 获取剩余本金+利息
     *
     * @return
     */
    public Long getRemainingPrincipalInterest() {
        Long planed = LongUtil.null2zero(this.principal) + LongUtil.null2zero(this.interest);
        Long actual = LongUtil.null2zero(this.collectionPrincipal) + LongUtil.null2zero(this.collectionInterest);
        return planed - actual;
    }

    /**
     * 判断该收款信息是否有逾期行为
     * 逾期已还的也算
     *
     * @return
     */
    public boolean overdued() {
        // A情况：已收款 且 收款时间 > 计划收款时间
        boolean a = collectionDate != null &&
                collectionDate.isAfter(planCollectionDate);

        // B情况：未收到款 且 计划收款时间 < 当前时间
        boolean b = collectionDate == null &&
                planCollectionDate.isBefore(LocalDate.now());

        // C已收到款 但是收款金额小于计划收款金额
        boolean c = collectionDate != null &&
                collectionAmount < planCollectionAmount;

        return a || b || c;
    }

    /**
     * 是否正在逾期
     *
     * @return
     */
    public boolean overdueing() {
        // 已收到款 但是收款金额小于计划收款金额
        boolean a = collectionDate != null &&
                collectionAmount < planCollectionAmount;

        //未收到款 且 计划收款时间 < 当前时间
        boolean b = collectionDate == null &&
                planCollectionDate.isBefore(LocalDate.now());

        return a || b;
    }


    public Long getReceipt() {
        if (CashFlowItemEnum.RENT.name().equals(cashFlowItem)) {
            return Math.min(LongUtil.null2zero(collectionPrincipal), LongUtil.null2zero(principal));
        }
        return 0L;
    }
}