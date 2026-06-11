package cn.zswltech.mithras.projectprocess.model.projreview;

import cn.zswltech.mithras.projectprocess.enums.InterestWayEnum;
import cn.zswltech.mithras.foundation.persistence.model.BaseModel;
import cn.zswltech.mithras.foundation.persistence.tag.IEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @author zhaozhengkang
 * @description 保理报价方案
 * @date 2022-08-01
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ProjReviewFactoringPrice extends BaseModel implements Serializable, IEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 立项基本信息表id
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
     * 保理额度有效期（月
     */
    @TableField("factoring_credit_term")
    private Integer factoringCreditTerm;

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
     * 保理融资比例
     */
    @TableField("factoring_financing_proportion")
    private Integer factoringFinancingProportion;

    /**
     * 手续费
     */
    @TableField("consulting_fee")
    private Long consultingFee;

    /**
     * 保理费率类型。固定利率：fixed、浮动利率：float
     */
    @TableField("rate_type")
    private String rateType;

    /**
     * 保理费率值。百分之多少
     */
    @TableField("factoring_rate_percent")
    private Integer factoringRatePercent;

    /**
     * 内部收益率。百分之多少
     */
    @TableField("irr_percent")
    private Integer irrPercent;

    /**
     * 还款计算方式
     */
    @TableField("rental_calc_type")
    private String rentalCalcType;

    /**
     * 利息方式 {@link InterestWayEnum#name()}
     */
    @TableField("interest_way")
    private String interestWay;

    /**
     * 还款频率。按月，按季，按年，不规则
     */
    @TableField("repay_rate")
    private String repayRate;

    @Override
    public void setMainId(Long id) {
        this.projectId = id;
    }

    @Override
    public Long getMainId() {
        return this.projectId;
    }
}
