package cn.zswltech.mithras.dto.fund.receiptrepay;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zhaozhengkang
 * @description 本金利息一览表
 * @date 2023-02-20
 */
@Data
@ApiModel("本金利息一览表编辑-请求体")
public class FundReceiptRepayCashFlowModifyREQ {
    
    @ApiModelProperty(value = "主键")
    private Long id;

    @ApiModelProperty(value = "备注")
    private String remark;

}
