package cn.zswltech.mithras.service.mapper.model.projestablish;

import cn.zswltech.mithras.service.enums.InterestWayEnum;
import cn.zswltech.mithras.service.enums.projestablish.RepayCalcType;
import cn.zswltech.mithras.common.model.BaseModel;
import cn.zswltech.mithras.common.model.IEntity;
import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @author zhaozhengkang
 * @description 保理报价方案表
 * @date 2022-07-20
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ProjEstablishFactoringPrice extends BaseModel implements Serializable, IEntity {

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
    @TableField("apply_credit_amount")
    private Long applyCreditAmount;

    /**
     * 保理额度有效期（月
     */
    @TableField(value = "factoring_credit_term", updateStrategy = FieldStrategy.IGNORED)
    private Integer factoringCreditTerm;

    /**
     * 还款方式。间接还款：indirect、直接还款：direct
     * @deprecated 字段作废
     */
    @Deprecated
    @TableField(value = "repay_type", updateStrategy = FieldStrategy.IGNORED)
    private String repayType;

    /**
     * 额度是否可循环
     */
    @TableField(value = "credit_amount_loop", updateStrategy = FieldStrategy.IGNORED)
    private Integer creditAmountLoop;

    /**
     * 保证金
     */
    @TableField(value = "earnest_money", updateStrategy = FieldStrategy.IGNORED)
    private Long earnestMoney;

    /**
     * 确权方
     * @deprecated 字段作废
     */
    @Deprecated
    @TableField(value = "confirmatory_party", updateStrategy = FieldStrategy.IGNORED)
    private String confirmatoryParty;

    /**
     * 手续费
     */
    @TableField(value = "consulting_fee", updateStrategy = FieldStrategy.IGNORED)
    private Long consultingFee;

    /**
     * 保理融资比例
     */
    @TableField(value = "factoring_financing_proportion", updateStrategy = FieldStrategy.IGNORED)
    private Integer factoringFinancingProportion;

    /**
     * 拟转应收账款概述
     * @deprecated 字段作废
     */
    @Deprecated
    @TableField(value = "summary", updateStrategy = FieldStrategy.IGNORED)
    private String summary;

    /**
     * 保理费率类型。固定利率：fixed、浮动利率：float
     */
    @TableField(value = "rate_type", updateStrategy = FieldStrategy.IGNORED)
    private String rateType;

    /**
     * 保理费率值。百分之多少
     */
    @TableField(value = "factoring_rate_percent", updateStrategy = FieldStrategy.IGNORED)
    private Integer factoringRatePercent;

    /**
     * 内部收益率。百分之多少
     */
    @TableField(value = "irr_percent", updateStrategy = FieldStrategy.IGNORED)
    private Integer irrPercent;

    /**
     * 还款计算方式。{@link RepayCalcType#name()}
     */
    @TableField(value = "rental_calc_type", updateStrategy = FieldStrategy.IGNORED)
    private String rentalCalcType;

    /**
     * 利息计算方式{@link InterestWayEnum#name()}
     */
    @TableField(value = "interest_way")
    private String interestWay;

    /**
     * @deprecated 字段作废
     */
    @Deprecated
    @TableField(value = "existing_lease_credit", updateStrategy = FieldStrategy.IGNORED)
    private Boolean existingLeaseCredit;

    /**
     * 是否新增租赁授信
     * @deprecated 字段作废
     */
    @Deprecated
    @TableField(value = "new_lease_credit", updateStrategy = FieldStrategy.IGNORED)
    private Boolean newLeaseCredit;

    /**
     * 还款频率。按月，按季，按年，不规则
     */
    @TableField("repay_rate")
    private String repayRate;

    @Override
    public void setMainId(Long id) {
        setProjEstablishId(id);
    }

    @Override
    public Long getMainId() {
        return getProjEstablishId();
    }

}
