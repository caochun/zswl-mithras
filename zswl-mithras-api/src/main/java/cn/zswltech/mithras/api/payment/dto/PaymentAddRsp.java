package cn.zswltech.mithras.api.payment.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2022/8/12 16:24
 */
@ApiModel("创建付款申请-出参")
@Data
public class PaymentAddRsp{

    @ApiModelProperty("id")
    private Long id;

}
