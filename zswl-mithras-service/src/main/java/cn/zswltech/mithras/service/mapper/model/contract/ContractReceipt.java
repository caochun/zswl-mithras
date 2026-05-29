package cn.zswltech.mithras.service.mapper.model.contract;

import cn.zswltech.mithras.common.model.BaseModel;
import cn.zswltech.mithras.common.model.IEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * @author dingqi
 * @date 2022/8/20
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ContractReceipt extends BaseModel implements IEntity {
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
     * 付款申请编号
     */
    @Deprecated
    @TableField("payment_apply_code")
    private String paymentApplyCode;

    /**
     * 借据编号
     */
    @TableField("receipt_code")
    private String receiptCode;

    /**
     * 序列号
     */
    @TableField("sequence")
    private Integer sequence;

    /**
     * 实际irr
     */
    @TableField("actual_irr")
    private Integer actualIrr;

    /**
     * 不含税租金（元）
     */
    @TableField("rent_excluding_tax")
    private Long rentExcludingTax;

    /**
     * 税率
     */
    @TableField("tax_rate")
    private Long taxRate;

    /**
     * 税额（元）
     */
    @TableField("tax")
    private Long tax;

    /**
     * 不含税利息
     */
    @TableField("excluding_interest_tax")
    private Long excludingInterestTax;

    /**
     * 融资租赁合同印花税税率
     */
    @TableField("stamp_duty_tax_rate_zl")
    private BigDecimal stampDutyTaxRateZL;

    /**
     * 买卖合同印花税税率
     */
    @TableField("stamp_duty_tax_rate_mm")
    private BigDecimal stampDutyTaxRateMM;

    /**
     * 已核销的投放款(仅直租)
     */
    @TableField("actual_pay_amount")
    private Long actualPayAmount;

    /**
     * 已核销的手续费/咨询服务费（不含税）
     */
    @TableField("actual_service_fee_without_tax")
    private Long actualServiceFeeWithoutTax;

    /**
     * 印花税-融资租赁合同
     */
    @TableField("stamp_duty_zl")
    private Long stampDutyZL;

    /**
     * 印花税-买卖合同（仅直租）
     */
    @TableField("stamp_duty_mm")
    private Long stampDutyMM;

    /**
     * 印花税（总）
     */
    @TableField("stamp_duty")
    private Long stampDuty;

    /**
     * 借据变更日期
     */
    @TableField("change_date")
    private LocalDate changeDate;

    /**
     * 借据收入确认明细是否已重算，0-未重算，1-已重算
     */
    @TableField("income_sharing_flag")
    private Integer incomeSharingFlag;

    /**
     * 借据起息日期
     */
    @TableField("receipt_start_date")
    private LocalDate receiptStartDate;

    /**
     * 是否是第一个借据，默认为0，0-不是，1-是
     */
    @TableField("is_first_receipt")
    private Integer isFirstReceipt;

    /**
     * 借据创建时取自客户管理的【企业规模】
     **/
    @TableField("org_scale")
    private String orgScale;

    /**
     * xirr
     **/
    @TableField("xirr")
    private Double xirr;

    @Override
    public void setMainId(Long id) {
        this.contractId = id;
    }

    @Override
    public Long getMainId() {
        return contractId;
    }
}
