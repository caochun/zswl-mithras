package cn.zswltech.mithras.dto.fund.receiptrepay;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zhaozhengkang
 * @description 收付款
 * @date 2023-02-20
 */
@Data
@ApiModel("收付款编辑-请求体")
public class FundReceiptRepayBaseInfoModifyREQ {
    @ApiModelProperty(value = "主键")
    private Long id;
    @ApiModelProperty(value = "备注")
    private String remark;
}
