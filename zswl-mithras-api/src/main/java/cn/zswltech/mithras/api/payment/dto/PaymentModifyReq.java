package cn.zswltech.mithras.api.payment.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2022/8/12 17:16
 */
@ApiModel("付款申请修改-入参")
@Data
public class PaymentModifyReq {
    @ApiModelProperty("id")
    private Long id;
    @ApiModelProperty("申请付款日期")
    @NotNull(message = "申请付款日期 为null")
    private LocalDate applyPaymentDate;
    @ApiModelProperty("申请付款金额")
    @NotNull(message = "申请付款金额 为null")
    private Long applyPaymentAmount;
    @ApiModelProperty("保证金")
    @NotNull(message = "保证金 为null")
    private Long earnestMoney;
    @ApiModelProperty("首期租金")
    @NotNull(message = "首期租金 为null")
    private Long downPayment;
    @ApiModelProperty("服务费/咨询费")
    @NotNull(message = "服务费/咨询费 为null")
    private Long consultingFee;
    @ApiModelProperty("币种")
    private String leasedCurrency;
    @ApiModelProperty("租赁财产价值")
    private Long leasedPrice;
    @ApiModelProperty("手续费(元)")
    private Long commission;
    @ApiModelProperty("首期利息(元)")
    private Long firstInstallmentInterest;
    @ApiModelProperty("名义价款")
    @NotNull(message = "名义价款 为null")
    private Long nominalPrice;
    @ApiModelProperty("最低irr")
    private Integer lowestIrr;
    @ApiModelProperty("备注说明")
    private String remark;
    @ApiModelProperty("首付款标志，0不包括，1包括")
    private Integer downPaymentType;
    @ApiModelProperty("质保金")
    private Long retentionMoney;
    @ApiModelProperty("质保金，0内扣，1不内扣")
    private Integer retentionMoneyType;
    @ApiModelProperty("付款计划列表")
    private List<PlanedDetailDto> details;
}
