package cn.zswltech.mithras.dto.fund.financing.fee;

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
@ApiModel("直接融资-请求体")
public class FundFinancingFeeSingleIdREQ {

    @ApiModelProperty(value = "id")
    @NotNull(message = "id不能为空")
    private Long id;

}
