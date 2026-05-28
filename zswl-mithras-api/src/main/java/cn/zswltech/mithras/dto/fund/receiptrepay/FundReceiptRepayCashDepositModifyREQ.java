package cn.zswltech.mithras.dto.fund.receiptrepay;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zhaozhengkang
 * @description 保证金明细
 * @date 2023-02-20
 */
@Data
@ApiModel("保证金明细编辑-请求体")
public class FundReceiptRepayCashDepositModifyREQ {

    @ApiModelProperty(value = "主键")
    private Long id;

    @ApiModelProperty(value = "本月支付金额")
    private Long paidAmount;
    
    @ApiModelProperty(value = "本月收入金额")
    private Long receiptAmount;

    @ApiModelProperty(value = "备注")
    private String remark;

}
