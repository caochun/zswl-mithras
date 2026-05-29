package cn.zswltech.mithras.service.mapper.model.fund.financing;

import cn.zswltech.mithras.service.enums.contract.LPRTypeEnum;
import cn.zswltech.mithras.service.enums.contract.RepayRateEnum;
import cn.zswltech.mithras.service.enums.fund.financing.LprAdjustmentDayEnum;
import cn.zswltech.mithras.service.enums.fund.financing.LprArrangeModeEnum;
import cn.zswltech.mithras.service.enums.projestablish.RateType;
import cn.zswltech.mithras.common.model.BaseModel;
import cn.zswltech.mithras.common.model.IEntity;
import cn.zswltech.mithras.common.annotation.IncludeNull;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@TableName("fund_financing_plan")
public class FundFinancingPlan extends BaseModel implements IEntity {
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
     * 融资金额
     */
    @TableField("financing_amount")
    private Long financingAmount;
    /**
     * 预计利息金额
     */
    @TableField("interest_amount")
    private Long interestAmount;
    /**
     * 融资期限（月）
     */
    @TableField("financing_month")
    private Integer financingMonth;
    /**
     * 保理手续费
     */
    @IncludeNull
    @TableField("service_charge_amount")
    private Long serviceChargeAmount;
    /**
     * 还款期数
     */
    @TableField("repay_times")
    private Integer repayTimes;
    /**
     * 保证金
     */
    @IncludeNull
    @TableField("earnest_money_amount")
    private Long earnestMoneyAmount;
    /**
     * 还款频率 {@link RepayRateEnum}
     */
    @TableField("repay_frequency")
    private String repayFrequency;
    /**
     * 开证许可证费用
     */
    @IncludeNull
    @TableField("license_amount")
    private Long licenseAmount;
    /**
     * 还款方式
     */
    @TableField("repay_way")
    private String repayWay;
    /**
     * 其他费用
     */
    @IncludeNull
    @TableField("other_amount")
    private Long otherAmount;
    /**
     * 担保费信息 {@link GuaranteeAmountInfo}
     */
    @IncludeNull
    @TableField("guarantee_amount_info")
    private String guaranteeAmountInfo;
    /**
     * 担保信息 {@link GuaranteeInfo}
     */
    @IncludeNull
    @TableField("guarantee_info")
    private String guaranteeInfo;

    /**
     * FTP收益率
     */
    @TableField("ftp_yield_rate")
    private Integer ftpYieldRate;

    /**
     * 借款年利率类型 {@link RateType}
     */
    @TableField("interest_rate_type")
    private String interestRateType;
    /**
     * LPR品种 {@link LPRTypeEnum}
     */
    @TableField("lpr_type")
    private String lprType;
    /**
     * LPR利率
     */
    @TableField("lpr_rate_percent")
    private Integer lprRatePercent;
    /**
     * LPR加点
     */
    @TableField("lpr_add_percent")
    private Integer lprAddPercent;

    /**
     * LPR调整方式
     * {@link LprArrangeModeEnum#name()}
     */
    @TableField("lpr_arrange_mode")
    private String lprArrangeMode;

    /**
     * LPR调整日
     * {@link LprAdjustmentDayEnum#name()}
     */
    @TableField("lpr_adjustment_day")
    private String lprAdjustmentDay;

    /**
     * 综合借款年利率-当前使用，每个月查询，尝试更新
     */
    @IncludeNull
    @TableField("comprehensive_interest_rate_current")
    private Integer comprehensiveInterestRateCurrent;

    /**
     * 综合借款年利率
     */
    @IncludeNull
    @TableField("comprehensive_interest_rate")
    private Integer comprehensiveInterestRate;

//    @TableField("comprehensive_financing_cost")
//    private Long comprehensiveFinancingCost;

//    @TableField("contract_rate")
//    private Long contractRate;

    /**
     * 还款日
     */
    @IncludeNull
    @TableField("repay_day")
    private Integer repayDay;

    @Override
    public void setMainId(Long id) {
        this.financingId = id;
    }

    @Override
    public Long getMainId() {
        return this.financingId;
    }

    @Data
    public static class GuaranteeAmountInfo {
        /**
         * 融资机构id
         */
        private Long organizationId;
        /**
         * 融资机构name
         */
        private String organizationName;
        /**
         * 担保机构id
         */
        private Long guaranteeAgencyId;
        /**
         * 担保机构名称
         */
        private String guaranteeAgencyName;
        /**
         * 担保费率
         */
        private Integer guaranteeFeeRate;
        /**
         * 担保费
         */
        private Long guaranteeFeeAmount;
    }

    @Data
    public static class GuaranteeInfo {
        /**
         * 融资机构id
         */
        private Long organizationId;
        /**
         * 融资机构name
         */
        private String organizationName;
        /**
         * 担保机构id
         */
        private Long guaranteeAgencyId;
        /**
         * 担保机构名称
         */
        private String guaranteeAgencyName;
        /**
         * 担保金额
         */
        private Long guaranteeAmount;
    }

}