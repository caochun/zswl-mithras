package cn.zswltech.mithras.api.payment.writeoff;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2022/8/16 14:09
 */
@ApiModel("查询付款记录列表-入参")
@Data
public class ActualDetailListReq{
    @ApiModelProperty("付款申请id")
    private Long paymentId;

    @ApiModelProperty("付款核销记录状态")
    private List<String> writeOffStatusList;
}
