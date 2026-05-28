package cn.zswltech.mithras.dto.fund.directfinancing;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @description 直接融资-费用明细
 * @author zhaozhengkang
 * @date 2023-06-17
 */
@Data
@ApiModel("直接融资-费用明细删除-请求体")
public class FundDirectFinancingFeeDetailRemoveREQ {

    @NotNull
    @ApiModelProperty("id")
    private Long id;

}
