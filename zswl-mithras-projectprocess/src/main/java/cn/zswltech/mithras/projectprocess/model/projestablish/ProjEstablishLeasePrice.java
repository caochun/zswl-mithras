package cn.zswltech.mithras.projectprocess.model.projestablish;

import cn.zswltech.mithras.projectprocess.enums.InterestWayEnum;
import cn.zswltech.mithras.projectprocess.enums.projestablish.RepayCalcType;
import cn.zswltech.mithras.foundation.persistence.model.BaseModel;
import cn.zswltech.mithras.foundation.persistence.tag.IEntity;
import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

/**
 * @description 租赁报价方案表
 * @author zhaozhengkang
 * @date 2022-07-20
 */
@Data
public class ProjEstablishLeasePrice extends BaseModel implements Serializable, IEntity {

    private static final long serialVersionUID = 1L;

    /**
    * 立项基本信息表id
    */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
    * 所属立项id
    */
    @TableField("proj_establish_id")
    private Long projEstablishId;

    /**
    * 申报授信金额
    */
    @TableField(value = "apply_credit_amount")
    private Long applyCreditAmount;

    /**
    * 租赁期限月数
    */
    @TableField(value = "lease_month_count",updateStrategy = FieldStrategy.IGNORED)
    private Integer leaseMonthCount;

    /**
    * 每年还款次数
    */
    @TableField(value = "repay_times_yearly",updateStrategy = FieldStrategy.IGNORED)
    private Integer repayTimesYearly;

    /**
    * 还款期数
    */
    @TableField(value = "repay_times_total",updateStrategy = FieldStrategy.IGNORED)
    private Integer repayTimesTotal;

    /**
     * 还款频率
     */
    @TableField(value = "repay_rate",updateStrategy = FieldStrategy.IGNORED)
    private String repayRate;


    /**
    * 支付方式。先付：advanced、后付：afterward
    */
    @TableField(value = "pay_type",updateStrategy = FieldStrategy.IGNORED)
    private String payType;

    /**
    * 租金计算方式。{@link RepayCalcType#name()}
    */
    @TableField(value = "rental_calc_type",updateStrategy = FieldStrategy.IGNORED)
    private String rentalCalcType;

    /**
     * 利息计算方式{@link InterestWayEnum#name()}
     */
    @TableField(value = "interest_way")
    private String interestWay;

    /**
    * 额度是否可循环
    */
    @TableField(value = "credit_amount_loop",updateStrategy = FieldStrategy.IGNORED)
    private Integer creditAmountLoop;

    /**
    * 保证金
    */
    @TableField(value = "earnest_money",updateStrategy = FieldStrategy.IGNORED)
    private Long earnestMoney;

    /**
    * 首付款
    */
    @TableField(value = "down_payment",updateStrategy = FieldStrategy.IGNORED)
    private Long downPayment;

    /**
    * 服务费/咨询费
    */
    @TableField(value = "consulting_fee",updateStrategy = FieldStrategy.IGNORED)
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
    @TableField(value = "nominal_price",updateStrategy = FieldStrategy.IGNORED)
    private Long nominalPrice;

    /**
    * 租赁利率类型。固定利率：fixed、浮动利率：float
    */
    @TableField(value = "rate_type",updateStrategy = FieldStrategy.IGNORED)
    private String rateType;

    /**
    * 租赁利率值。百分之多少
    */
    @TableField(value = "lease_rate_percent",updateStrategy = FieldStrategy.IGNORED)
    private Integer leaseRatePercent;

    /**
    * 内部收益率。百分之多少
    */
    @TableField(value = "irr_percent",updateStrategy = FieldStrategy.IGNORED)
    private Integer irrPercent;


    @Override
    public void setMainId(Long id) {
        setProjEstablishId(id);
    }

    @Override
    public Long getMainId() {
        return getProjEstablishId();
    }

}
