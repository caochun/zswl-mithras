package cn.zswltech.mithras.service.mapper.model.projreview;

import cn.zswltech.mithras.service.enums.InterestWayEnum;
import cn.zswltech.mithras.common.model.BaseModel;
import cn.zswltech.mithras.common.model.IEntity;
import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * @author zhaozhengkang
 * @description 债权转让报价方案
 * @date 2022-08-01
 */
@Data
public class ProjReviewAocPrice extends BaseModel implements Serializable, IEntity {

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
     * 转让额度有效期（月
     */
    @TableField("aoc_credit_term")
    private Integer aocCreditTerm;

    /**
     * 还款方式。间接还款：indirect、直接还款：direct
     */
    @TableField("repay_type")
    private String repayType;

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
     * 确权方
     */
    @TableField(value = "confirmatory_party", updateStrategy = FieldStrategy.IGNORED)
    private String confirmatoryParty;

    /**
     * 服务费/咨询费
     */
    @TableField("consulting_fee")
    private Long consultingFee;

    /**
     * 转让融资比例
     */
    @TableField("aoc_financing_proportion")
    private Integer aocFinancingProportion;

    /**
     * 拟转应收账款概述
     */
    @TableField("summary")
    private String summary;

    /**
     * 转让费率类型。固定利率：fixed、浮动利率：float
     */
    @TableField("rate_type")
    private String rateType;

    /**
     * 转让费率值。百分之多少
     */
    @TableField("aoc_rate_percent")
    private Integer aocRatePercent;

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
     * 租金计算方式。等额租金：equivalent_rental、等额本金：equivalent_capital、平息法：pacification、不规则还款：irregular_repaynn其他：other
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
        return this.getProjectId();
    }
}
