package cn.zswltech.mithras.dto.fund.receiptrepay;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author zhaozhengkang
 * @description 资金管理-融资管理-我方付款账户
 * @date 2023-02-22
 */
@Data
@ApiModel("资金管理-收付款管理-我方付款账户删除-请求体")
public class FundRepayAccountRemoveREQ {

    @NotNull
    @ApiModelProperty("id")
    private Long id;

}
