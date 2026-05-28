package cn.zswltech.mithras.dto.contract.prepayment;

import cn.zswltech.mithras.dto.ListBaseRSP;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@Data
public class ContractPrepaymentDetailRSP extends ListBaseRSP {

    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty("主合同id")
    private Long contractId;

    @ApiModelProperty("是否提前结清，0-否，1-是")
    private Integer isEarlySettle;

    @ApiModelProperty("保证金是否抵扣，0-否，1-是")
    private Integer isEarnestMoneyDeduction;

    @ApiModelProperty("保证金余额")
    private Long earnestMoneyBalance;

    @ApiModelProperty("保证金抵扣金额")
    private Long earnestMoneyDeductionAmount;

    @ApiModelProperty(value = "申请还款日期")
    private LocalDate applayRepaymentDate;

    @ApiModelProperty(value = "到期未付租金")
    private Long unpaidRentDue;

    @ApiModelProperty("未到期本金")
    private Long beforeMaturityPrincipal;

    @ApiModelProperty("未到期利息")
    private Long beforeMaturityInterest;

    @ApiModelProperty(value = "违约金")
    private Long penalty;

    @ApiModelProperty("违约金减免方式")
    private String penaltyDerateType;

    @ApiModelProperty("违约金减免百分比")
    private Integer penaltyDeratePercent;

    @NotNull(message = "<违约金减免金额>不能为空")
    @ApiModelProperty("违约金减免金额")
    private Long penaltyDerateAmount;

    @ApiModelProperty(value = "提前归还本金")
    private Long earlyRepayment;

    @ApiModelProperty(value = "提前归还利息")
    private Long earlyRepaymentInterest;

    @ApiModelProperty(value = "提前终止补偿金")
    private Long loss;

    @ApiModelProperty("提前终止补偿金减免方式")
    private String lossDerateType;

    @ApiModelProperty("提前终止补偿金减免百分比")
    private Integer lossDeratePercent;

    @ApiModelProperty(value = "提前终止补偿金减免金额")
    private Long applyDerateAmount;

    @ApiModelProperty("名义价款")
    private Long nominalPrice;

    @Deprecated
    @ApiModelProperty(value = "名义价款申请还款日期（字段废弃）")
    private LocalDate nominalPriceDate;

    @ApiModelProperty(value = "说明")
    private String remark;

}