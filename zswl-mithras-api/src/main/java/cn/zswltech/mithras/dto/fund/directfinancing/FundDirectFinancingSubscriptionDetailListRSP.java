package cn.zswltech.mithras.dto.fund.directfinancing;

import cn.zswltech.mithras.api.common.PageR;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zhaozhengkang
 * @description 直接融资-认购明细
 * @date 2023-06-17
 */
@Data
@ApiModel("直接融资-认购明细列表-返回体")
public class FundDirectFinancingSubscriptionDetailListRSP {

    @ApiModelProperty(value = "合计认购额度（万元）")
    private String total;

    @ApiModelProperty(value = "分页list")
    private PageR<FundDirectFinancingSubscriptionDetailRSP> page;

}