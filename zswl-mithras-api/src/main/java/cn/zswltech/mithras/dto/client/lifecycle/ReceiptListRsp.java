package cn.zswltech.mithras.dto.client.lifecycle;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/6/25 14:58
 */
@ApiModel("客户全周期借据列表返回")
@Data
public class ReceiptListRsp {

    @ApiModelProperty("借据号")
    private String receiptCode;

    @ApiModelProperty("合同id")
    private Long contractId;

}
