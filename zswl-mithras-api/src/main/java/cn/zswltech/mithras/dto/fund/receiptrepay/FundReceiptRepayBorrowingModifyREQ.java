package cn.zswltech.mithras.dto.fund.receiptrepay;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zhaozhengkang
 * @description 借款流入
 * @date 2023-02-20
 */
@Data
@ApiModel("借款流入编辑-请求体")
public class FundReceiptRepayBorrowingModifyREQ {
    @ApiModelProperty(value = "主键")
    private Long id;
    @ApiModelProperty(value = "备注")
    private String remark;

}
