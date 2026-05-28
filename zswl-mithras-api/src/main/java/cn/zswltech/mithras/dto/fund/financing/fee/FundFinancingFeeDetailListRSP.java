package cn.zswltech.mithras.dto.fund.financing.fee;

import cn.zswltech.mithras.api.common.PageR;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zhaozhengkang
 * @description 直接融资-费用明细
 * @date 2023-06-17
 */
@Data
@ApiModel("直接融资-费用明细列表-返回体")
@Deprecated
public class FundFinancingFeeDetailListRSP{

    @ApiModelProperty(value = "融资金额")
    private Long financingAmount;

    @ApiModelProperty(value = "票面加权平均利率")
    private Long averageCouponRate;

    @ApiModelProperty(value = "费用合计")
    private Long totalFee;

    @ApiModelProperty(value = "费用明细")
    private PageR<FundFinancingFeeDetailRSP> page;

}
