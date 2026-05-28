package cn.zswltech.mithras.dto.fund.financing.earlysettle;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author dingqi
 * @date 2023/2/23
 * @description
 */
@Data
@ApiModel("融资管理-提前结清-保存结清方案-请求体")
public class FundFinancingEarlySettlePlanSaveREQ {
    @ApiModelProperty("id")
    private Long id;

    @NotNull(message = "融资id不能为空")
    @ApiModelProperty("融资id")
    private Long financingId;

    @NotNull(message = "提前偿还金额不能为空")
    @ApiModelProperty("提前偿还金额")
    private Long earlyRepayAmount;

    @NotNull(message = "提前偿还本金不能为空")
    @ApiModelProperty("提前偿还本金金额")
    private Long earlyPrincipleAmount;

    @ApiModelProperty("提前偿还利息金额")
    private Long earlyInterestAmount;

    @ApiModelProperty("违约金金额")
    private Long liquidatedDamagesAmount;

    @ApiModelProperty("其他费用金额")
    private Long otherFeeAmount;

    @ApiModelProperty("违约全减免金额")
    private Long remissionAmount;

    @ApiModelProperty("剩余本金")
    private Long lastPrincipal;

    @ApiModelProperty("提前结清原因")
    @NotBlank(message = "提前结清原因不能为空")
    private String reason;
}
