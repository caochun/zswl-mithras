package cn.zswltech.mithras.service.mapper.model.projpricing;

import cn.zswltech.mithras.common.model.BaseModel;
import cn.zswltech.mithras.common.model.IEntity;
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
 * 项目定价-保理报价方案表
 * </p>
 *
 * @author chenyifei
 * @since 2024-08-16
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("proj_pricing_factoring_price")
public class ProjPricingFactoringPrice extends BaseModel implements Serializable, IEntity {

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
     * 保理额度有效期（月
     */
    @TableField("factoring_credit_term")
    private Integer factoringCreditTerm;

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
     * 保理融资比例
     */
    @TableField("factoring_financing_proportion")
    private Integer factoringFinancingProportion;

    /**
     * 服务费/咨询费
     */
    @TableField("consulting_fee")
    private Long consultingFee;

    /**
     * 拟转应收账款概述
     */
    @TableField("summary")
    private String summary;

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
     * 是否有存续租赁授信
     */
    @TableField("existing_lease_credit")
    private Boolean existingLeaseCredit;

    /**
     * 是否新增租赁授信
     */
    @TableField("new_lease_credit")
    private Boolean newLeaseCredit;

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

    @TableField("create_by")
    private Long createBy;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("update_by")
    private Long updateBy;

    @TableField("update_time")
    private LocalDateTime updateTime;

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
