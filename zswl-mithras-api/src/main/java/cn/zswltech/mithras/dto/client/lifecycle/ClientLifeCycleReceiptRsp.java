package cn.zswltech.mithras.dto.client.lifecycle;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/6/25 14:56
 */
@ApiModel("客户全周期借据详情返回")
@Data
public class ClientLifeCycleReceiptRsp {

    @ApiModelProperty("逾期金额")
    private Long overdueAmount;

    @ApiModelProperty("借据列表")
    private List<ReceiptListRsp> receiptPage;
}
