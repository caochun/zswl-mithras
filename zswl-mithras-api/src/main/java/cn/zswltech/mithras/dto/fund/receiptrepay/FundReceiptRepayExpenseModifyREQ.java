package cn.zswltech.mithras.dto.fund.receiptrepay;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zhaozhengkang
 * @description 费用一览表
 * @date 2023-02-20
 */
@Data
@ApiModel("费用一览表编辑-请求体")
public class FundReceiptRepayExpenseModifyREQ {

    @ApiModelProperty(value = "主键")
    private Long id;

    @ApiModelProperty(value = "本月支付金额")
    private Long payAmount;

    @ApiModelProperty(value = "核销状态")
    private String writeOffState;

    @ApiModelProperty(value = "备注")
    private String remark;

}
