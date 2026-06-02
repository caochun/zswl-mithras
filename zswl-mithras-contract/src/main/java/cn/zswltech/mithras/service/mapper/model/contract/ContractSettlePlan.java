package cn.zswltech.mithras.service.mapper.model.contract;

import cn.zswltech.mithras.service.enums.contract.ContractSettlePlanTypeEnum;
import cn.zswltech.mithras.service.mapper.model.BaseModel;
import cn.zswltech.mithras.service.mapper.tag.IEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * @author dingqi
 * @date 2022/8/24
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("contract_settle_plan")
public class ContractSettlePlan extends BaseModel implements Serializable, IEntity {
    private static final long serialVersionUID = 7614337969661067791L;

    /**
     * 主键id
     */
    @TableId(type = IdType.AUTO)
    @TableField("id")
    private Long id;

    /**
     * 主合同id
     */
    @TableField("contract_id")
    private Long contractId;

    /**
     * 结清类型
     * @see ContractSettlePlanTypeEnum#name()
     */
    @TableField("settle_type")
    private String settleType;

    /**
     * 到期未付租金
     */
    @TableField("outstanding_rent")
    private Long outstandingRent;

    /**
     * 未到期本金
     */
    @TableField("before_maturity_principal")
    private Long beforeMaturityPrincipal;

    /**
     * 未到期利息
     */
    @TableField("before_maturity_interest")
    private Long beforeMaturityInterest;

    /**
     * 损失金
     */
    @TableField("loss")
    private Long loss;

    /**
     * 违约金
     */
    @TableField("liquidated_damages")
    private Long liquidatedDamages;

    /**
     * 保证金余额
     */
    @TableField("earnest_balance")
    private Long earnestBalance;

    /**
     * 保证金是否内扣
     */
    @TableField("is_earnest_deduction")
    private Integer isEarnestDeduction;

    /**
     * 名义货价
     */
    @TableField("nominal_price")
    private Long nominalPrice;

    /**
     * 申请减免金额
     */
    @TableField("apply_derate_amount")
    private Long applyDerateAmount;

    /**
     * 原到期日
     */
    @TableField("original_deadline")
    private LocalDate originalDeadline;

    /**
     * 申请结清日期
     */
    @TableField("apply_settle_date")
    private LocalDate applySettleDate;

    /**
     * 结清说明
     */
    @TableField("settle_remark")
    private String settleRemark;

    @Override
    public void setMainId(Long id) {
        this.contractId = id;
    }

    @Override
    public Long getMainId() {
        return this.contractId;
    }
}
