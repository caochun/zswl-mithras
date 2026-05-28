package cn.zswltech.mithras.dto.fund.receiptrepay;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zhaozhengkang
 * @description 借款流入
 * @date 2023-02-20
 */
@Data
@ApiModel("借款流入列表-请求体")
public class FundReceiptRepayBorrowingListREQ extends PageReq {
    @ApiModelProperty(value = "收付款id")
    private Long receiptRepayId;
}
