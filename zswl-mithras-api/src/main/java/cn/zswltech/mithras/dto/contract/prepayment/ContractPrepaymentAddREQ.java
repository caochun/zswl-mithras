package cn.zswltech.mithras.dto.contract.prepayment;

import cn.zswltech.mithras.dto.contract.ContractFlowBasicREQ;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
public class ContractPrepaymentAddREQ extends ContractFlowBasicREQ {
    @NotNull(message = "<是否提前结清>不能为空")
    @ApiModelProperty("是否提前结清，0-否，1-是")
    private Integer isEarlySettle;

    @ApiModelProperty("保证金余额")
    private Long earnestMoneyBalance;

    @ApiModelProperty("保证金是否抵扣，0-否，1-是")
    private Integer isEarnestMoneyDeduction;

    @ApiModelProperty("保证金抵扣金额")
    private Long earnestMoneyDeductionAmount;

    @ApiModelProperty(value = "提前还款日期")
    @NotNull(message = "提前还款日期不能为空")
    private LocalDate applayRepaymentDate;

    @ApiModelProperty(value = "到期未付租金")
    //@NotNull(message = "到期未付租金不能为空")
    private Long unpaidRentDue;

    @ApiModelProperty(value = "未到期本金")
    private Long beforeMaturityPrincipal;

    @ApiModelProperty(value = "未到期利息")
    private Long beforeMaturityInterest;

    @ApiModelProperty(value = "违约金")
    private Long penalty;

    @NotBlank(message = "<违约金减免方式>不能为空")
    @ApiModelProperty("违约金减免方式")
    private String penaltyDerateType;

    @ApiModelProperty("违约金减免百分比")
    private Integer penaltyDeratePercent;

    @ApiModelProperty("违约金减免金额")
    private Long penaltyDerateAmount;

    @ApiModelProperty(value = "提前归还本金")
    //@NotNull(message = "提前归还本金不能为空")
    private Long earlyRepayment;

    @ApiModelProperty(value = "提前归还利息")
    private Long earlyRepaymentInterest;

    @ApiModelProperty(value = "提前终止补偿金")
    private Long loss;

    @NotBlank(message = "<提前终止补偿金减免方式>不能为空")
    @ApiModelProperty("提前终止补偿金减免方式")
    private String lossDerateType;

    @ApiModelProperty("提前终止补偿金减免百分比")
    private Integer lossDeratePercent;

    @NotNull(message = "<提前终止补偿金减免金额>不能为空")
    @ApiModelProperty(value = "提前终止补偿金减免金额")
    private Long applyDerateAmount;

    @ApiModelProperty("名义价款")
    @Min(message = "名义价款必须大于等于0", value = 0)
    //@NotNull(message = "名义价款不能为空")
    private Long nominalPrice;

//    @ApiModelProperty(value = "名义价款申请还款日期")
//    //@NotNull(message = "申请还款日期不能为空")
//    private LocalDate nominalPriceDate;

    @ApiModelProperty(value = "说明")
    private String remark;

}