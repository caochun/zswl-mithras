package cn.zswltech.mithras.dto.interestPay;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author dingqi
 * @date 2025/12/8
 * @description
 */
@Data
public class InterestPayCalDetailModifyREQ {
    @NotNull(message = "id不能为空")
    @ApiModelProperty("id")
    private Long id;

    @NotNull(message = "期末计提利息余额不能为空")
    @ApiModelProperty("期末计提利息余额")
    private Long endOfPeriodInterestBalance;

    @NotNull(message = "钆差金额不能为空")
    @ApiModelProperty("钆差金额")
    private Long financingCostDiff;
}
