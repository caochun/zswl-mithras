package cn.zswltech.mithras.projectprocess.mapper.model.projreview;

import cn.zswltech.mithras.projectprocess.enums.InterestWayEnum;
import cn.zswltech.mithras.projectprocess.enums.projestablish.RepayCalcType;
import cn.zswltech.mithras.service.mapper.model.BaseModel;
import cn.zswltech.mithras.service.mapper.tag.IEntity;
import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * @author zhaozhengkang
 * @description 租赁报价方案
 * @date 2022-08-01
 */
@Data
public class ProjReviewLeasePrice extends BaseModel implements Serializable, IEntity {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 所属项目id
     */
    @TableField("project_id")
    private Long projectId;

    /**
     * 申报授信金额
     */
    @TableField("apply_credit_amount")
    private Long applyCreditAmount;

    /**
     * 项目批复金额
     */
    @TableField("project_approval_amount")
    private Long projectApprovalAmount;

    @TableField(exist = false)
    private Long approvedAmount;

    /**
     * 租赁期限月数
     */
    @TableField("lease_month_count")
    private Integer leaseMonthCount;

    /**
     * 每年还款次数
     */
    @TableField(value = "repay_times_yearly", updateStrategy = FieldStrategy.IGNORED)
    private Integer repayTimesYearly;

    @TableField(value = "repay_rate", updateStrategy = FieldStrategy.IGNORED)
    private String repayRate;

    /**
     * 还款期数
     */
    @TableField("repay_times_total")
    private Integer repayTimesTotal;

    /**
     * 支付方式。先付：advanced、后付：afterward
     */
    @TableField("pay_type")
    private String payType;

    /**
     * 利息方式 {@link InterestWayEnum#name()}
     */
    @TableField("interest_way")
    private String interestWay;

    /**
     * 租金计算方式 {@link RepayCalcType#name()}
     */
    @TableField("rental_calc_type")
    private String rentalCalcType;

    /**
     * 额度是否可循环
     */
    @TableField("credit_amount_loop")
    private Integer creditAmountLoop;

    /**
     * 保证金
     */
    @TableField("earnest_money")
    private Long earnestMoney;

    /**
     * 首付款
     */
    @TableField("down_payment")
    private Long downPayment;

    /**
     * 服务费/咨询费
     */
    @TableField("consulting_fee")
    private Long consultingFee;

    /**
     * 手续费(元)
     */
    @TableField(value = "commission",updateStrategy = FieldStrategy.IGNORED)
    private Long commission;

    /**
     * 首期利息(元)
     */
    @TableField(value = "first_installment_interest",updateStrategy = FieldStrategy.IGNORED)
    private Long firstInstallmentInterest;

    /**
     * 名义货价
     */
    @TableField("nominal_price")
    private Long nominalPrice;

    /**
     * 租赁利率类型。固定利率：fixed、浮动利率：float
     */
    @TableField("rate_type")
    private String rateType;

    /**
     * 租赁利率值。百分之多少
     */
    @TableField("lease_rate_percent")
    private Integer leaseRatePercent;

    /**
     * 内部收益率。百分之多少
     */
    @TableField("irr_percent")
    private Integer irrPercent;

    /**
     * 计划起租日
     */
    @TableField("planned_starting_date")
    private LocalDate plannedStartingDate;

    @Override
    public void setMainId(Long id) {
        this.projectId = id;
    }

    @Override
    public Long getMainId() {
        return this.projectId;
    }
}
