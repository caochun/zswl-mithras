package cn.zswltech.mithras.dto.fund.financing.baseinfo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author dingqi
 * @date 2023/2/24
 * @description
 */
@Data
public class FundFinancingCalcRemainingGuaranteeAmountREQ {
    @ApiModelProperty("融资id")
    @NotNull(message = "融资id不能为空")
    private Long financingId;

    @ApiModelProperty("担保机构id")
    @NotNull(message = "担保机构id不能为空")
    private Long guaranteeAgencyId;

    @ApiModelProperty("填写的担保金额")
    @NotNull(message = "担保金额不能为空")
    private Long guaranteeAmount;
}
