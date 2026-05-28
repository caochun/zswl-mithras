package cn.zswltech.mithras.api.payment.writeoff;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2022/8/19 14:05
 */
@ApiModel("核销记录查询-入参")
@Data
public class PaymentWriteOffHistoryListReq extends PageReq {

    @ApiModelProperty("申请id")
    private Long paymentId;
}
