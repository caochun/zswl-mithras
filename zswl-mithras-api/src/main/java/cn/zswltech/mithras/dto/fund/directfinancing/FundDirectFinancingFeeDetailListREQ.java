package cn.zswltech.mithras.dto.fund.directfinancing;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author zhaozhengkang
 * @description 直接融资-费用明细
 * @date 2023-06-17
 */
@Data
@ApiModel("直接融资-费用明细列表-请求体")
public class FundDirectFinancingFeeDetailListREQ extends PageReq {

    @ApiModelProperty(value = "直接融资id")
    @NotNull(message = "直接融资id不能为空")
    private Long financingId;

}
