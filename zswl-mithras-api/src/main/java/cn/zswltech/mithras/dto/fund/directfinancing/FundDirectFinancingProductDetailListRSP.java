package cn.zswltech.mithras.dto.fund.directfinancing;

import cn.zswltech.mithras.api.common.PageR;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zhaozhengkang
 * @description 直接融资-产品明细
 * @date 2023-06-17
 */
@Data
@ApiModel("直接融资-产品明细列表-返回体")
public class FundDirectFinancingProductDetailListRSP {
    @ApiModelProperty(value = "合计信息")
    private FundDirectFinancingProductDetailRSP total;

    @ApiModelProperty(value = "分页list")
    private PageR<FundDirectFinancingProductDetailRSP> page;

}