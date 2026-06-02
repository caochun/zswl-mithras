package cn.zswltech.mithras.service.mapper.model.contract;

import cn.zswltech.mithras.service.enums.InterestWayEnum;
import cn.zswltech.mithras.service.enums.contract.LPRTypeEnum;
import cn.zswltech.mithras.service.enums.projestablish.RateType;
import cn.zswltech.mithras.service.enums.projestablish.RepayCalcType;
import cn.zswltech.mithras.service.mapper.model.BaseModel;
import cn.zswltech.mithras.service.mapper.tag.IEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @author zhaozhengkang
 * @description 债权转让报价方案
 * @date 2022-08-01
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("contract_aoc_price")
public class ContractAocPrice extends BaseModel implements Serializable, IEntity, ContractPrice {
    private static final long serialVersionUID = -522019421311453111L;

    /**
     * 主键id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 合同id
     */
    @TableField("contract_id")
    private Long contractId;

    /**
     * 合同金额
     */
    @TableField("contract_amount")
    private Long contractAmount;

    /**
     * 额度是否可循环
     */
    @TableField("credit_amount_loop")
    private Integer creditAmountLoop;

    /**
     * 转让额度有效期（月）
     */
    @TableField("aoc_credit_term")
    private Integer aocCreditTerm;

    /**
     * 保证金
     */
    @TableField("earnest_money")
    private Long earnestMoney;

    /**
     * 手续费
     */
    @TableField("consulting_fee")
    private Long consultingFee;

    /**
     * 结构化利息
     */
    @TableField("structured_interest")
    private String structuredInterest;

    /**
     * 转让费率类型 {@link RateType#name()}
     */
    @TableField("rate_type")
    private String rateType;

    /**
     * LPR品种 {@link LPRTypeEnum#name()}
     */
    @TableField("lpr_type")
    private String lprType;

    /**
     * LPR利率
     */
    @TableField("lpr_percent")
    private Integer lprPercent;

    /**
     * LPR加点
     */
    @TableField("lpr_add_percent")
    private Integer lprAddPercent;

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
     * 还款计算方式 {@link RepayCalcType#name()}
     */
    @TableField("repay_calc_type")
    private String repayCalcType;

    /**
     * 利息计算方式{@link InterestWayEnum#name()}
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
        this.contractId = id;
    }

    @Override
    public Long getMainId() {
        return this.contractId;
    }
}
