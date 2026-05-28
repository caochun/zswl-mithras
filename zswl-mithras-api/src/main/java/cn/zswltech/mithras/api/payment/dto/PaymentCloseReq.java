package cn.zswltech.mithras.api.payment.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2022/8/12 16:50
 */
@ApiModel("付款关闭-入参")
@Data
public class PaymentCloseReq {
    @ApiModelProperty("付款申请IDs")
    @NotNull(message = "付款信息为空")
    private List<Long> ids;
}
