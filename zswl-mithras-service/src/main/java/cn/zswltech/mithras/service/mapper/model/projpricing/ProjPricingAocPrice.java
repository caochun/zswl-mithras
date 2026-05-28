package cn.zswltech.mithras.service.mapper.model.projpricing;

import cn.zswltech.mithras.service.mapper.model.BaseModel;
import cn.zswltech.mithras.service.mapper.tag.IEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * <p>
 * 项目定价-债权转让报价方案表
 * </p>
 *
 * @author chenyifei
 * @since 2024-08-16
 */
@Data
@TableName("proj_pricing_aoc_price")
public class ProjPricingAocPrice extends BaseModel implements Serializable, IEntity {

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
    @TableField("confirmatory_party")
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
     * 还款频率
     */
    @TableField("repay_rate")
    private String repayRate;


    @Override
    public void setMainId(Long id) {
        setId(id);
    }

    @Override
    public Long getMainId() {
        return getId();
    }
}
