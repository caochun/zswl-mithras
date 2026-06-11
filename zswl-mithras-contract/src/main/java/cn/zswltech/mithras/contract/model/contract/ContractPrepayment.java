package cn.zswltech.mithras.contract.model.contract;

import cn.zswltech.mithras.contract.enums.DerateTypeEnum;
import cn.zswltech.mithras.foundation.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.foundation.persistence.model.BaseModel;
import cn.zswltech.mithras.foundation.persistence.tag.IEntity;
import cn.zswltech.mithras.foundation.persistence.plugin.IncludeNull;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.time.LocalDate;

/**
 * @ClassName Prepayment
 * @Description 提前付款表
 * @Author jackerhe
 * @Date 2022/8/25 11:19 上午
 * @Version 1.0
 **/
@Data
public class ContractPrepayment extends BaseModel implements IEntity {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 所属合同id
     */
    @TableField("contract_id")
    private Long contractId;

    /**
     * 是否提前结清 {@link YesOrNoNumberEnum#getCode()}
     */
    @TableField("is_early_settle")
    private Integer isEarlySettle;

    // 保证金余额
    @TableField("earnest_money_balance")
    private Long earnestMoneyBalance;

    /**
     * 保证金是否抵扣 {@link YesOrNoNumberEnum#getCode()}
     */
    @TableField("is_earnest_money_deduction")
    private Integer isEarnestMoneyDeduction;

    /**
     * 保证金抵扣金额
     */
    @TableField("earnest_money_deduction_amount")
    private Long earnestMoneyDeductionAmount;

    //提前还款日期
    @TableField(value = "applay_repayment_date")
    private LocalDate applayRepaymentDate;

    //到期未付租金
    @TableField("unpaid_rent_due")
    private Long unpaidRentDue;

    // 未到期本金
    @TableField("before_maturity_principal")
    private Long beforeMaturityPrincipal;

    // 未到期利息
    @TableField("before_maturity_interest")
    private Long beforeMaturityInterest;

    //违约金
    @TableField("penalty")
    @IncludeNull
    private Long penalty;

    /**
     * 违约金减免方式 {@link DerateTypeEnum#name()}
     */
    @TableField("penalty_derate_type")
    private String penaltyDerateType;

    /**
     * 违约金减免百分比
     */
    @IncludeNull
    @TableField("penalty_derate_percent")
    private Integer penaltyDeratePercent;

    /**
     * 违约金减免金额
     */
    @TableField("penalty_derate_amount")
    private Long penaltyDerateAmount;

    //提前归还本金
    @TableField("early_repayment")
    private Long earlyRepayment;

    //提前归还利息
    @TableField("early_repayment_interest")
    @IncludeNull
    private Long earlyRepaymentInterest;

    //提前终止补偿金
    @TableField("loss")
    @IncludeNull
    private Long loss;

    /**
     * 提前终止补偿金减免方式 {@link DerateTypeEnum#name()}
     */
    @TableField("loss_derate_type")
    private String lossDerateType;

    /**
     * 提前终止补偿金减免百分比
     */
    @IncludeNull
    @TableField("loss_derate_percent")
    private Integer lossDeratePercent;

    /**
     * 提前终止补偿金减免金额
     */
    @TableField("apply_derate_amount")
    @IncludeNull
    private Long applyDerateAmount;

    /**
     * 名义货价
     */
    @TableField("nominal_price")
    private Long nominalPrice;

    /**
     * 名义货价还款时间
     * @deprecated 字段废弃
     */
    @Deprecated
    @TableField("nominal_price_date")
    private LocalDate nominalPriceDate;

    /**
     * 备注说明
     */
    @TableField("remark")
    private String remark;

    @Override
    public void setMainId(Long id) {
        this.contractId = id;
    }

    @Override
    public Long getMainId() {
        return contractId;
    }


}
