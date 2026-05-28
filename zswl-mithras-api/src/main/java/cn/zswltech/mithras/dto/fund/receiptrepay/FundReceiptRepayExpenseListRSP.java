package cn.zswltech.mithras.dto.fund.receiptrepay;

import cn.zswltech.mithras.dto.ListBaseRSP;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zhaozhengkang
 * @description 费用一览表
 * @date 2023-02-20
 */
@Data
@ApiModel("费用一览表列表-返回体")
public class FundReceiptRepayExpenseListRSP extends ListBaseRSP {

    @ApiModelProperty(value = "主键")
    private Long id;

    @ApiModelProperty(value = "收付款id")
    private Long receiptRepayId;

    @ApiModelProperty(value = "费用编号")
    private String cashFlowCode;

    @ApiModelProperty(value = "费用类型")
    private String expenseType;

    @ApiModelProperty(value = "金额")
    private Long totalAmount;

    @ApiModelProperty(value = "累计已支付金额")
    private Long totalPaidAmount;

    @ApiModelProperty(value = "本月支付金额")
    private Long payAmount;

    @ApiModelProperty(value = "核销状态")
    private String writeOffState;

    @ApiModelProperty(value = "备注")
    private String remark;

}
