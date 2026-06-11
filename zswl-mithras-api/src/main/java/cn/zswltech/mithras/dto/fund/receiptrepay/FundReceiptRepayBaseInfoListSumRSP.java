package cn.zswltech.mithras.dto.fund.receiptrepay;

import cn.zswltech.mithras.api.common.PageR;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
@ApiModel("收付款列表-返回体")
public class FundReceiptRepayBaseInfoListSumRSP {

    @ApiModelProperty(value = "融资金额")
    private Long financingAmount;
//    @ApiModelProperty(value = "已还本金")
//    private Long repayPrincipal;
//    @ApiModelProperty(value = "已还利息")
//    private Long repayInterest;
//    @ApiModelProperty(value = "一年内到期本金")
//    private Long oneYearPrincipal;

    @ApiModelProperty(value = "本月待还金额")
    private Long monthRepayAmount;
    @ApiModelProperty(value = "本月应还本金")
    private Long monthRepayPrincipal;
    @ApiModelProperty(value = "本月应还利息")
    private Long monthRepayInterest;

    @ApiModelProperty(value = "列表数据")
    private PageR<FundReceiptRepayBaseInfoListRSP> list;

}
