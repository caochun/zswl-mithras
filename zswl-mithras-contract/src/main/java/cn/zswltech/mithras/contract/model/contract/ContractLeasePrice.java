package cn.zswltech.mithras.contract.model.contract;

import cn.zswltech.mithras.projectprocess.enums.InterestWayEnum;
import cn.zswltech.mithras.foundation.persistence.model.BaseModel;
import cn.zswltech.mithras.foundation.persistence.tag.IEntity;
import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

/**
 * @author vico
 * @description 合同-租赁报价方案表
 * @date 2022-08-12
 */
@Data
public class ContractLeasePrice extends BaseModel implements Serializable, IEntity, ContractPrice {

    private static final long serialVersionUID = 1L;

    /**
     * 租赁报价方案id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
    * 所属合同id
    */
    @TableField("contract_id")
    private Long contractId;

    /**
    * 申报授信金额-合同金额
    */
    @TableField("apply_credit_amount")
    private Long applyCreditAmount;

    /**
     * 项目金额
     */
    @TableField("proj_credit_amount")
    private Long projCreditAmount;

    /**
    * 租赁期限月数
    */
    @TableField("lease_month_count")
    private Integer leaseMonthCount;

    /**
     * 项目租赁期限月数
     */
    @TableField("proj_lease_month_count")
    private Integer projLeaseMonthCount;

    /**
    * 还款频率。按月，按季，按年，不规则
    */
    @TableField("repay_rate")
    private String repayRate;

    /**
    * 还款期数
    */
    @TableField(value = "repay_times_total", updateStrategy = FieldStrategy.IGNORED)
    private Integer repayTimesTotal;

    /**
    * 支付方式。先付：advanced、后付：afterward
    */
    @TableField("pay_type")
    private String payType;

    /**
    * 租金计算方式
    */
    @TableField("rental_calc_type")
    private String rentalCalcType;

    /**
     * 利息计算方式{@link InterestWayEnum#name()}
     */
    @TableField("interest_way")
    private String interestWay;

    /**
    * 保证金
    */
    @TableField("earnest_money")
    private Long earnestMoney;

    /**
     * 项目保证金
     */
    @TableField("proj_earnest_money")
    private Long projEarnestMoney;

    /**
    * 首付款
    */
    @TableField("down_payment")
    private Long downPayment;

    /**
     * 项目首期租金
     */
    @TableField("proj_down_payment")
    private Long projDownPayment;

    /**
    * 服务费/咨询费
    */
    @TableField("consulting_fee")
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
     * 结构化利息
     */
    @TableField("structured_interest")
    private String structuredInterest;

    /**
     * 项目服务费/咨询费
     */
    @TableField("proj_consulting_fee")
    private Long projConsultingFee;

    /**
    * 名义货价
    */
    @TableField("nominal_price")
    private Long nominalPrice;

    /**
     * 项目租赁利率值。百分之多少
     */
    @TableField("lease_rate_percent")
    private Integer leaseRatePercent;

    /**
    * 租赁利率类型。固定利率：fixed、浮动利率：float
    */
    @TableField("rate_type")
    private String rateType;

    /**
    * lpr品种 一年期，五年期
    */
    @TableField("lpr_type")
    private String lprType;

    /**
    * lpr
    */
    @TableField("lpr_percent")
    private Integer lprPercent;

    /**
    * 加点
    */
    @TableField("lpr_add_percent")
    private Integer lprAddPercent;

    /**
    * 租前lpr
    */
    @TableField("before_lpr_percent")
    private Integer beforeLprPercent;

    /**
    * 租前加点
    */
    @TableField("before_lpr_add_percent")
    private Integer beforeLprAddPercent;


    @TableField("before_rate_type")
    private String beforeRateType;

    /**
     * lpr品种 一年期，五年期
     */
    @TableField("before_lpr_type")
    private String beforeLprType;

    /**
    * 罚息日利率
    */
    @TableField("default_interest_rate")
    private Integer defaultInterestRate;

    /**
    * 内部收益率。百分之多少
    */
    @TableField("irr_percent")
    private Integer irrPercent;

    /**
     * 项目内部收益率
     */
    @TableField("proj_irr_percent")
    private Integer projIrrPercent;

    /**
    * 额度是否可循环
    */
    @TableField("credit_amount_loop")
    private Integer creditAmountLoop;

    @Override
    public void setMainId(Long id) {
        this.contractId = id;
    }

    @Override
    public Long getMainId() {
        return contractId;
    }

    @Override
    public Long getContractAmount() {
        return applyCreditAmount;
    }
}
