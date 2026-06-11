package cn.zswltech.mithras.fund.mapper.model.financing;

import cn.zswltech.mithras.foundation.persistence.model.BaseModel;
import cn.zswltech.mithras.foundation.persistence.tag.IEntity;
import cn.zswltech.mithras.foundation.persistence.plugin.IncludeNull;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@TableName("fund_financing_early_settle_plan")
public class FundFinancingEarlySettlePlan extends BaseModel implements IEntity {
    /**
     * 主键id
     */
    @TableId(type = IdType.AUTO)
    @TableField("id")
    private Long id;
    /**
     * 融资id
     */
    @TableField("financing_id")
    private Long financingId;
    /**
     * 提前偿还金额
     */
    @TableField("early_repay_amount")
    private Long earlyRepayAmount;
    /**
     * 提前偿还本金金额
     */
    @TableField("early_principle_amount")
    private Long earlyPrincipleAmount;
    /**
     * 提前偿还利息金额
     */
    @TableField("early_interest_amount")
    private Long earlyInterestAmount;
    /**
     * 违约金金额
     */
    @IncludeNull
    @TableField("liquidated_damages_amount")
    private Long liquidatedDamagesAmount;
    /**
     * 其他费用金额
     */
    @IncludeNull
    @TableField("other_fee_amount")
    private Long otherFeeAmount;
    /**
     * 违约全减免金额
     */
    @IncludeNull
    @TableField("remission_amount")
    private Long remissionAmount;

    /**
     * 提前结清原因
     */
    @TableField("reason")
    private String reason;

    /**
     * 剩余本金
     */
    @TableField("last_principal")
    private Long lastPrincipal;

    @Override
    public void setMainId(Long id) {
        this.financingId = id;
    }

    @Override
    public Long getMainId() {
        return this.financingId;
    }
}