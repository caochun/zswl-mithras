package cn.zswltech.mithras.dto.fund.receiptrepay;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author zhaozhengkang
 * @description 资金管理-融资管理-对方收款账户
 * @date 2023-02-22
 */
@Data
@ApiModel("资金管理-融资管理-对方收款账户列表-请求体")
public class FundReceiptAccountListREQ extends PageReq {

    @ApiModelProperty(value = "收付款id")
    @NotNull(message = "收付款id不能为空")
    private Long receiptRepayId;

}
