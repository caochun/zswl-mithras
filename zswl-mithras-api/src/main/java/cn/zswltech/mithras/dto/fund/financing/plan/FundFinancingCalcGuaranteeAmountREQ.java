package cn.zswltech.mithras.dto.fund.financing.plan;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author dingqi
 * @date 2023/2/28
 * @description
 */
@Data
@ApiModel("融资管理-融资方案-担保费计算-请求体")
public class FundFinancingCalcGuaranteeAmountREQ {
    @ApiModelProperty("融资金额")
    @NotNull(message = "融资金额不能为空")
    private Long financingAmount;

    @ApiModelProperty("担保比例")
    @NotNull(message = "担保比例不能为空")
    private Integer guaranteeRate;

    @ApiModelProperty("融资期限")
    @NotNull(message = "融资期限不能为空")
    private Integer financingMonth;
}
