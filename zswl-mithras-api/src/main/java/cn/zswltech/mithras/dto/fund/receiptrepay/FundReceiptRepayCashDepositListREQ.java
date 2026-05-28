package cn.zswltech.mithras.dto.fund.receiptrepay;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zhaozhengkang
 * @description 保证金明细
 * @date 2023-02-20
 */
@Data
@ApiModel("保证金明细列表-请求体")
public class FundReceiptRepayCashDepositListREQ extends PageReq {
    @ApiModelProperty(value = "收付款id")
    private Long receiptRepayId;
}
