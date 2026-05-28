package cn.zswltech.mithras.dto.fund.receiptrepay;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zhaozhengkang
 * @description 费用一览表
 * @date 2023-02-20
 */
@Data
@ApiModel("费用一览表列表-请求体")
public class FundReceiptRepayExpenseListREQ extends PageReq {
    @ApiModelProperty(value = "收付款id")
    private Long receiptRepayId;
}
