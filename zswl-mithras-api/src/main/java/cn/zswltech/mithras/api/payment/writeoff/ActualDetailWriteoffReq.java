package cn.zswltech.mithras.api.payment.writeoff;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2022/8/18 13:41
 */
@ApiModel("付款记录核销请求-入参")
@Data
public class ActualDetailWriteoffReq {

    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty("核销状态")
    private String writeOffStatus;
}
