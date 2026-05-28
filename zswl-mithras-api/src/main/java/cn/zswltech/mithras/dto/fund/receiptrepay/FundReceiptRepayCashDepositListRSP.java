package cn.zswltech.mithras.dto.fund.receiptrepay;

import cn.zswltech.mithras.dto.ListBaseRSP;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zhaozhengkang
 * @description 保证金明细
 * @date 2023-02-20
 */
@Data
@ApiModel("保证金明细列表-返回体")
public class FundReceiptRepayCashDepositListRSP extends ListBaseRSP {

    @ApiModelProperty(value = "主键")
    private Long id;

    @ApiModelProperty(value = "收付款id")
    private Long receiptRepayId;
    @ApiModelProperty(value = "费用编号")
    private String cashFlowCode;

    @ApiModelProperty(value = "费用类型")
    private String depositCashFlowType;

    @ApiModelProperty(value = "金额")
    private Long amount;

    @ApiModelProperty(value = "核销状态")
    private String writeOffState;

    @ApiModelProperty(value = "本月支付金额")
    private Long paidAmount;

    @ApiModelProperty(value = "本月收入金额")
    private Long receiptAmount;

    @ApiModelProperty(value = "备注")
    private String remark;

}
