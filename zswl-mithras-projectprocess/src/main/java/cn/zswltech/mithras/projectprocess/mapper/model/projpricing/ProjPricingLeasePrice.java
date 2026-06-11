package cn.zswltech.mithras.projectprocess.mapper.model.projpricing;

import cn.zswltech.mithras.foundation.persistence.model.BaseModel;
import cn.zswltech.mithras.foundation.persistence.tag.IEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * <p>
 * 项目定价-租赁报价方案表
 * </p>
 *
 * @author chenyifei
 * @since 2024-08-16
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("proj_pricing_lease_price")
public class ProjPricingLeasePrice extends BaseModel implements Serializable, IEntity {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 所属项目ID
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

    /**
     * 租赁期限月数
     */
    @TableField("lease_month_count")
    private Integer leaseMonthCount;

    /**
     * 每年还款次数
     */
    @TableField("repay_times_yearly")
    private Integer repayTimesYearly;

    /**
     * 还款期数
     */
    @TableField("repay_times_total")
    private Integer repayTimesTotal;

    /**
     * 还款频率
     */
    @TableField("repay_rate")
    private String repayRate;

    /**
     * 支付方式。先付：advanced、后付：afterward	
     */
    @TableField("pay_type")
    private String payType;

    /**
     * 租金计算方式。等额租金：equivalent_rental、等额本金：equivalent_capital、平息法：pacification、不规则还款：irregular_repay

其他：other
     */
    @TableField("rental_calc_type")
    private String rentalCalcType;

    /**
     * 利息计算方式
     */
    @TableField("interest_way")
    private String interestWay;

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
     * 手续费
     */
    @TableField("commission")
    private Long commission;

    /**
     * 首期利息(元)
     */
    @TableField("first_installment_interest")
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

    /**
     * 创建人id、发起人id	
     */
    @TableField("create_by")
    private Long createBy;

    /**
     * 创建时间。默认当前时间
     */
    @TableField("create_time")
    private LocalDateTime createTime;

    /**
     * 最后更新人id	
     */
    @TableField("update_by")
    private Long updateBy;

    /**
     * 更新时间；每次记录变化，自动更新为当前时间	
     */
    @TableField("update_time")
    private LocalDateTime updateTime;


    @Override
    public void setMainId(Long id) {
        setId(id);
    }

    @Override
    public Long getMainId() {
        return getId();
    }
}
